package com.chetna.ngo.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chetna.ngo.R;
import com.chetna.ngo.adapters.SliderAdapterExample;
import com.chetna.ngo.models.SliderItem;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    private Context context;
    private TextView tvAdminMessage;
    SliderView sliderView;
    SliderAdapterExample adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();

        context = this;

        LinearLayout Lyt = findViewById(R.id.adminLyt);
        tvAdminMessage = Lyt.findViewById(R.id.marqueText);
        sliderView = Lyt.findViewById(R.id.imageSlider);
        loadSlider();
        getAdminMessage();

        findViewById(R.id.lytAbout)
                .setOnClickListener(view -> startActivity(new Intent(context, AboutUsActivity.class)));
        findViewById(R.id.lytPartner)
                .setOnClickListener(view -> startActivity(new Intent(context, TrustiesActivity.class)));
        findViewById(R.id.lytVolenteer)
                .setOnClickListener(view -> startActivity(new Intent(context, BeAVolenteerActivity.class)));
        findViewById(R.id.lytMembers)
                .setOnClickListener(view -> startActivity(new Intent(context, LoginActivity.class)));
        findViewById(R.id.lytFeedback)
                .setOnClickListener(view -> startActivity(new Intent(context, AddSuggestionsActivity.class)));
        findViewById(R.id.bNotifications)
                .setOnClickListener(view -> startActivity(new Intent(context, RewardsActivity.class)));
        findViewById(R.id.lytCaseHistory)
                .setOnClickListener(view -> startActivity(new Intent(context, CaseHistoryActivity.class)));
        findViewById(R.id.lytEvents)
                .setOnClickListener(view -> startActivity(new Intent(context, EventsActivity.class)));
        findViewById(R.id.galleryLyt)
                .setOnClickListener(view -> startActivity(new Intent(context, ProjectListActivity.class)
                        .putExtra("from", "g")));

        findViewById(R.id.changeLang)
                .setOnClickListener(view -> alertDailog());

    }

    private void loadSlider() {
        adapter = new SliderAdapterExample(context);
        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
        sliderView.startAutoCycle();
        getSliderData();
    }

    private void getSliderData() {
        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.GET_SLIDER_IMAGE, response -> {
            Log.e("TAG", "changeMessage: " + response);
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
                JSONArray requestsData;
                requestsData = jsonObject.getJSONArray("data");
                if (requestsData.length() > 0) {
                    for (int i = 0; i < requestsData.length(); i++) {
                        JSONObject object = requestsData.getJSONObject(i);
                        SliderItem model = new SliderItem(object.getString("text"), object.getString("image"), object.getString("id"), object.getString("text"), object.getString("project_id"));
                        adapter.addItem(model);
                    }
                }
                Constants.showToast(context, jsonObject.getString("message"));
            } catch (JSONException e) {
                e.printStackTrace();
                Constants.hideProgressDialog();
            }

        }, error -> {
            Constants.hideProgressDialog();
            if (error instanceof NetworkError || error instanceof TimeoutError) {
                Constants.showToast(context, "Please Check your Internet connection");
                return;
            }
            Constants.showToast(context, error.getLocalizedMessage());
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("project_id", "0");
                Log.e("PARAMS", "getParams: " + params);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        request.setRetryPolicy(new
                DefaultRetryPolicy(
                Constants.CONNECTION_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);

    }


    private void getAdminMessage() {
        Constants.showProgressDialog("Loading Data!", "Please Wait.....", context);
        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.GET_ADMIN_MESSAGE, response -> {
            Log.e("TAG", "getAdminMessage: " + response);
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
                Constants.showToast(context, jsonObject.getString("message"));
                if (jsonObject.getBoolean("status")) {
                    JSONObject userData = jsonObject.getJSONObject("data");
                    tvAdminMessage.setText(userData.getString("message").toString());

                    Log.e("TAG", "getAdminMessage: " + userData);
                }
            } catch (JSONException e) {
                Log.e("TAG", "getAdminMessage: " + e.getLocalizedMessage());
            }
        }, error -> {
            Constants.hideProgressDialog();
            if (error instanceof NetworkError || error instanceof TimeoutError) {
                Constants.showToast(context, "Please Check your Internet connection");
                return;
            }
            Constants.showToast(context, error.getLocalizedMessage());
        });
        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        request.setRetryPolicy(new
                DefaultRetryPolicy(
                Constants.CONNECTION_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);


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