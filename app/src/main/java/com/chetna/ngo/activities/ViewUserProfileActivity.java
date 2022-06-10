package com.chetna.ngo.activities;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chetna.ngo.R;
import com.chetna.ngo.databinding.ActivityViewUserProfileBinding;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ViewUserProfileActivity extends AppCompatActivity {


    private ActivityViewUserProfileBinding binding;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(context, R.color.yellow));
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_backspace_24);
        toolbar.setTitle(R.string.request_detail);
        toolbar.setNavigationOnClickListener(v -> finish());
        getUserData(getIntent().getStringExtra("userId"), getIntent().getStringExtra("type"));
        if (getIntent().getStringExtra("type") == null){
            getUserData(getIntent().getStringExtra("userId"), Constants.getString(context,Constants.USER_TYPE));
        }
    }


    private void getUserData(String userID, String type) {
        Constants.showProgressDialog(" Getting User Data!", "Please Wait...", context);
        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.GET_DETAILS_VIA_USERID, response -> {
            try {
                Log.e("TAG", "getUserData: " + response);
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
                Constants.showToast(context, jsonObject.getString("message"));
                JSONObject userData = jsonObject.getJSONObject("0");
                binding.viewProfileName.setText("Name : " + userData.getString("name"));
                binding.viewProfileEmail.setText("Email : " + userData.getString("email"));
                binding.viewProfilePhone.setText("Phone : " + userData.getString("phone"));
                binding.viewProfileBankName.setText("Bank Name : " + userData.getString("bank_name"));
                binding.viewProfileNameOnBank.setText("Name On Bank : " + userData.getString("name_on_bank"));
                binding.viewProfileAcNo.setText("Account Number : " + userData.getString("account_number"));
                binding.viewProfileIFSC.setText("IFSC Code : " + userData.getString("ifsc_code"));
                Picasso.with(context)
                        .load(BaseUrls.BASE_URL + userData.getString("pan_card"))
                        .error(R.drawable.image_error)
                        .into(binding.viewProfilePanCard);
                Picasso.with(context)
                        .load(BaseUrls.BASE_URL + userData.getString("aadhar_front"))
                        .error(R.drawable.image_error)
                        .into(binding.viewProfileAadharCardFront);
                Picasso.with(context)
                        .load(BaseUrls.BASE_URL + userData.getString("aadhar_back"))
                        .error(R.drawable.image_error)
                        .into(binding.viewProfileAadharCardBack);

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
                params.put("user_id", userID);
                params.put("type", type);
                params.put("get", "user");
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