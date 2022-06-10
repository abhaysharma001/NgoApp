package com.chetna.ngo.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chetna.ngo.R;
import com.chetna.ngo.adapters.WorkerRequestAdapter;
import com.chetna.ngo.databinding.ActivityWorkerRequestsBinding;
import com.chetna.ngo.models.ProjectModel;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WorkerRequestsActivity extends AppCompatActivity {

    private Context context;
    private ArrayList<ProjectModel> list = new ArrayList<>();
    private WorkerRequestAdapter adapter;
    private ActivityWorkerRequestsBinding binding;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWorkerRequestsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        if (Constants.isReporting) {
            getSupportActionBar().setTitle(R.string.send_report);
        } else {
            getSupportActionBar().setTitle(R.string.worker_requests);
        }
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_backspace_24);
        recyclerView = binding.workerRecyclerView;
        adapter = new WorkerRequestAdapter(context, list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.hasFixedSize();
        getRequests();

        binding.sendReport.setOnClickListener(v -> {
            if (Constants.getSavedPostList(context).isEmpty()) {
                Constants.showToast(context, "Select Some Reports First");
            } else {
                startActivity(new Intent(context, SendReportActivity.class));
            }
        });
    }

    public void getRequests() {
        list.clear();
        String url = "";
        if (Constants.getString(context, Constants.USER_TYPE).equals(Constants.USER_TYPE_ADMIN) || Constants.getString(context, Constants.USER_TYPE).equals(Constants.USER_TYPE_SUB_ADMIN))
            url = BaseUrls.ADMIN_VERIFICATION_LIST;
        else if (Constants.getString(context, Constants.USER_TYPE).equals(Constants.USER_TYPE_PROJECT_CO_ORDINATOR)) {
            if (Constants.isReporting) {
                url = BaseUrls.GET_STREET_CORDINATOR_LIST;
            } else {
                url = BaseUrls.PROJECT_COORDINATOR_VERIFICATION_LIST;
            }
        } else {
            Toast.makeText(context, "Invalid User Type", Toast.LENGTH_SHORT).show();
            return;
        }
        Constants.showProgressDialog("Getting Requests", "Please Wait...", context);
        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            Log.e("TAG", "getVerificationList: " + response);
            Constants.hideProgressDialog();
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getBoolean("status")) {
                    JSONArray requestsData;
                    if (jsonObject.has("0")) {
                        requestsData = jsonObject.getJSONArray("0");
                    } else {
                        requestsData = jsonObject.getJSONArray("data");
                    }
                    if (requestsData.length() > 0) {
                        binding.noDataLayout.setVisibility(View.GONE);
                    }
                    for (int i = 0; i < requestsData.length(); i++) {
                        JSONObject object = requestsData.getJSONObject(i);
                        ProjectModel model = new ProjectModel();
                        if (object.has("id")) {
                            model.setId(object.getString("id"));
                        }
                        if (object.has("user_id")) {
                            model.setUser_id(object.getString("user_id"));
                        }
                        if (object.has("project_cordinator_id")) {
                            model.setProject_cordinator_id(object.getString("project_cordinator_id"));
                        }
                        if (object.has("user_type")) {
                            model.setUser_type(object.getString("user_type"));
                        }
                        if (object.has("project_id")) {
                            model.setProject_id(object.getString("project_id"));
                        }
                        if (object.has("project_name")) {
                            model.setProject_name(object.getString("project_name"));
                        }
                        if (object.has("verify_status")) {
                            model.setVerify_status(object.getString("verify_status"));
                        }
                        if (object.has("user_name")) {
                            model.setUser_name(object.getString("user_name"));
                        }
                        if (object.has("post_count")) {
                            model.setPost_count(object.getString("post_count"));
                        }
                        list.add(model);
                    }
                    adapter.notifyDataSetChanged();
                    if (Constants.isReporting) {
                        binding.sendReport.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.noDataLayout.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Constants.hideProgressDialog();
            }

        }, error -> {
            Constants.hideProgressDialog();
            binding.noDataLayout.setVisibility(View.VISIBLE);
            if (error instanceof NetworkError || error instanceof TimeoutError) {
                Constants.showToast(context, "Please Check your Internet connection");
            }
            //Constants.showToast(context, error.getLocalizedMessage());
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("list", "any");
                if (Constants.isReporting || Constants.getString(context, Constants.USER_TYPE).equals(Constants.USER_TYPE_ADMIN) || Constants.getString(context, Constants.USER_TYPE).equals(Constants.USER_TYPE_SUB_ADMIN)) {
                    params.put("page", "1");
                }
                //if (Constants.getString(context, Constants.USER_TYPE).equals(Constants.USER_TYPE_ADMIN) || Constants.getString(context, Constants.USER_TYPE).equals(Constants.USER_TYPE_SUB_ADMIN)) {
                    params.put("project_id", getIntent().getStringExtra("project_id") != null ? getIntent().getStringExtra("project_id") : "0");
               // }
                params.put("user_id", Constants.getString(context, Constants.USER_ID));
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