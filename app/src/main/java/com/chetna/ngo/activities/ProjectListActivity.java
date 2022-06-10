package com.chetna.ngo.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.chetna.ngo.adapters.ProjectListAdapter;
import com.chetna.ngo.databinding.ActivityProjectListBinding;
import com.chetna.ngo.models.ProjectModel;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProjectListActivity extends AppCompatActivity {

    private Context context;
    private ActivityProjectListBinding binding;
    private ArrayList<ProjectModel> list = new ArrayList<>();
    private ArrayList<ProjectModel> temp_list = new ArrayList<>();
    private ProjectListAdapter adapter;
    String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProjectListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        if (getIntent().getExtras().getString("from") != null) {
            from = getIntent().getExtras().getString("from");
        } else {
            from = "p";
        }
        if (from.equalsIgnoreCase("assign_sub_admin")) {
            getSupportActionBar().setTitle(R.string.assign_sub_admin_to_project);
        } else {
            getSupportActionBar().setTitle(R.string.projects_list);
        }
        getProjectList("", false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_backspace_24);
        Log.e("TAG", "onCreate: from " + from);
        adapter = new ProjectListAdapter(list, context, from);
        binding.rvProjectList.setAdapter(adapter);
        binding.rvProjectList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        binding.bSearchProject.setOnClickListener(view -> {
            String text;
            text = binding.edSearchProject.getText().toString();
            if (text.isEmpty()) {
                binding.edSearchProject.setError("Enter type something....");
                binding.edSearchProject.requestFocus();
                return;
            }
            getProjectList(text, true);
        });


        binding.edSearchProject.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    list.clear();

                    list.addAll(temp_list);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        if (!from.equalsIgnoreCase("g")) {
            binding.searchLyt
                    .setVisibility(Constants.getString(context, Constants.USER_TYPE)
                            .equals(Constants.USER_TYPE_SUB_ADMIN) ? View.GONE : View.VISIBLE);
        }
    }

    public void getProjectList(String text, boolean isSearch) {
        Log.e("TAG", "getProjectList: " + Constants.getString(context, Constants.USER_TYPE));
        String url;
        if (isSearch) {
            temp_list.clear();
            temp_list.addAll(list);
            url = BaseUrls.SEARCH_PROJECT;
        } else {
            if (Constants.getString(context, Constants.USER_TYPE).equals(Constants.USER_TYPE_SUB_ADMIN) && !from.equalsIgnoreCase("g")) {
                url = BaseUrls.GET_SUB_ADMIN_LIST_PROJECT_LIST;
            } else {
                url = BaseUrls.GET_PROJECT_LIST;
            }
        }
        list.clear();
        Constants.showProgressDialog(" Loading Project list!", "Please Wait...", context);
        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            Log.e("TAG", "login: " + response);
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
                if (!jsonObject.getBoolean("status")) {
                    if (isSearch) {
                        list.clear();
                        adapter.notifyDataSetChanged();
                        return;
                    }
                }
                JSONArray requestsData;
                if (jsonObject.has("0")) {
                    requestsData = jsonObject.getJSONArray("0");
                } else {
                    requestsData = jsonObject.getJSONArray("data");
                }
                if (requestsData.length() > 0) {
                    binding.noDataLayout.setVisibility(View.GONE);
                }
                if (isSearch) {
                    list.clear();
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
                if (!isSearch) {
                    params.put("user_id", Constants.getString(context, Constants.USER_ID));
                    params.put("page", "1");
                    params.put("is_spinner", "0");

                } else {
                    params.put("keyword", text);
                }
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