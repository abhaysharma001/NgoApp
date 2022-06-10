package com.chetna.ngo.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chetna.ngo.R;
import com.chetna.ngo.databinding.ActivityRegisterBinding;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private Context context;
    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        binding.loginText.setOnClickListener(view -> ((Activity) context).finish());
        binding.bRegister.setOnClickListener(view -> sendToNextActivity());
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(context, R.color.yellow));

        List<String> list = new ArrayList<>();
        list.add("Front line Worker");
        list.add("Project Coordinator");
        list.add("Street Coordinator");


        Spinner userTypeSpinner = findViewById(R.id.userTypeSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSpinner.setAdapter(adapter);

        Constants.setString(context, Constants.USER_TYPE, Constants.USER_TYPE_PUBLIC);
        binding.userTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 0) {
                    Constants.setString(context, Constants.USER_TYPE, Constants.USER_TYPE_FRONT_LINE_WORKER);
                }
                if (i == 1) {
                    Constants.setString(context, Constants.USER_TYPE, Constants.USER_TYPE_PROJECT_CO_ORDINATOR);
                }
                if (i == 2) {
                    Constants.setString(context, Constants.USER_TYPE, Constants.USER_TYPE_STREET_CO_ORDINATOR);
                }
                Log.e("TAG", "onItemSelected: "+Constants.getString(context, Constants.USER_TYPE));

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Constants.setString(context, Constants.USER_TYPE, Constants.USER_TYPE_PUBLIC);
            }
        });

    }


    private void sendToNextActivity() {
        String name, email, ephone, phone, password, confirmPassword;

        name = binding.registerName.getText().toString().trim();
        email = binding.registerEmail.getText().toString().trim();
        phone = binding.registerPhone.getText().toString().trim();
        ephone = binding.registerEmergencyPhone.getText().toString().trim();
        password = binding.registerPassword.getText().toString().trim();
        confirmPassword = binding.registerConfirmPass.getText().toString().trim();


        if (name.isEmpty()) {
            binding.registerName.setError("Enter Name!");
            return;
        }
        if (email.isEmpty()) {
            binding.registerEmail.setError("Enter Email!");
            return;
        }
        if (phone.isEmpty()) {
            binding.registerPhone.setError("Enter Phone!");
            return;
        }
        if (ephone.isEmpty()) {
            binding.registerEmergencyPhone.setError("Enter Emergency Phone!");
            return;
        }
        if (password.isEmpty()) {
            binding.registerPassword.setError("Enter Password!");
            return;
        }
        if (confirmPassword.isEmpty()) {
            binding.registerConfirmPass.setError("Enter Password!");
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(context, "Password doesn't match!", Toast.LENGTH_SHORT).show();
            binding.registerConfirmPass.setError("");
            return;
        }

        Constants.setString(context, Constants.NAME, name);
        Constants.setString(context, Constants.PHONE, phone);
        Constants.setString(context, Constants.EMAIL, email);
        Constants.setString(context, Constants.E_NUMBER, ephone);
        Intent intent = new Intent(context, CompleteProfileActivity.class);
        intent.putExtra("password", password);
        if (Constants.getString(context, Constants.USER_TYPE).equals(Constants.USER_TYPE_PROJECT_CO_ORDINATOR))
            startActivity(intent);
        else
            createUser(password);

    }

    private void createUser(String  p) {


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
                params.put("blood_group", "");
                params.put("account_number", "");
                params.put("password", p);
                params.put("bank_name", "");
                params.put("name_on_bank", "");
                params.put("ifsc_code", "");
                params.put("project_name", "");
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