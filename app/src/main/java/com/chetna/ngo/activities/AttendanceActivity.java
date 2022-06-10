
package com.chetna.ngo.activities;

import static com.chetna.ngo.utils.Constants.USER_TYPE;
import static com.chetna.ngo.utils.Constants.hideProgressDialog;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.chetna.ngo.R;
import com.chetna.ngo.databinding.ActivityAttendenceBinding;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;
import com.chetna.ngo.utils.GPSTracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AttendanceActivity extends AppCompatActivity {

    private Context context;
    int SELECT_PICTURE = 200;
    private ActivityAttendenceBinding binding;
    private Uri imageUri;
    private String stringLatitude, stringLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAttendenceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;

        getSupportActionBar().setTitle(R.string.attendance);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_backspace_24);

        binding.ivAttendanceImage.setOnClickListener(view -> imageChooser());

        binding.bSubmitAttendance.setOnClickListener(view -> {
            try {
                if (isGpsPermission()) {
                    getLocation();
                }
                updateAttendance();
            } catch (Exception e) {
                Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

    }

    private void imageChooser() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(((Activity) context), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 201);
        } else {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            Uri selectedImageUri = data.getData();
            if (requestCode == SELECT_PICTURE) {
                imageUri = selectedImageUri;
            }
            if (null != selectedImageUri) {
                // update the preview image in the layout
                checkPermission();
                binding.ivAttendanceImage.setImageURI(selectedImageUri);
            }
        }
    }


    private String getRealPathFromURI(Uri contentURI) {
        return Constants.getPath(context, contentURI);
    }

    private File getFileFromPath(String path) {
        try {
            return new File(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void checkPermission() {
        try {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(((Activity) context), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 201);
            }
        } catch (Exception e) {
            Log.e("TAG", "checkPermission: " + e.getLocalizedMessage());
        }
    }

    private boolean isGpsPermission() {
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        }
        if (requestCode == 201 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            imageChooser();
        }
    }

    private void getLocation() {
        GPSTracker gpsTracker = new GPSTracker(this);
        if (gpsTracker.getIsGPSTrackingEnabled()) {
            stringLatitude = String.valueOf(gpsTracker.latitude);

            stringLongitude = String.valueOf(gpsTracker.longitude);
        } else {

            gpsTracker.showSettingsAlert();
        }
    }


    private void updateAttendance() throws Exception {
        Constants.showProgressDialog("Updating Attendance!", "Please Wait....", context);
        MultipartBody.Builder multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (imageUri != null) {
            String path = getRealPathFromURI(imageUri);
            File file = getFileFromPath(path);
            if (file != null) {
                file = Compressor.getDefault(context).compressToFile(file);
                RequestBody requestDlFile = RequestBody.create(file, MediaType.parse(path.substring(getRealPathFromURI(imageUri).lastIndexOf(".") + 1)));
                multipartBody.addFormDataPart("photo", path.substring(path.lastIndexOf("/") + 1), requestDlFile);
            }
        } else {
            Toast.makeText(context, "Please Select Image!", Toast.LENGTH_SHORT).show();
            return;
        }

        multipartBody.addFormDataPart("user_id", Constants.getString(context, Constants.USER_ID));
        multipartBody.addFormDataPart("user_name", Constants.getString(context, Constants.NAME));
        multipartBody.addFormDataPart("type", Constants.getString(context, Constants.USER_TYPE));
        multipartBody.addFormDataPart("location", stringLatitude + " , " + stringLongitude);
        multipartBody.addFormDataPart("working_area_id", getIntent().getStringExtra("working_area_id"));

        MultipartBody requestBody = multipartBody.build();
        String url = BaseUrls.ATTENDANCE;
        if (Constants.getString(AttendanceActivity.this, USER_TYPE).equalsIgnoreCase("3")) {
            url = BaseUrls.ATTENDANCE_OF_WORKING_AREA;
        }

        attemptPost(requestBody, url);
        //throw new Exception("device error");
    }

    private void attemptPost(final RequestBody requestBody, String url) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {

                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .header("Accept", "application/json")
                            .url(url)
                            .post(requestBody)
                            .build();
                    Response serverresponse = Constants.getClient().newCall(request).execute();
                    String response = serverresponse.body().string();
                    Constants.hideProgressDialog();
                    Log.e("TAG", "doInBackground: " + response);
                    if (response.isEmpty()) {
                        Handler handler = new Handler(getMainLooper());
                        handler.post(() -> Constants.showToast(context, "Some thing went wrong Try Again..."));
                    } else {
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            boolean status = jObject.getBoolean("status");
                            if (status) {
                                runOnUiThread(() -> {
                                    hideProgressDialog();
                                    Constants.showToast(AttendanceActivity.this, "Attendance Submit Successfully");
                                });
                            } else {
                                runOnUiThread(() -> {
                                    try {
                                        String message = jObject.getString("message");
                                        hideProgressDialog();
                                        Constants.showToast(context, message);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        runOnUiThread(() -> hideProgressDialog());
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            runOnUiThread(() -> hideProgressDialog());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> hideProgressDialog());
                }
                return null;
            }
        }.execute();
    }
}