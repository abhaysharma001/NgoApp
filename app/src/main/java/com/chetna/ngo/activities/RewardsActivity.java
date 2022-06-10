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
import com.chetna.ngo.adapters.RewardsAdapter;
import com.chetna.ngo.databinding.ActivityRewardsBinding;
import com.chetna.ngo.models.RewardModel;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RewardsActivity extends AppCompatActivity {

    ActivityRewardsBinding binding;
    Context context;
    private ArrayList<RewardModel> list = new ArrayList<>();
    private RewardsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRewardsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        getSupportActionBar().setTitle(R.string.our_rewards);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_backspace_24);

        adapter = new RewardsAdapter(context,list);

        binding.rvRewardsList.setAdapter(adapter);
        binding.rvRewardsList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvRewardsList.setHasFixedSize(true);

        getRewardsList();

    }


    private void getRewardsList() {
        Log.e("TAG", "getRewardsList: " + Constants.getString(context, Constants.USER_TYPE));
        Constants.showProgressDialog(" Loading Our Rewards!", "Please Wait...", context);
        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.GET_REWARDS, response -> {
            Log.e("TAG", "getRewardsList: " + response);
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
                RewardModel model;
                for (int i = 0; i < requestsData.length(); i++) {
                    model = new RewardModel();
                    JSONObject object = requestsData.getJSONObject(i);
                    model.setId(object.optString("id"));
                    model.setTitle(object.optString("title"));
                    model.setDescription(object.optString("description"));
                    model.setPhotos(new ArrayList<>(Arrays.asList(object.getString("photo").split(","))));
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
                params.put("user_id", Constants.getString(context, Constants.USER_ID));
                params.put("page", "1");
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