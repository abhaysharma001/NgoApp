package com.chetna.ngo.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
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
import com.chetna.ngo.databinding.ActivityLoginBinding;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private Context context;
    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(context, R.color.yellow));


        if (!Constants.getString(context, Constants.USER_ID).equals("")) {
            startActivity(new Intent(context, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }


        binding.bLogin
                .setOnClickListener(view -> {
                    if (binding.loginEmail.getText().toString().trim().isEmpty()) {
                        Toast.makeText(context, "Enter Email!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (binding.loginPassword.getText().toString().trim().isEmpty()) {
                        Toast.makeText(context, "Enter Password!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    loginUser(binding.loginEmail.getText().toString().trim(),
                            binding.loginPassword.getText().toString().trim());
                });

        binding.registerText
                .setOnClickListener(view -> startActivity(new Intent(context, RegisterActivity.class)));
    }

    private void loginUser(String email, String pass) {
        Constants.showProgressDialog("Signing in", "Please Wait...", context);
        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.LOGIN, response -> {
            Log.e("TAG", "login: " + response);
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
                Constants.showToast(context, jsonObject.getString("message"));
                if (jsonObject.getBoolean("status")) {
                    JSONObject userData = jsonObject.getJSONObject("0");
                    Log.e("TAG", "loginUser: " + userData);
                    Constants.setString(context, Constants.USER_ID, userData.getString("id"));
                    Constants.setString(context, Constants.NAME, userData.getString("name"));
                    Constants.setString(context, Constants.EMAIL, userData.getString("email"));
                    Constants.setString(context, Constants.PHONE, userData.getString("phone"));
                    Constants.setString(context, Constants.USER_TYPE, userData.getString("user_type"));
                    Constants.setString(context, Constants.BANK_NAME, userData.getString("bank_name"));
                    Constants.setString(context, Constants.NAME_ON_BANK, userData.getString("name_on_bank"));
                    Constants.setString(context, Constants.AC_NO, userData.getString("account_number"));
                    Constants.setString(context, Constants.IFSC, userData.getString("ifsc_code"));
                    Constants.setString(context, Constants.BLOOD_GROUP, userData.getString("blood_group"));
                    Constants.setString(context, Constants.LOGIN, Constants.YES);
                    startActivity(new Intent(context, MainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
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
                params.put("password", pass);
                params.put("email", email);
                params.put("login", "login");
                Log.e("PARAMS", "getParams: " + params);
                return params;
            }
/*
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                // params.put("authtoken", Constants.getString(context, Constant.AUTHENTICATION_TOKEN));
                return params;
            }*/
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