package com.chetna.ngo.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.chetna.ngo.R;
import com.chetna.ngo.databinding.ActivityMainBinding;
import com.chetna.ngo.fragments.AdminFragment;
import com.chetna.ngo.fragments.FrontLineWorkerFragment;
import com.chetna.ngo.fragments.ProjectCordinatorFragment;
import com.chetna.ngo.fragments.PublicFragment;
import com.chetna.ngo.fragments.StreetCordinatorFragment;
import com.chetna.ngo.utils.Constants;
import com.chetna.ngo.utils.GPSTracker;
import com.chetna.ngo.utils.MyWorker;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private Context context;
    ActionBarDrawerToggle actionBarDrawerToggle;
    private ActivityMainBinding binding;
    boolean isLocationOn = false;
    GPSTracker gpsTracker;
    String TAG = "LOCATION_UPDATE";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = this;

        getSupportActionBar().setTitle("Home");

        gpsTracker = new GPSTracker(context);

        View navView = binding.navigationView.getHeaderView(0);

        navView.findViewById(R.id.textChangeLanguage).setOnClickListener(view -> {
                    alertDailog();
                }
        );


        navView.findViewById(R.id.logoutLyt).setOnClickListener(view -> {
            Log.e("TAG", "onCreate: ");
            Constants.setString(context, Constants.USER_ID, "");
            Constants.setString(context, Constants.PAN_CARD, "");
            Constants.setString(context, Constants.BLOOD_GROUP, "");
            Constants.setString(context, Constants.BANK_NAME, "");
            Constants.setString(context, Constants.AC_NO, "");
            Constants.setString(context, Constants.IFSC, "");
            Constants.setString(context, Constants.NAME_ON_BANK, "");

            startActivity(new Intent(context, LoginActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        });
        navView.findViewById(R.id.ourStory)
                .setOnClickListener(view -> startActivity(new Intent(context, OurStoryActivity.class)));

        navView.findViewById(R.id.ourApproach)
                .setOnClickListener(view -> startActivity(new Intent(context, OurApproachActivity.class)));

        navView.findViewById(R.id.suggesstion)
                .setOnClickListener(view -> startActivity(new Intent(context, AddSuggestionsActivity.class)));
        navView.findViewById(R.id.completeProfile)
                .setOnClickListener(view -> startActivity(new Intent(context, UploadDocuments.class)));

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, binding.drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        ((TextView) navView.findViewById(R.id.tvUserName)).setText("Welcome :" + Constants.getString(context, Constants.NAME));
        ((TextView) navView.findViewById(R.id.tvUserEmail)).setText(Constants.getString(context, Constants.EMAIL));
        navView.findViewById(R.id.tvUserEmail).setVisibility(View.GONE);
        if (Constants.getString(context, Constants.USER_TYPE).equals(Constants.USER_TYPE_ADMIN)) {
            navView.findViewById(R.id.lytProfile).setVisibility(View.GONE);
        }
        if (Constants.getString(context, Constants.USER_TYPE).equals(Constants.USER_TYPE_ADMIN)
                || Constants.getString(context, Constants.USER_TYPE).equals(Constants.USER_TYPE_SUB_ADMIN)) {
            findViewById(R.id.locationLyt).setVisibility(View.GONE);
        }
        navView.findViewById(R.id.lytProfile).
                setOnClickListener(view -> startActivity(new Intent(context, ViewUserProfileActivity.class)
                        .putExtra("userId", Constants.getString(context, Constants.USER_ID))
                        .putExtra("type", Constants.getString(context, Constants.USER_TYPE))));

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AppCompatButton bLocation = findViewById(R.id.bLocation);

        try {
            if (isWorkScheduled(WorkManager.getInstance(context).getWorkInfosByTag(TAG).get())) {
                isLocationOn = true;
                binding.bLocation.setText("ON");
            } else {
                isLocationOn = false;
                binding.bLocation.setText("OFF");
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        binding.bLocation.setOnClickListener(view -> {
            if (!isLocationOn) {
                isLocationOn = true;
                bLocation.setText("ON");
            } else {
                isLocationOn = false;
                bLocation.setText("OFF");
            }
            getLocation();
        });

        if (Constants.getString(context, Constants.USER_TYPE).equals(Constants.USER_TYPE_PUBLIC))
            replaceFragment(new PublicFragment());
        if (Constants.getString(context, Constants.USER_TYPE).equals(Constants.USER_TYPE_FRONT_LINE_WORKER))
            replaceFragment(new FrontLineWorkerFragment());
        if (Constants.getString(context, Constants.USER_TYPE).equals(Constants.USER_TYPE_PROJECT_CO_ORDINATOR))
            replaceFragment(new ProjectCordinatorFragment());
        if (Constants.getString(context, Constants.USER_TYPE).equals(Constants.USER_TYPE_STREET_CO_ORDINATOR))
            replaceFragment(new StreetCordinatorFragment());
        if (Constants.getString(context, Constants.USER_TYPE).equals(Constants.USER_TYPE_ADMIN) || Constants.getString(context, Constants.USER_TYPE).equals(Constants.USER_TYPE_SUB_ADMIN)) {
            replaceFragment(new AdminFragment());
        }
    }


    private void getLocation() {
        if (checkPermission()) {
            if (gpsTracker.getIsGPSTrackingEnabled()) {
                setLocationUpdate(isLocationOn);
            }
        } else {
            gpsTracker.showSettingsAlert();
        }

    }

    private boolean checkPermission() {
        boolean isGranted = false;
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermission();
            } else {
                isGranted = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isGranted;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
    }

    final PeriodicWorkRequest periodicWorkRequest
            = new PeriodicWorkRequest.Builder(MyWorker.class, 5, TimeUnit.MINUTES)
            .addTag(TAG)
            .build();


    private void setLocationUpdate(boolean isLocationOn) {
        try {
            if (isLocationOn) {
                WorkManager.getInstance(context).enqueueUniquePeriodicWork("location",
                        ExistingPeriodicWorkPolicy.REPLACE,
                        periodicWorkRequest);
            } else {
                // binding.bLocation.setText("OFF");
                WorkManager.getInstance(context).cancelWorkById(periodicWorkRequest.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isWorkScheduled(List<WorkInfo> workInfos) {
        boolean running = false;
        if (workInfos == null || workInfos.size() == 0) return false;
        for (WorkInfo workStatus : workInfos) {
            running = workStatus.getState() == WorkInfo.State.RUNNING | workStatus.getState() == WorkInfo.State.ENQUEUED;
        }
        return running;
    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.mainFrame, fragment);
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void alertDailog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Change Language")
                .setCancelable(false)
                .setPositiveButton("English", (dialogInterface, i) -> {
                    Toast.makeText(context, "English language choose", Toast.LENGTH_SHORT).show();
                    changeLanguage("en");

                });
        builder.setNegativeButton("Hindi", (dialogInterface, i) -> {
            //Toast.makeText(context, "Hindi language choose", Toast.LENGTH_SHORT).show();
            changeLanguage("hi");
        });
        builder.show();

    }

    private void changeLanguage(String lang) {
        Constants.setString(context, Constants.LANG, lang);
        Constants.setLanguage(context, Constants.getString(context, Constants.LANG));
        recreate();
    }


}