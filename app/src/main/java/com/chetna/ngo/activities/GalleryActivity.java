package com.chetna.ngo.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.chetna.ngo.adapters.GalleryAdapter;
import com.chetna.ngo.adapters.ProjectListAdapter;
import com.chetna.ngo.databinding.ActivityGalleryBinding;
import com.chetna.ngo.databinding.GalleryItemLytBinding;
import com.chetna.ngo.models.CaseHistoryModel;
import com.chetna.ngo.models.GalleryModel;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GalleryActivity extends AppCompatActivity {

    private Context context;
    private ArrayList<GalleryModel> list = new ArrayList<>();
    private GalleryAdapter adapter;
    private ActivityGalleryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGalleryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        getSupportActionBar().setTitle(R.string.gallery);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_backspace_24);

        String id = getIntent().getExtras().getString("project_id");

        adapter = new GalleryAdapter(context,list);
        binding.rvGalleryList.setLayoutManager(new LinearLayoutManager(context));
        binding.rvGalleryList.setAdapter(adapter);
        getGallery(id);
    }


    private void getGallery(String id) {
        binding.noDataLayout.setVisibility(View.VISIBLE);
        Log.e("TAG", "getGallery: " + Constants.getString(context, Constants.USER_TYPE));
        Constants.showProgressDialog(" Loading Gallery!", "Please Wait...", context);
        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.GET_GALLERY_VIA_PROJECT, response -> {
            Log.e("TAG", "gallery: " + response);
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
                    for (int i = 0; i < requestsData.length(); i++) {
                        GalleryModel model = new GalleryModel();
                        JSONObject object = requestsData.getJSONObject(i);
                        model.setImage(object.getString("image"));
                        model.setId(object.getString("id"));
                        list.add(model);
                        Log.e("TAG", "getGallery: added");
                    }
                    adapter.notifyDataSetChanged();
                }
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

            binding.noDataLayout.setVisibility(View.VISIBLE);
            Constants.showToast(context, error.getLocalizedMessage());
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("page", "1");
                params.put("project_id", id);
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