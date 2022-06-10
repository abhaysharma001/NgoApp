package com.chetna.ngo.activities;

import static com.chetna.ngo.utils.Constants.hideProgressDialog;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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
import com.chetna.ngo.databinding.ActivityUploadDocumentsBinding;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadDocuments extends AppCompatActivity {

    private Context context;
    int SELECT_PICTURE = 200;
    private View selectedView = null;
    ActivityUploadDocumentsBinding binding;
    private String projectName;
    private Uri aadharCardFrontUri, aadharCardFBackUri, panCardFrontUri;

    String bloodGroup, bankName, accountNumber, ifscCode, nameOnBank, panCardNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadDocumentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;


        getSupportActionBar().setTitle(R.string.upload_document);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_backspace_24);


        binding.aadharCardFront.setOnClickListener(v -> {
            selectedView = binding.aadharCardFront;
            imageChooser();
        });
        binding.aadharCardBack.setOnClickListener(v -> {
            selectedView = binding.aadharCardBack;
            imageChooser();
        });
        binding.panCardFront.setOnClickListener(v -> {
            selectedView = binding.panCardFront;
            imageChooser();
        });


        binding.bContinue.setOnClickListener(v -> {
            try {
                validateData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        getData();
        Log.e("TAG", "onCreate: "+Constants.getString(context,Constants.USER_ID) );
        getUserData(Constants.getString(context,Constants.USER_ID),Constants.getString(context,Constants.USER_TYPE));

    }

    private void getData() {

        panCardNumber = Constants.getString(context, Constants.PAN_CARD);
        bloodGroup = Constants.getString(context, Constants.BLOOD_GROUP);
        bankName = Constants.getString(context, Constants.BANK_NAME);
        accountNumber = Constants.getString(context, Constants.AC_NO);
        ifscCode = Constants.getString(context, Constants.IFSC);
        nameOnBank = Constants.getString(context, Constants.NAME_ON_BANK);


        binding.bloodGroup.setText(bloodGroup);

        binding.bankName.setText(bankName);

        binding.accountNumber.setText(accountNumber);

        binding.nameOnBank.setText(nameOnBank);

        binding.ifscCode.setText(ifscCode);

//        binding.panCardNumber.setText(panCardNumber);


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
                if (!userData.getString("pan_card").equalsIgnoreCase("")) {


                    binding.bloodGroup.setText(userData.getString("blood_group"));
                    binding.nameOnBank.setText(userData.getString("pan"));
                    binding.bankName.setText(userData.getString("pan_card"));
                    binding.accountNumber.setText(userData.getString("account_number"));
                    binding.ifscCode.setText(userData.getString("ifsc_code"));

                    Picasso.with(context)
                            .load(BaseUrls.BASE_URL + userData.getString("pan_card"))
                            .error(R.drawable.image_error)
                            .into(binding.panCardFront);
                    binding.panCardFront.setEnabled(false);
                    Picasso.with(context)
                            .load(BaseUrls.BASE_URL + userData.getString("aadhar_front"))
                            .error(R.drawable.image_error)
                            .into(binding.aadharCardFront);
                    binding.aadharCardFront.setEnabled(false);
                    Picasso.with(context)
                            .load(BaseUrls.BASE_URL + userData.getString("aadhar_back"))
                            .error(R.drawable.image_error)
                            .into(binding.aadharCardBack);
                    binding.aadharCardBack.setEnabled(false);
                }else {
                    binding.lockedView.setVisibility(View.GONE);
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
            Constants.showToast(context, error.getLocalizedMessage());
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userID);
                params.put("type", type);
                params.put("get", "user");
                Log.e("TAG", "getParams: "+params );
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


    private void imageChooser() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(((Activity) context), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 201);
        }
        if (selectedView != null) {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            Uri selectedImageUri = data.getData();
            if (requestCode == SELECT_PICTURE) {
                if (selectedView == binding.aadharCardFront) {
                    aadharCardFrontUri = selectedImageUri;
                }
                if (selectedView == binding.aadharCardBack) {
                    aadharCardFBackUri = selectedImageUri;
                }
                if (selectedView == binding.panCardFront) {
                    panCardFrontUri = selectedImageUri;
                }
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    ((ImageView) selectedView).setImageURI(selectedImageUri);
                    selectedView = null;
                }
            }
        }
    }


    private String getRealPathFromURI(Uri contentURI) {
        return Constants.getPath(context, contentURI);
    }

    private File getFileFromPath(String path) {
        try {
            return new File(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private void createUser() throws Exception {
        Constants.showProgressDialog("Creating user", "Please Wait....", context);
        MultipartBody.Builder multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM);


        if (binding.aadharCardBack.isEnabled()) {

            if (aadharCardFrontUri != null) {

                String path = getRealPathFromURI(aadharCardFrontUri);
                File file = getFileFromPath(path);
                if (file != null) {
                    file = Compressor.getDefault(context).compressToFile(file);
                    RequestBody requestDlFile = RequestBody.create(file, MediaType.parse(path.substring(getRealPathFromURI(aadharCardFrontUri).lastIndexOf(".") + 1)));
                    multipartBody.addFormDataPart("aadhar_front", path.substring(path.lastIndexOf("/") + 1), requestDlFile);
                }
            } else {
                Toast.makeText(context, "Please Select Aadhar Card Image!", Toast.LENGTH_SHORT).show();
            }
            if (aadharCardFBackUri != null) {
                String path = getRealPathFromURI(aadharCardFBackUri);
                File file = getFileFromPath(path);
                if (file != null) {
                    file = Compressor.getDefault(context).compressToFile(file);
                    RequestBody requestDlFile = RequestBody.create(file, MediaType.parse(path.substring(path.lastIndexOf(".") + 1)));
                    multipartBody.addFormDataPart("aadhar_back", path.substring(path.lastIndexOf("/") + 1), requestDlFile);
                }
            } else {
                Toast.makeText(context, "Please Select Aadhar Card Image!", Toast.LENGTH_SHORT).show();
            }

            if (panCardFrontUri != null) {
                String path = getRealPathFromURI(panCardFrontUri);
                File file = getFileFromPath(path);
                if (file != null) {
                    file = Compressor.getDefault(context).compressToFile(file);
                    RequestBody requestDlFile = RequestBody.create(file, MediaType.parse(path.substring(path.lastIndexOf(".") + 1)));
                    multipartBody.addFormDataPart("pan_card", path.substring(path.lastIndexOf("/") + 1), requestDlFile);
                }
            } else {
                Toast.makeText(context, "Please Select Pan Card Image!", Toast.LENGTH_SHORT).show();
            }
        }


        multipartBody.addFormDataPart("blood_group", bloodGroup);
        multipartBody.addFormDataPart("account_number", accountNumber);
        multipartBody.addFormDataPart("password", getIntent().getStringExtra("password"));
        multipartBody.addFormDataPart("bank_name", bankName);
        multipartBody.addFormDataPart("name_on_bank", nameOnBank);
        multipartBody.addFormDataPart("ifsc_code", ifscCode);
        multipartBody.addFormDataPart("project_name", projectName);


        MultipartBody requestBody = multipartBody.build();
        attemptPost(requestBody);
        throw new Exception("device error");
    }


    private void validateData() throws Exception {

        // aadharCardNumber = binding.aadharCardNumber.getText().toString().trim();
      //  panCardNumber = binding.panCardNumber.getText().toString().trim();
        bloodGroup = binding.bloodGroup.getText().toString().trim();
        bankName = binding.bankName.getText().toString().trim();
        accountNumber = binding.accountNumber.getText().toString().trim();
        ifscCode = binding.ifscCode.getText().toString().trim();
        nameOnBank = binding.nameOnBank.getText().toString().trim();


        if (bloodGroup.isEmpty()) {
            binding.bloodGroup.setError("Enter Blood Group!");
            return;
        }
        if (bankName.isEmpty()) {
            binding.bankName.setError("Enter Bank Name!");
            return;
        }
        if (accountNumber.isEmpty()) {
            binding.accountNumber.setError("Enter Account Number!");
            return;
        }
        if (nameOnBank.isEmpty()) {
            binding.nameOnBank.setError("Enter Name!");
            return;
        }
        if (ifscCode.isEmpty()) {
            binding.ifscCode.setError("Enter IFSC Code!");
            return;
        }
       /* if (panCardNumber.isEmpty()) {
            binding.panCardNumber.setError("Enter IFSC Code!");
            return;
        }*/
        createUser();


    }


    private void attemptPost(final RequestBody requestBody) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .header("Accept", "application/json")
                            .url(BaseUrls.UPDATE_PAN_AND_AADHAR)
                            .post(requestBody)
                            .build();
                    Response serverresponse = Constants.getClient().newCall(request).execute();
                    String response = serverresponse.body().string();
                    Constants.hideProgressDialog();
                    Log.e("TAG", "doInBackground: " + response);
                    if (response.isEmpty()) {
                        Handler handler = new Handler(getMainLooper());
                        handler.post(() -> Constants.showToast(context, "Some thing went wrong Try Again..."));
                    } else {
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            final JSONObject jsonObject = jObject.getJSONObject("0");
                            String status = jObject.getString("status");
                            if (status.equalsIgnoreCase("true")) {
                                runOnUiThread(() -> {
                                    hideProgressDialog();
                                    //   saveUserData();
                                });
                            } else {
                                runOnUiThread(() -> {
                                    try {
                                        String message = jsonObject.getString("message");
                                        hideProgressDialog();
                                        Constants.showToast(context, message);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        runOnUiThread(() -> hideProgressDialog());
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            runOnUiThread(() -> hideProgressDialog());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> hideProgressDialog());
                }
                return null;
            }
        }.execute();
    }

}