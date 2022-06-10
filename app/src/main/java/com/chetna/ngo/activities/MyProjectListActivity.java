package com.chetna.ngo.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
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
import com.chetna.ngo.adapters.ProjectListAdapter;
import com.chetna.ngo.databinding.ActivityMyProjectListBinding;
import com.chetna.ngo.models.ProjectModel;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyProjectListActivity extends AppCompatActivity {

    private ActivityMyProjectListBinding binding;
    private Context context;
    private ArrayList<ProjectModel> list = new ArrayList<>();
    private ProjectListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyProjectListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = this;
        getSupportActionBar().setTitle(R.string.my_projects);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_backspace_24);

        getProjectList();

        adapter = new ProjectListAdapter(list,context,"m");
        binding.rvProjectList.setAdapter(adapter);
        binding.rvProjectList.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));

    }


    private void getProjectList(){
        Log.e("TAG", "getProjectList: " + Constants.getString(context, Constants.USER_TYPE));
        Constants.showProgressDialog(" Loading Project list!", "Please Wait...", context);
        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.MY_PROJECT_LIST, response -> {
            Log.e("TAG", "login: " + response);
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
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
                params.put("user_id", Constants.getString(context,Constants.USER_ID));
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