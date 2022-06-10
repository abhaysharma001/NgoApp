package com.chetna.ngo.activities;

import android.content.Context;
import android.content.Intent;
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
import com.chetna.ngo.adapters.ChildListAdapter;
import com.chetna.ngo.databinding.ActivityChildListBinding;
import com.chetna.ngo.models.ChildModel;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChildListActivity extends AppCompatActivity {
    private Context context;
    private ActivityChildListBinding binding;
    private ArrayList<ChildModel> list = new ArrayList<>();
    private ChildListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChildListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;

        getSupportActionBar().setTitle("Area : " + getIntent().getExtras().getString("name"));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_backspace_24);

        loadChildList(getIntent().getExtras().getString("working_area_id"));
        binding.bAddStudent.setOnClickListener(view -> {
            Intent intent = new Intent(context, AddStudentActivity.class);
            intent.putExtra("working_area_id", getIntent().getExtras().getString("working_area_id"));
            startActivity(intent);
        });

        adapter = new ChildListAdapter(context, list);
        binding.rvChildList.setAdapter(adapter);
        binding.rvChildList.hasFixedSize();
        binding.rvChildList.setLayoutManager(new LinearLayoutManager(context));


    }

    private void loadChildList(String id) {
        Constants.showProgressDialog("Loading Student List!", "Please Wait...", context);

        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.GET_FRONTLINE_WORKER_CHILD_LIST, response -> {
            Log.e("TAG", "loadChildList: " + response);
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
                Constants.showToast(context, jsonObject.getString("message"));
                JSONArray requestsData = jsonObject.getJSONArray("data");
                if (requestsData.length() > 0) {
                    binding.noDataLayout.setVisibility(View.GONE);
                } else {
                    binding.noDataLayout.setVisibility(View.VISIBLE);
                }
                for (int i = 0; i < requestsData.length(); i++) {
                    JSONObject object = requestsData.getJSONObject(i);
                    ChildModel model = new ChildModel();
                    model.setName(object.getString("name"));
                    model.setAge(object.getString("age"));
                    model.setGender(object.getString("gender"));
                    model.setId(object.getString("id"));
                    list.add(model);
                }
                adapter.notifyDataSetChanged();


            } catch (JSONException e) {
                e.printStackTrace();
                binding.noDataLayout.setVisibility(View.VISIBLE);
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
                params.put("working_area_id", id);
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