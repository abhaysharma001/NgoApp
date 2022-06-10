package com.chetna.ngo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chetna.ngo.R;
import com.chetna.ngo.databinding.ActivityBeAvolenteerBinding;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BeAVolenteerActivity extends AppCompatActivity {

    private ActivityBeAvolenteerBinding binding;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBeAvolenteerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle(R.string.be_a_volunteer);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_backspace_24);
        context = this;
        binding.bSubmit.setOnClickListener(view -> {

            String name, email, phone, address, about;

            name = binding.edName.getText().toString();
            email = binding.edEmail.getText().toString();
            phone = binding.edPhone.getText().toString();
            address = binding.address.getText().toString();
            about = binding.about.getText().toString();

            if (name.isEmpty()) {
                binding.edName.setError("Enter Name!");
                return;
            }
            if (email.isEmpty()) {
                binding.edEmail.setError("Enter Email!");
                return;
            }
            if (phone.isEmpty()) {
                binding.edPhone.setError("Enter Phone!");
                return;
            }
            if (address.isEmpty()) {
                binding.address.setError("Enter Address!");
                return;
            }
            if (about.isEmpty()) {
                binding.address.setError("Enter About You!");
                return;
            }

            addRequest(name, email, phone, address, about);


        });


    }

    private void addRequest(String name, String email, String phone, String address, String about) {
        Constants.showProgressDialog("Adding request", "Please Wait...", context);
        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.ADD_VOLENTEER, response -> {
            Log.e("TAG", "addRequest: " + response);
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
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
                params.put("name", name);
                params.put("email", email);
                params.put("phone", phone);
                params.put("working_area", address);
                params.put("about_yourself", about);
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