package com.chetna.ngo.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
import com.chetna.ngo.adapters.LeaveAdapter;
import com.chetna.ngo.databinding.ActivityLeaveListBinding;
import com.chetna.ngo.models.LeaveModel;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LeaveListActivity extends AppCompatActivity {

    private ActivityLeaveListBinding binding;
    private LeaveListActivity activity;
    private LeaveAdapter adapter;
    private ArrayList<LeaveModel> leaveAdapterArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLeaveListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;

        getSupportActionBar().setTitle(R.string.leave_application);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_backspace_24);

        adapter = new LeaveAdapter(leaveAdapterArrayList, activity);
        binding.rvLeaveApplications.setLayoutManager(new LinearLayoutManager(activity));
        binding.rvLeaveApplications.setAdapter(adapter);


        getData();


    }

    private void getData() {

        Constants.showProgressDialog("Applying For Leave!", "Please Wait...", activity);
        // list.clear();
        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.MY_LEAVE_LIST, response -> {
            Log.e("TAG", "applyLeave: " + response);
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
                Constants.showToast(activity, jsonObject.getString("message"));
                if (jsonObject.getBoolean("status")) {
                    binding.noDataLayout.setVisibility(View.GONE);
                    JSONArray array = jsonObject.getJSONArray("data");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        LeaveModel model = new LeaveModel();
                        model.setUser_id(obj.getString("user_id"));
                        model.setUser_name(obj.getString("user_name"));
                        model.setApplied_on(obj.getString("applied_on"));
                        model.setPhoto(obj.getString("photo"));
                        model.setUser_type(obj.getString("user_type"));
                        model.setReason(obj.getString("reason"));
                        model.setFrom_date(obj.getString("from_date"));
                        model.setTo_date(obj.getString("to_date"));
                        model.setLeave_address(obj.getString("leave_address"));
                        model.setStatus(obj.getString("status"));

                        leaveAdapterArrayList.add(model);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    binding.noDataLayout.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Constants.hideProgressDialog();
                binding.noDataLayout.setVisibility(View.VISIBLE);
            }

        }, error -> {
            Constants.hideProgressDialog();
            binding.noDataLayout.setVisibility(View.VISIBLE);
            if (error instanceof NetworkError || error instanceof TimeoutError) {
                Constants.showToast(activity, "Please Check your Internet connection");
                return;
            }
            Constants.showToast(activity, error.getLocalizedMessage());
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", Constants.getString(activity, Constants.USER_ID));
                Log.e("PARAMS", "getParams: " + params);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(activity.getApplicationContext());
        request.setRetryPolicy(new
                DefaultRetryPolicy(
                Constants.CONNECTION_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }
}