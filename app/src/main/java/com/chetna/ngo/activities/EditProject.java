package com.chetna.ngo.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.chetna.ngo.databinding.ActivityAddPostBinding;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditProject extends AppCompatActivity {

    private Context context;
    private TextView tvFrom, tvTo;
    private EditText edName, edDescription, location;
    private String status, projectId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_project);

        context = this;

        status = getIntent().getStringExtra("status");
        projectId = getIntent().getStringExtra("projectId");
        getSupportActionBar().setTitle(R.string.project);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_backspace_24);


        tvFrom = findViewById(R.id.tvProjectFromDate);
        edName = findViewById(R.id.edProjectTitle);
        tvTo = findViewById(R.id.tvProjectToDate);
        tvFrom.setOnClickListener(view -> showDatePicker(true));
        tvTo.setOnClickListener(view -> showDatePicker(false));

        findViewById(R.id.bAddProject)
                .setOnClickListener(view -> {
                    String name, from, to, description, location;

                    from = tvFrom.getText().toString();
                    name = ((EditText) findViewById(R.id.edProjectTitle)).getText().toString();
                    to = tvTo.getText().toString();

                    if (name.isEmpty()) {
                        ((EditText) findViewById(R.id.edProjectTitle)).setError("Enter Project Title!");
                        return;
                    }
                    from = from.replace("/", "-");
                    to = to.replace("/", "-");
                    changeProjectStatus(status, name, projectId, from, to);

                });


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
                tvFrom.setText(sdf.format(calendar.getTime()));
            } else {
                tvTo.setText(sdf.format(calendar.getTime()));
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, date, calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        // this line for restrict your calendar to select future dates
        // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void changeProjectStatus(String status, String name, String projectId, String from, String to) {
        if (from.isEmpty()) {
            Toast.makeText(context, "Enter start date!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (to.isEmpty()) {
            Toast.makeText(context, "Enter end date!", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.e("TAG", "createProject: " + Constants.getString(context, Constants.USER_TYPE));
        Constants.showProgressDialog(status + "ing Project".toUpperCase(), "Please Wait...", context);
        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.ADMIN_CREATE_PROJECT, response -> {
            Log.e("TAG", "createProject: " + response);
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
                Constants.showToast(context, jsonObject.getString("message"));
                if (jsonObject.getBoolean("status")) {
                    tvFrom.setText("");
                    tvTo.setText("");
                    edName.setText("");
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
                params.put(status, "any");
                params.put("project_name", name);
                if (projectId != null) {
                    params.put("project_id", projectId);
                }
                params.put("from_date", from);
                params.put("to_date", to);

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

}

