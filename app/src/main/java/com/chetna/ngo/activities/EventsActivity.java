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
import com.chetna.ngo.adapters.EventsAdapter;
import com.chetna.ngo.databinding.ActivityCaseHistoryBinding;
import com.chetna.ngo.databinding.ActivityEventsBinding;
import com.chetna.ngo.models.EventsModel;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventsActivity extends AppCompatActivity {

    private Context context;
    private ActivityEventsBinding binding;
    private ArrayList<EventsModel> eventsModelArrayList=new ArrayList<>();
    private EventsAdapter eventsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        getSupportActionBar().setTitle(R.string.event);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_backspace_24);
        binding.rvEventsList.setLayoutManager(new LinearLayoutManager(context));
        binding.rvEventsList.setHasFixedSize(true);
        eventsAdapter=new EventsAdapter(eventsModelArrayList);
        binding.rvEventsList.setAdapter(eventsAdapter);

        getEvents();
    }

    private void getEvents() {
        Log.e("TAG", "getEvents: " + Constants.getString(context, Constants.USER_TYPE));
        Constants.showProgressDialog(" Loading Events!", "Please Wait...", context);
        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.GET_EVENT_LIST, response -> {
            Log.e("TAG", "getEvents: " + response);
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
                        JSONObject object = requestsData.getJSONObject(i);
                        EventsModel model=new EventsModel();
                        model.setId(object.getString("id"));
                        model.setEvent_name(object.getString("event_name"));
                        model.setEvent_desc(object.getString("event_desc"));
                        model.setEvent_date(object.getString("event_date"));
                        model.setEvent_time(object.getString("event_time"));
                        eventsModelArrayList.add(model);
                    }
                    eventsAdapter.notifyDataSetChanged();
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
            Log.e("TAG", "getEvents: "+error.getLocalizedMessage() );
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