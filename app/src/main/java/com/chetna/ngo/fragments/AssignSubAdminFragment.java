package com.chetna.ngo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chetna.ngo.adapters.SubAdminAdapter;
import com.chetna.ngo.databinding.FragmentAssignSubAdminBinding;
import com.chetna.ngo.models.ProjectModel;
import com.chetna.ngo.models.UserModel;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AssignSubAdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AssignSubAdminFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String project_id,project_name;
    private FragmentAssignSubAdminBinding binding;
    private Context context;
    private ArrayList<UserModel> userModels=new ArrayList<>();
    private SubAdminAdapter subAdminAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public AssignSubAdminFragment() {
        // Required empty public constructor
    }

    public static AssignSubAdminFragment newInstance(String project_id,String project_name) {
        AssignSubAdminFragment fragment = new AssignSubAdminFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, project_id);
        args.putString(ARG_PARAM2, project_name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            project_id = getArguments().getString(ARG_PARAM1);
            project_name = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAssignSubAdminBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        subAdminAdapter=new SubAdminAdapter(userModels,context,project_id,project_name);
        binding.rvSubAdminList.setLayoutManager(new LinearLayoutManager(context));
        binding.rvSubAdminList.setAdapter(subAdminAdapter);
        getSubAdminList();
    }

    private void getSubAdminList() {
        String url = BaseUrls.GET_SUB_ADMIN_LIST;
        Constants.showProgressDialog(" Loading Sub Admin list!", "Please Wait...", context);
        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            Log.e("TAG", "login: " + response);
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
                JSONArray requestsData;
                requestsData = jsonObject.getJSONArray("data");
                if (requestsData.length() > 0) {
                    binding.noDataLayout.setVisibility(View.GONE);
                    for (int i = 0; i < requestsData.length(); i++) {
                        JSONObject object = requestsData.getJSONObject(i);
                        UserModel model=new UserModel();
                        model.setId(object.getString("id"));
                        model.setName(object.getString("name"));
                        model.setEmail(object.getString("email"));
                        model.setPhone(object.getString("phone"));
                        model.setIs_added_to_this_project(object.getBoolean("is_added_to_this_project"));
                        userModels.add(model);
                    }
                    subAdminAdapter.notifyDataSetChanged();
                }
                Constants.showToast(context,jsonObject.getString("message"));

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
                params.put("get","any");
                params.put("project_id",project_id);
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