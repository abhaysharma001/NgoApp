package com.chetna.ngo.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import com.chetna.ngo.databinding.ActivityAddChildProgressBinding;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddChildProgressActivity extends AppCompatActivity {

    private Context context;
    private ActivityAddChildProgressBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddChildProgressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        int level1 = Integer.parseInt(getIntent().getStringExtra("level"));
        level1++;
        binding.edLevel.setText(String.valueOf(level1));
        binding.edChildLevelName.setText(getIntent().getStringExtra("child_name"));

        binding.bAddChildLevel.setOnClickListener(view -> {
            String name = binding.edChildLevelName.getText().toString();
            String info = binding.edLevelInfo.getText().toString();
            if (name.isEmpty()) {
                binding.edChildLevelName.setError("Enter Child Name");
                return;
            }
            if (info.isEmpty()) {
                binding.edLevelInfo.setError("Enter Level Information");
                return;
            }

            addChildLevel(binding.edLevel.getText().toString().trim(), info, name);
        });


    }

    private void addChildLevel(String level, String info, String name) {
        ProgressDialog pd = new ProgressDialog(context);
        pd.setTitle("Adding Level!");
        pd.setMessage("Please Wait!");
        pd.setCancelable(false);
        pd.show();
        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.ADD_CHILD_PROGRESS, response -> {
            Log.e("TAG", "addChildLevel: " + response);
            try {
                pd.dismiss();
                JSONObject jsonObject = new JSONObject(response);
                Constants.showToast(context, jsonObject.getString("message"));
                ((Activity) context).finish();

            } catch (JSONException e) {
                e.printStackTrace();
                pd.dismiss();
            }

        }, error -> {
            pd.dismiss();
            if (error instanceof NetworkError || error instanceof TimeoutError) {
                Constants.showToast(context, "Please Check your Internet connection");
                return;
            }
            Constants.showToast(context, error.getLocalizedMessage());
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("child_name", name);
                params.put("level", level);
                params.put("level_information", info);
                params.put("child_id", getIntent().getStringExtra("child_id"));
                params.put("user_id", Constants.getString(context, Constants.USER_ID));
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