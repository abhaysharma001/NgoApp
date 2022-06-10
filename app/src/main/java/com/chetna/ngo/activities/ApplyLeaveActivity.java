package com.chetna.ngo.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chetna.ngo.R;
import com.chetna.ngo.databinding.ActivityApplyLeaveBinding;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ApplyLeaveActivity extends AppCompatActivity {

    private Context context;
    private ActivityApplyLeaveBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityApplyLeaveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        getSupportActionBar().setTitle(R.string.leave_application);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_backspace_24);

        binding.edFrom.setOnClickListener(v -> {
            showDatePicker(true);
        });
        binding.edTo.setOnClickListener(v -> {
            showDatePicker(false);
        });

        binding.bApplyLeave.setOnClickListener(view -> {
            String reason, location, from, to;
            reason = binding.edReason.getText().toString();
            location = binding.edLeaveLocation.getText().toString();
            from = binding.edFrom.getText().toString();
            to = binding.edTo.getText().toString();

            if (reason.isEmpty()) {
                binding.edReason.setError("Enter Reason");
                binding.edReason.requestFocus();
                return;
            }
            if (from.isEmpty()) {
                binding.edFrom.setError("Select From Date");
                binding.edFrom.requestFocus();
                return;
            }
            if (to.isEmpty()) {
                binding.edTo.setError("Select To Date");
                binding.edTo.requestFocus();
                return;
            }
            if (location.isEmpty()) {
                binding.edLeaveLocation.setError("Enter Leave Location");
                binding.edLeaveLocation.requestFocus();
                return;
            }
            applyLeave(reason, location, from, to);
        });

        binding.bMyLeaveApplications.setOnClickListener(view -> startActivity(new Intent(context, LeaveListActivity.class)));

    }

    public void applyLeave(String reason, String location, String from, String to) {
        Constants.showProgressDialog("Applying For Leave!", "Please Wait...", context);
        // list.clear();
        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.APPLY_LEAVE, response -> {
            Log.e("TAG", "applyLeave: " + response);
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
                Constants.showToast(context, jsonObject.getString("message"));
                if (jsonObject.getBoolean("status")) {
                    binding.edTo.setText("");
                    binding.edFrom.setText("");
                    binding.edReason.setText("");
                    binding.edLeaveLocation.setText("");
                }
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
                params.put("user_name", Constants.getString(context, Constants.NAME));
                params.put("type", Constants.getString(context, Constants.USER_TYPE));
                params.put("from_date", from);
                params.put("to_date", to);
                params.put("reason", reason);
                params.put("leave_address", location);
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
        if (ApplyLeaveActivity.this != null) {
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
                    binding.edFrom.setText(sdf.format(calendar.getTime()));
                } else {
                    binding.edTo.setText(sdf.format(calendar.getTime()));
                }
            };

            DatePickerDialog datePickerDialog = new DatePickerDialog(ApplyLeaveActivity.this, date, calendar
                    .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            // this line for restrict your calendar to select future dates
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        }
    }

}