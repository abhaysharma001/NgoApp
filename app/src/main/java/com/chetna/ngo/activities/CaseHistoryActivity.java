package com.chetna.ngo.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.chetna.ngo.adapters.CaseHistoryAdapter;
import com.chetna.ngo.databinding.ActivityCaseHistoryBinding;
import com.chetna.ngo.models.AttendanceModel;
import com.chetna.ngo.models.CaseHistoryModel;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CaseHistoryActivity extends AppCompatActivity {

    private Context context;
    private ArrayList<CaseHistoryModel> list = new ArrayList<>();
    private ActivityCaseHistoryBinding binding;
    private CaseHistoryAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCaseHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        getSupportActionBar().setTitle(R.string.case_history);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_backspace_24);

        adapter = new CaseHistoryAdapter(context,list);
        recyclerView = binding.rvCaseHistoryList;
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.hasFixedSize();
        getCaseHistory();
    }

    private void getCaseHistory() {
        Log.e("TAG", "getCaseHistory: " + Constants.getString(context, Constants.USER_TYPE));
        Constants.showProgressDialog(" Loading Case History!", "Please Wait...", context);
        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.GET_CASE_HISTORY, response -> {
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
                CaseHistoryModel model;
                for (int i = 0; i < requestsData.length(); i++) {
                    model = new CaseHistoryModel();
                    JSONObject object = requestsData.getJSONObject(i);
                    model.setDate(object.getString("date"));
                    model.setId(object.getString("id"));
                    model.setTitle(object.getString("title"));
                    model.setDescription(object.getString("description"));
                    ArrayList<String> imagesList =new ArrayList<>(Arrays.asList(object.getString("image").split(",")));
                    model.setImage(imagesList);
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