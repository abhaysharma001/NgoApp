package com.chetna.ngo.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.chetna.ngo.databinding.ActivityCompleteProfileBinding;
import com.chetna.ngo.models.ProjectModel;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompleteProfileActivity extends AppCompatActivity {


    private Context context;
    private ActivityCompleteProfileBinding binding;
    private List<String> sp_data = new ArrayList();
    ArrayAdapter<String> adapter;
    private String projectName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCompleteProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = this;
        sp_data.add("Select Project Name");
        getSupportActionBar().setTitle(R.string.complete_profile);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_backspace_24);

        binding.bContinue
                .setOnClickListener(view -> sendToNextActivity());
        getProjectList();

        Spinner spinner = binding.spProjectName;
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sp_data);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    projectName = sp_data.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    private void sendToNextActivity() {
        String bloodGroup, bankName, accountNumber, ifscCode, nameOnBank;

        // aadharCardNumber = binding.aadharCardNumber.getText().toString().trim();
        // panCardNumber = binding.panCardNumber.getText().toString().trim();
        bloodGroup = binding.bloodGroup.getText().toString().trim();
        bankName = binding.bankName.getText().toString().trim();
        accountNumber = binding.accountNumber.getText().toString().trim();
        ifscCode = binding.ifscCode.getText().toString().trim();
        nameOnBank = binding.nameOnBank.getText().toString().trim();


        /*if (bloodGroup.isEmpty()) {
            binding.bloodGroup.setError("Enter Blood Group!");
            return;
        }
        if (bankName.isEmpty()) {
            binding.bankName.setError("Enter Bank Name!");
            return;
        }
        if (accountNumber.isEmpty()) {
            binding.accountNumber.setError("Enter Account Number!");
            return;
        }
        if (nameOnBank.isEmpty()) {
            binding.nameOnBank.setError("Enter Name!");
            return;
        }
        if (ifscCode.isEmpty()) {
            binding.ifscCode.setError("Enter IFSC Code!");
            return;
        }*/
        createUser(bloodGroup, bankName, nameOnBank, ifscCode, accountNumber);


    }

    private void getProjectList() {

        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.GET_PROJECT_LIST, response -> {
            Log.e("TAG", "getProjectList : " + response);
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
                JSONArray requestsData;
                if (jsonObject.has("0")) {
                    requestsData = jsonObject.getJSONArray("0");
                } else {
                    requestsData = jsonObject.getJSONArray("data");
                }


                sp_data.clear();

                sp_data.add("Select Project Name");
                for (int i = 0; i < requestsData.length(); i++) {
                    JSONObject object = requestsData.getJSONObject(i);
                    ProjectModel model = new ProjectModel();
                    if (object.has("id")) {
                        model.setId(object.getString("id"));
                    }
                    if (object.has("project_cordinator_id")) {
                        model.setProject_cordinator_id(object.getString("project_cordinator_id"));
                    }
                    if (object.has("project_id")) {
                        model.setProject_id(object.getString("project_id"));
                    }
                    if (object.has("project_name")) {
                        model.setProject_name(object.getString("project_name"));
                        sp_data.add(object.getString("project_name"));

                    }

                }
                adapter.notifyDataSetChanged();
                Log.e("TAG", "getProjectList: Sp : " + sp_data.size());


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

                params.put("user_id", "1000");
                params.put("page", "1");
                params.put("is_spinner", "1");

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


    private void createUser(String bloodGroup, String bankName, String nameOnBank, String ifscCode, String accountNumber) {

        Log.e("TAG", "createUser: " + projectName);
        if (TextUtils.isEmpty(projectName)) {
            Toast.makeText(context, "Please select project name!", Toast.LENGTH_SHORT).show();
            return;
        }
        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.REGISTER, response -> {
            if (response != null) {
                Log.e("TAG", "Register: " + response);
                try {
                    JSONObject res = new JSONObject(response);
                    boolean status = res.getBoolean("status");
                    String msg = res.getString("message");
                    JSONObject object = res.getJSONObject("0");
                    Constants.setString(context,Constants.USER_ID,object.getString("id"));

                    if (status) {
                        startActivity(new Intent(this, MainActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));

                    }
                    Constants.showToast(context, msg);

                } catch (JSONException e) {

                }
            }

        }, error -> Log.e("error is ", "" + error)) {
            //Pass Your Parameters here
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("blood_group", bloodGroup);
                params.put("account_number", accountNumber);
                params.put("password", getIntent().getStringExtra("password"));
                params.put("bank_name", bankName);
                params.put("name_on_bank", nameOnBank);
                params.put("ifsc_code", ifscCode);
                params.put("project_name", projectName);
                params.put("name", Constants.getString(context, Constants.NAME));
                params.put("email", Constants.getString(context, Constants.EMAIL));
                params.put("number", Constants.getString(context, Constants.PHONE));
                params.put("type", Constants.getString(context, Constants.USER_TYPE));
                params.put("ephone", Constants.getString(context, Constants.E_NUMBER));
                params.put("register", Constants.getString(context, Constants.USER_TYPE));

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        request.setRetryPolicy(new DefaultRetryPolicy(
                Constants.CONNECTION_TIMEOUT
                , DefaultRetryPolicy.DEFAULT_MAX_RETRIES
                , DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        queue.add(request);


    }


}