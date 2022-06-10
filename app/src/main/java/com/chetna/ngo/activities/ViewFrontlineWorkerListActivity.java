
package com.chetna.ngo.activities;

import android.content.Context;
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
import com.chetna.ngo.adapters.FrontLineWorkerList;
import com.chetna.ngo.databinding.ActivityViewFrontlineWorkerListBinding;
import com.chetna.ngo.models.FrontLineWorkerListModel;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewFrontlineWorkerListActivity extends AppCompatActivity {

    private Context context;
    private ActivityViewFrontlineWorkerListBinding binding;

    private FrontLineWorkerList frontLineWorkerListAdapter;
    private ArrayList<FrontLineWorkerListModel> frontLineWorkerListModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewFrontlineWorkerListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        getSupportActionBar().setTitle(getIntent().getStringExtra("name"));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_backspace_24);
        binding.frontLineWorkerRecyclerView.setVisibility(View.VISIBLE);
        binding.frontLineWorkerRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        frontLineWorkerListAdapter = new FrontLineWorkerList(frontLineWorkerListModelArrayList, context);
        binding.frontLineWorkerRecyclerView.setAdapter(frontLineWorkerListAdapter);
        loadFrontlineWorkerList();
    }

    public void loadFrontlineWorkerList() {
        Constants.showProgressDialog("Loading Worker List!", "Please Wait...", context);

        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.GET_FRONTLINE_WORKER_LIST, response -> {
            Log.e("TAG", "loadFrontlineWorkerList: " + response);
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
                Constants.showToast(context, jsonObject.getString("message"));
                if (jsonObject.has("data")) {
                    JSONArray requestsData = jsonObject.getJSONArray("data");
                    if (requestsData.length() > 0) {
                        binding.noDataLayout.setVisibility(View.GONE);
                    }
                    for (int i = 0; i < requestsData.length(); i++) {
                        JSONObject object = requestsData.getJSONObject(i);
                        FrontLineWorkerListModel model = new FrontLineWorkerListModel();
                        if (object.has("id")) {
                            model.setId(object.getString("id"));
                        }
                        if (object.has("user_id")) {
                            model.setUser_id(object.getString("user_id"));
                        }
                        if (object.has("user_name")) {
                            model.setUser_name(object.getString("user_name"));
                        }
                        if (object.has("verify_status")) {
                            model.setVerify_status(object.getString("verify_status"));
                        }

                        if (object.has("project_id")) {
                            model.setProject_id(object.getString("project_id"));
                        }
                        if (object.has("project_name")) {
                            model.setProject_name(object.getString("project_name"));
                        }
                        if (object.has("user_type")) {
                            model.setUser_type(object.getString("user_type"));
                        }
                        if (object.has("post_count")) {
                            model.setPost_count(object.getString("post_count"));
                        }
                        if (object.has("project_cordinator_id")) {
                            model.setProject_cordinator_id(object.getString("project_cordinator_id"));
                        }
                        if (object.has("status")) {
                            model.setStatus(object.getString("status"));
                        }

                        model.setWorking_area_name(getIntent().getStringExtra("name"));
                        model.setWorking_area_id(getIntent().getStringExtra("working_area_id"));

                        frontLineWorkerListModelArrayList.add(model);
                    }
                    frontLineWorkerListAdapter.notifyDataSetChanged();
                } else {
                    binding.noDataLayout.setVisibility(View.GONE);
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
                params.put("page", "1");
                params.put("working_area_id", getIntent().getStringExtra("working_area_id"));
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