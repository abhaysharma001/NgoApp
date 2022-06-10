package com.chetna.ngo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chetna.ngo.R;
import com.chetna.ngo.databinding.ActivityAddSuggestionsBinding;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddSuggestionsActivity extends AppCompatActivity {

    ActivityAddSuggestionsBinding binding;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddSuggestionsBinding.inflate(getLayoutInflater());
        context = this;
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle(R.string.add_suggestion);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_backspace_24);


        binding.bSubmitSuggestion.setOnClickListener(v -> {
            if (!binding.edSuggestionTitle.getText().equals("")) {
                submitSuggestion(binding.edSuggestionTitle.getText().toString(),
                        binding.edSuggestionLocation.getText().toString(),
                        binding.edSuggestionMessage.getText().toString());
            }
        });


    }

    private void submitSuggestion(String title, String location, String message) {
        Constants.showProgressDialog("Submitting Suggestion!", "Please Wait.....", context);
        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.SUBMIT_SUGGESTION, response -> {
            Log.e("TAG", "submitSuggestion: " + response);
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
                Constants.showToast(context, jsonObject.getString("message"));
                if (jsonObject.getBoolean("status")) {
                    Constants.showToast(context, "Suggestion Submitted!");
                    JSONObject userData = jsonObject.getJSONObject("0");
                    Log.e("TAG", "submitSuggestion: " + userData);
                }
            } catch (JSONException e) {
                Log.e("TAG", "submitSuggestion: " + e.getLocalizedMessage());
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
                params.put("suggestion", title + "\n" + location + "\n" + message);
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