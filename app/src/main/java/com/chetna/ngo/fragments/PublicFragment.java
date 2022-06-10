package com.chetna.ngo.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chetna.ngo.R;
import com.chetna.ngo.activities.AddSuggestionsActivity;
import com.chetna.ngo.adapters.SliderAdapterExample;
import com.chetna.ngo.models.SliderItem;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PublicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PublicFragment extends Fragment {

    private Context context;

    private TextView tvAdminMessage;

    SliderView sliderView;
    SliderAdapterExample adapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void loadSlider() {

        adapter = new SliderAdapterExample(context);
        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
        sliderView.startAutoCycle();
        getSliderData();
    }

    private void getSliderData() {
        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.GET_SLIDER_IMAGE, response -> {
            Log.e("TAG", "changeMessage: " + response);
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
                JSONArray requestsData;
                requestsData = jsonObject.getJSONArray("data");
                if (requestsData.length() > 0) {
                    for (int i = 0; i < requestsData.length(); i++) {
                        JSONObject object = requestsData.getJSONObject(i);
                        SliderItem model = new SliderItem(object.getString("text"), object.getString("image"), object.getString("id"), object.getString("text"), object.getString("project_id"));
                        adapter.addItem(model);
                    }
                }
                Constants.showToast(context, jsonObject.getString("message"));
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
                params.put("project_id", "0");
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


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PublicFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PublicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PublicFragment newInstance(String param1, String param2) {
        PublicFragment fragment = new PublicFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_public, container, false);

        view.findViewById(R.id.addPostLyt)
                .setOnClickListener(view1 -> {
                    Constants.showToast(context, "This Feature Launch Soon");
                    //startActivity(new Intent(context, AddPostActivity.class);
                });

        LinearLayout Lyt = view.findViewById(R.id.adminLyt);
        tvAdminMessage = Lyt.findViewById(R.id.marqueText);
        sliderView = Lyt.findViewById(R.id.imageSlider);
        loadSlider();
        getAdminMessage();
        view.findViewById(R.id.addSuggestionsLyt)
                .setOnClickListener(view1 -> startActivity(new Intent(context, AddSuggestionsActivity.class)));

        view.findViewById(R.id.donateLyt)
                .setOnClickListener(view1 ->
                        Toast.makeText(context, "Donation!", Toast.LENGTH_SHORT).show());


        return view;
    }

    private void getAdminMessage() {
        Constants.showProgressDialog("Loading Data!", "Please Wait.....", context);
        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.GET_ADMIN_MESSAGE, response -> {
            Log.e("TAG", "getAdminMessage: " + response);
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
                Constants.showToast(context, jsonObject.getString("message"));
                if (jsonObject.getBoolean("status")) {
                    JSONObject userData = jsonObject.getJSONObject("data");
                    tvAdminMessage.setText(userData.getString("message").toString());

                    Log.e("TAG", "getAdminMessage: " + userData);
                }
            } catch (JSONException e) {
                Log.e("TAG", "getAdminMessage: " + e.getLocalizedMessage());
            }
        }, error -> {
            Constants.hideProgressDialog();
            if (error instanceof NetworkError || error instanceof TimeoutError) {
                Constants.showToast(context, "Please Check your Internet connection");
                return;
            }
            Constants.showToast(context, error.getLocalizedMessage());
        });
        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        request.setRetryPolicy(new
                DefaultRetryPolicy(
                Constants.CONNECTION_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);


    }

}