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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chetna.ngo.databinding.FragmentCreateSubAdminBinding;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateSubAdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateSubAdminFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentCreateSubAdminBinding binding;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public CreateSubAdminFragment() {
        // Required empty public constructor
    }

    public static CreateSubAdminFragment newInstance() {
        CreateSubAdminFragment fragment = new CreateSubAdminFragment();
        Bundle args = new Bundle();
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
        binding = FragmentCreateSubAdminBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.bSubmit.setOnClickListener(v -> {
            String name, email, phone, password;
            name = binding.edName.getText().toString();
            email = binding.edEmail.getText().toString();
            phone = binding.edPhone.getText().toString();
            password = binding.edPassword.getText().toString();
            if (name.isEmpty()) {
                binding.edName.setError("Enter Name!");
                binding.edName.requestFocus();
                return;
            }
            if (email.isEmpty()) {
                binding.edEmail.setError("Enter Email!");
                binding.edEmail.requestFocus();
                return;
            }
            if (phone.isEmpty()) {
                binding.edPhone.setError("Enter Phone Number!");
                binding.edPhone.requestFocus();
                return;
            }
            if (password.isEmpty()) {
                binding.edPassword.setError("Enter Password!");
                binding.edPassword.requestFocus();
                return;
            }
            createSubAdmin(name, email, phone, password);
        });
    }

    private void createSubAdmin(String name, String email, String number, String password) {
        Constants.showProgressDialog(" Adding Event!", "Please Wait...", context);
        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.REGISTER, response -> {
            Log.e("TAG", "gallery: " + response);
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("status")) {
                    if (jsonObject.getBoolean("status")) {
                        binding.edPassword.setText("");
                        binding.edEmail.setText("");
                        binding.edPhone.setText("");
                        binding.edName.setText("");
                        Constants.showToast(context, "Sub Admin Create Successfully");
                    } else {
                        Constants.showToast(context, jsonObject.getString("message"));
                    }
                } else {
                    Constants.showToast(context, "Something Went Wrong Try Again...");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Constants.showToast(context, "Something Went Wrong Try Again...");
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
                params.put("name", name);
                params.put("email", email);
                params.put("number", number);
                params.put("password", password);
                params.put("type", "6");
                params.put("register", "any");
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