package com.chetna.ngo.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
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
import com.chetna.ngo.adapters.AttendanceAdapter;
import com.chetna.ngo.databinding.ActivityViewAttendanceBinding;
import com.chetna.ngo.models.AttendanceModel;
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

public class ViewAttendanceActivity extends AppCompatActivity {

    private ActivityViewAttendanceBinding binding;
    private Context context;
    private ArrayList<AttendanceModel> list = new ArrayList<>();
    private AttendanceAdapter adapter;
    private AppCompatButton bProjectCo, bFrontLine, bStreetCo;
    private ArrayList<AttendanceModel> listToShow = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewAttendanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;

        getSupportActionBar().setTitle(R.string.view_attendance);

        binding.tvEndDate.setOnClickListener(view -> showDatePicker(false));
        binding.tvStartDate.setOnClickListener(view -> showDatePicker(true));

        bProjectCo = binding.bProjectCo;
        bStreetCo = binding.bStreetCo;
        bFrontLine = binding.bFrontLine;

        adapter = new AttendanceAdapter(context, listToShow);
        binding.rvAttendance.setLayoutManager(new LinearLayoutManager(this));
        binding.rvAttendance.setAdapter(adapter);


        binding.bSearch.setOnClickListener(view -> {
            String startDate, endDate;
            startDate = binding.tvStartDate.getText().toString();
            endDate = binding.tvEndDate.getText().toString();

            if (startDate.isEmpty()) {
                Toast.makeText(context, "Please select start date!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (endDate.isEmpty()) {
                Toast.makeText(context, "Please select end date!", Toast.LENGTH_SHORT).show();
                return;
            }
            startDate = startDate.replace("/", "-");
            endDate = endDate.replace("/", "-");
            getAttendance(startDate, endDate);

        });

        if (Constants.getString(context, Constants.USER_TYPE).equalsIgnoreCase(Constants.USER_TYPE_PROJECT_CO_ORDINATOR)) {
            bProjectCo.setVisibility(View.GONE);
        }
        if (Constants.getString(context, Constants.USER_TYPE).equalsIgnoreCase(Constants.USER_TYPE_STREET_CO_ORDINATOR)) {
            bProjectCo.setVisibility(View.GONE);
            bStreetCo.setVisibility(View.GONE);
        }
        if (Constants.getString(context, Constants.USER_TYPE).equalsIgnoreCase(Constants.USER_TYPE_FRONT_LINE_WORKER)) {
            bProjectCo.setVisibility(View.GONE);
            bStreetCo.setVisibility(View.GONE);
            bFrontLine.setVisibility(View.GONE);
        }

        bFrontLine.setOnClickListener(view -> {
            bFrontLine.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.qqq)));
            bStreetCo.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green)));
            bProjectCo.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green)));
            addToList(Constants.USER_TYPE_FRONT_LINE_WORKER);

        });
        bProjectCo.setOnClickListener(view -> {
            bProjectCo.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.qqq)));
            bFrontLine.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green)));
            bStreetCo.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green)));
            addToList(Constants.USER_TYPE_PROJECT_CO_ORDINATOR);
        });
        bStreetCo.setOnClickListener(view -> {
            bStreetCo.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.qqq)));
            bFrontLine.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green)));
            bProjectCo.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green)));
            addToList(Constants.USER_TYPE_STREET_CO_ORDINATOR);

        });


    }

    private void addToList(String userType) {
        listToShow.clear();
        for (AttendanceModel m :
                list) {
            if (m.getUser_type().equalsIgnoreCase(userType)) {
                listToShow.add(m);
            }
        }
        adapter.notifyDataSetChanged();

    }

    private void getAttendance(String startDate, String endDate) {
        // Log.e("TAG", "getAttendance: " + Constants.getString(context, Constants.USER_TYPE));
        Constants.showProgressDialog(" Loading Attendance list!", "Please Wait...", context);
        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.GET_ATTENDANCE, response -> {
            Log.e("TAG", "getAttendance: " + response);
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
                JSONArray requestsData;
                if (jsonObject.has("0")) {
                    requestsData = jsonObject.getJSONArray("0");
                } else {
                    requestsData = jsonObject.getJSONArray("data");
                }
                if (requestsData.length() > 0) {
                    binding.noDataLayout.setVisibility(View.GONE);
                }
                AttendanceModel model;

                for (int i = 0; i < requestsData.length(); i++) {
                    model = new AttendanceModel();
                    JSONObject object = requestsData.getJSONObject(i);

                    if (object.has("date"))
                        model.setDate(object.getString("date"));

                    if (object.has("id"))
                        model.setId(object.getString("id"));

                    if (object.has("photo"))
                        model.setPhoto(object.getString("photo"));

                    if (object.has("user_id"))
                        model.setUser_id(object.getString("user_id"));

                    if (object.has("user_type"))
                        model.setUser_type(object.getString("user_type"));

                    if (object.has("user_name"))
                        model.setUsername(object.getString("user_name"));

                    if (object.has("count"))
                        model.setCount(object.getString("count"));

                    if (object.has("date_attendance"))
                        model.setDate_attendance(object.getString("date_attendance"));
                    list.add(model);
                }
                adapter.notifyDataSetChanged();


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
                params.put("user_id", Constants.getString(context, Constants.USER_ID));
                params.put("start_date", startDate);
                params.put("end_date", endDate);
                params.put("project_id", getIntent().getExtras().get("project_id").toString());
                params.put("page", "1");
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

        DatePickerDialog datePickerDialog = new DatePickerDialog(ViewAttendanceActivity.this, date, calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        // this line for restrict your calendar to select future dates
        // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }
}