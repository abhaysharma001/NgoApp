package com.chetna.ngo.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chetna.ngo.R;
import com.chetna.ngo.adapters.ReportingAdapter;
import com.chetna.ngo.databinding.ActivityViewUserReportingBinding;
import com.chetna.ngo.models.PostModel;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ViewUserReportingActivity extends AppCompatActivity {

    private Context context;
    private ActivityViewUserReportingBinding binding;
    private ArrayList<PostModel> postModels = new ArrayList<>();
    private ReportingAdapter reportingAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewUserReportingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;

        getSupportActionBar().setTitle(R.string.view_reports);
        binding.postRecyclerView.setVisibility(View.VISIBLE);
        binding.postRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        reportingAdapter = new ReportingAdapter(postModels, context, Constants.isReporting);
        binding.postRecyclerView.setAdapter(reportingAdapter);
        loadReporting();

        binding.tvEndDate.setOnClickListener(view -> showDatePicker(false));
        binding.tvStartDate.setOnClickListener(view -> showDatePicker(true));


        binding.bSearch.setOnClickListener(view -> {
            String startDate,endDate;
            startDate = binding.tvStartDate.getText().toString();
            endDate = binding.tvEndDate.getText().toString();

            if (startDate.isEmpty()){
                Toast.makeText(context, "Please select start date!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (endDate.isEmpty()){
                Toast.makeText(context, "Please select end date!", Toast.LENGTH_SHORT).show();
                return;
            }
            startDate = startDate.replace("/","-");
            endDate = endDate.replace("/","-");
            loadReportingByDate(startDate,endDate);

        });

        binding.bLoadAllReports.setOnClickListener(view -> loadReporting());

    }

    private void loadReportingByDate(String startDate,String endDate){
        postModels.clear();
        Constants.showProgressDialog("Loading Reports", "Please Wait...", context);
        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.get_reporting_from_to_date, response -> {
            Log.e("TAG", "loadReporting: " + response);
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
                Constants.showToast(context, jsonObject.getString("message"));
                JSONArray requestsData = jsonObject.getJSONArray("data");
                ArrayList<String> savedList = Constants.getSavedPostList(context);
                if (requestsData.length() > 0) {
                    binding.noDataLayout.setVisibility(View.GONE);
                }else {
                    binding.bLoadAllReports.setVisibility(View.VISIBLE);
                }
                for (int i = 0; i < requestsData.length(); i++) {
                    JSONObject object = requestsData.getJSONObject(i);
                    PostModel model = new PostModel();
                    if (object.has("id")) {
                        model.setId(object.getString("id"));
                        if (savedList.contains(object.getString("id"))) {
                            model.setAddToReporting(true);
                        }
                    }
                    if (object.has("user_id")) {
                        model.setUser_id(object.getString("user_id"));
                    }
                    if (object.has("area")) {
                        model.setWorking_area_id(object.getString("area"));
                    }
                    if (object.has("user_name")) {
                        model.setUser_name(object.getString("user_name"));
                    }
                    if (object.has("details")) {
                        model.setDetails(object.getString("details"));
                    }
                    if (object.has("location")) {
                        model.setLocation(object.getString("location"));
                    }
                    if (object.has("text")) {
                        model.setText(object.getString("text"));
                    }
                    if (object.has("photo")) {
                        model.setPhoto(object.getString("photo"));
                    }
                    if (object.has("date")) {
                        model.setDate(object.getString("date"));
                    }
                    if (object.has("post_id")) {
                        model.setPost_ids(object.getString("post_id"));
                    }
                    postModels.add(model);
                }
                reportingAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
                binding.bLoadAllReports.setVisibility(View.VISIBLE);
                Constants.hideProgressDialog();
            }

        }, error -> {
            Constants.hideProgressDialog();
            if (error instanceof NetworkError || error instanceof TimeoutError) {
                Constants.showToast(context, "Please Check your Internet connection");
                return;
            }
            binding.bLoadAllReports.setVisibility(View.VISIBLE);
            Constants.showToast(context, error.getLocalizedMessage());
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("for_user_id", getIntent().getStringExtra("user_id"));
                params.put("page", "1");
                params.put("start_date", startDate);
                params.put("end_date", endDate);
                params.put("user_id", "16");
                params.put("for_user_type", getIntent().getStringExtra("user_type"));
                params.put("working_area_id", getIntent().getStringExtra("working_area_id"));
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

    public void loadReporting() {
        binding.bLoadAllReports.setVisibility(View.GONE);
        postModels.clear();
        Constants.showProgressDialog("Loading Reports", "Please Wait...", context);
        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.GET_REPORTING_VIA_USER_ID, response -> {
            Log.e("TAG", "loadReporting: " + response);
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
                Constants.showToast(context, jsonObject.getString("message"));
                JSONArray requestsData = jsonObject.getJSONArray("data");
                ArrayList<String> savedList = Constants.getSavedPostList(context);
                if (requestsData.length() > 0) {
                    binding.noDataLayout.setVisibility(View.GONE);
                }
                for (int i = 0; i < requestsData.length(); i++) {
                    JSONObject object = requestsData.getJSONObject(i);
                    PostModel model = new PostModel();
                    if (object.has("id")) {
                        model.setId(object.getString("id"));
                        if (savedList.contains(object.getString("id"))) {
                            model.setAddToReporting(true);
                        }
                    }
                    if (object.has("user_id")) {
                        model.setUser_id(object.getString("user_id"));
                    }
                    if (object.has("area")) {
                        model.setWorking_area_id(object.getString("area"));
                    }
                    if (object.has("user_name")) {
                        model.setUser_name(object.getString("user_name"));
                    }
                    if (object.has("details")) {
                        model.setDetails(object.getString("details"));
                    }
                    if (object.has("location")) {
                        model.setLocation(object.getString("location"));
                    }
                    if (object.has("text")) {
                        model.setText(object.getString("text"));
                    }
                    if (object.has("photo")) {
                        model.setPhoto(object.getString("photo"));
                    }
                    if (object.has("date")) {
                        model.setDate(object.getString("date"));
                    }
                    if (object.has("post_id")) {
                        model.setPost_ids(object.getString("post_id"));
                    }
                    postModels.add(model);
                }
                reportingAdapter.notifyDataSetChanged();

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
                params.put("user_id", getIntent().getStringExtra("user_id"));
                params.put("page", "1");
                params.put("type", getIntent().getStringExtra("user_type"));
                params.put("working_area_id", getIntent().getStringExtra("working_area_id"));
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


    private void showDatePicker(boolean isStartDate) {
        // check if activity not null otherwise your app is crashed
        // create new object of calendar everytime else when you reselect the date it bydefault chose prevoius one.
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String myFormat = "yyyy/MM/dd"; //Here you can change you desire format of date
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            // here pass the select date to matchDate method
            if (isStartDate) {
                binding.tvStartDate.setText(sdf.format(calendar.getTime()));
            } else {
                binding.tvEndDate.setText(sdf.format(calendar.getTime()));
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(ViewUserReportingActivity.this, date, calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        // this line for restrict your calendar to select future dates
       // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

}