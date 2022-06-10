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
import com.chetna.ngo.adapters.ChildLevelListAdapter;
import com.chetna.ngo.databinding.ActivityViewChildProfileBinding;
import com.chetna.ngo.models.ChildLevelModel;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewChildProfileActivity extends AppCompatActivity {


    private ActivityViewChildProfileBinding binding;
    private Context context;
    private ArrayList<ChildLevelModel> list = new ArrayList<>();
    private ChildLevelListAdapter adapter;
    private int level = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewChildProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = this;

        getSupportActionBar().setTitle(R.string.student_profile);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_backspace_24);

        binding.tvChildName.setText(getIntent().getStringExtra("name"));
        binding.tvChildAge.setText(getIntent().getStringExtra("age"));
        binding.tvChildGender.setText(getIntent().getStringExtra("gender"));

        binding.bAddStudentLevel.setOnClickListener(view -> startActivity(new Intent(context, AddChildProgressActivity.class)
                .putExtra("child_id", getIntent().getExtras().getString("child_id"))
                .putExtra("level", String.valueOf(level))
                .putExtra("child_name", getIntent().getStringExtra("name"))));


        adapter = new ChildLevelListAdapter(context, list);
        binding.rvChildLevelList.setAdapter(adapter);
        binding.rvChildLevelList.setLayoutManager(new LinearLayoutManager(context));
        binding.rvChildLevelList.hasFixedSize();


        loadChildProfile(getIntent().getExtras().getString("child_id"));

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recreate();
    }

    private void loadChildProfile(String child_id) {

        Constants.showProgressDialog("Adding Student!", "Please Wait...", context);
        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.GET_FRONTLINE_CHILD_LEVEL_LIST, response -> {
            Log.e("TAG", "addStudent: " + response);
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
                Constants.showToast(context, jsonObject.getString("message"));
                JSONArray requestsData = jsonObject.getJSONArray("data");
                if (requestsData.length() > 0) {
                    binding.noDataLayout.setVisibility(View.GONE);
                }
                for (int i = 0; i < requestsData.length(); i++) {
                    JSONObject object = requestsData.getJSONObject(i);
                    ChildLevelModel model = new ChildLevelModel();
                    model.setLevel_information(object.getString("level_information"));
                    model.setId(object.getString("id"));
                    list.add(model);
                }
                level = requestsData.length();
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

                params.put("child_id", child_id);
                params.put("frontline_worker_id", Constants.getString(context, Constants.USER_ID));
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