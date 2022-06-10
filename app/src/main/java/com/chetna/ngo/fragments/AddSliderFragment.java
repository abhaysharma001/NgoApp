package com.chetna.ngo.fragments;

import static android.app.Activity.RESULT_OK;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.bumptech.glide.Glide;
import com.chetna.ngo.R;
import com.chetna.ngo.adapters.AddSliderAdapter;
import com.chetna.ngo.databinding.AddSliderBottomSheetBinding;
import com.chetna.ngo.databinding.FragmentAddSliderBinding;
import com.chetna.ngo.interfaces.OpenBottomSheetDialog;
import com.chetna.ngo.models.SliderItem;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddSliderFragment extends Fragment implements OpenBottomSheetDialog {

    private static final String ARG_PARAM1 = "param1";
    private String project_id;
    private Context context;
    private FragmentAddSliderBinding binding;
    private AddSliderAdapter adapter;
    private ArrayList<SliderItem> sliderItemArrayList = new ArrayList<>();
    private LayoutInflater layoutInflater1;
    private ViewGroup viewGroup;
    BottomSheetDialog bottomSheetDialog;
    AddSliderBottomSheetBinding binding1;
    private boolean isUpdateImage = false;
    int SELECT_PICTURE = 200;
    private Uri imageUri;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public AddSliderFragment() {
        // Required empty public constructor
    }

    public static AddSliderFragment newInstance(String param1) {
        AddSliderFragment fragment = new AddSliderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    private void imageChooser() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(((Activity) getActivity()), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 201);
        } else {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            project_id = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layoutInflater1 = inflater;
        viewGroup = container;
        binding = FragmentAddSliderBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new AddSliderAdapter(sliderItemArrayList, context, this);
        binding.rvSliderList.setLayoutManager(new LinearLayoutManager(context));
        binding.rvSliderList.setAdapter(adapter);

        loadSliderForProject();

        binding.addSliderImage.setOnClickListener(v -> {
            openBottomSheetDialog(false, null);
        });

    }

    private void openBottomSheetDialog(boolean isUpdate, SliderItem sliderItem) {
        if (bottomSheetDialog == null) {
            bottomSheetDialog = new BottomSheetDialog(context);
            binding1 = AddSliderBottomSheetBinding.inflate(layoutInflater1, viewGroup, false);
            bottomSheetDialog.setContentView(binding1.getRoot());
        }
        isUpdateImage = false;
        imageUri = null;
        if (isUpdate) {
            binding1.titleSlider.setText("Update Slider Image");
            binding1.bSubmit.setText("Update Slider Image");
            Glide.with(context)
                    .load(BaseUrls.BASE_URL + sliderItem.getImage())
                    .into(binding1.sliderImage);
        } else {
            binding1.titleSlider.setText("Add Slider Image");
            binding1.bSubmit.setText("Add Slider Image");
            binding1.sliderImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_add));
        }

        binding1.bSubmit.setOnClickListener(v -> {
            if (binding1.edTitle.getText().toString().trim().equalsIgnoreCase("")) {
                binding1.edTitle.setError("Enter Title For Slider");
                binding1.edTitle.requestFocus();
                return;
            }
            if (isUpdate) {
                try {
                    attemptSlider(binding1.edTitle.getText().toString().trim(), true, sliderItem.getId());
                } catch (Exception e) {
                    Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                if (imageUri == null) {
                    Constants.showToast(context, "Please Select Image");
                    return;
                }
                try {
                    attemptSlider(binding1.edTitle.getText().toString().trim(), false, null);
                } catch (Exception e) {
                    Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        binding1.sliderImage.setOnClickListener(v -> {
            imageChooser();
        });
        binding1.close.setOnClickListener(v -> {
            if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.show();
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

    private void attemptSlider(String title, boolean isUpdate, String id) throws Exception {
        MultipartBody.Builder multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (imageUri != null) {
            String path = getRealPathFromURI(imageUri);
            File file = getFileFromPath(path);
            if (file != null) {
                file = Compressor.getDefault(context).compressToFile(file);
                RequestBody requestDlFile = RequestBody.create(file, MediaType.parse(path.substring(getRealPathFromURI(imageUri).lastIndexOf(".") + 1)));
                multipartBody.addFormDataPart("slider_image", path.substring(path.lastIndexOf("/") + 1), requestDlFile);
            }
        } else {
            Toast.makeText(context, "Please Select Image!", Toast.LENGTH_SHORT).show();
            return;
        }
        multipartBody.addFormDataPart("text", title);
        String ti = "Adding Slider";
        if (isUpdate) {
            ti = "Updating Slider";
            multipartBody.addFormDataPart("id", id);
            multipartBody.addFormDataPart("status", "2");
        } else {
            multipartBody.addFormDataPart("status", "1");
        }
        multipartBody.addFormDataPart("user_id", Constants.getString(context, Constants.USER_ID));
        Constants.showProgressDialog(ti, "Please Wait....", context);

        MultipartBody requestBody = multipartBody.build();
        String url = BaseUrls.ADD_SLIDER_IMAGE;

        attemptPost(requestBody, url);
    }

    private void attemptPost(final RequestBody requestBody, String url) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .header("Accept", "application/json")
                            .url(url)
                            .post(requestBody)
                            .build();
                    Response serverresponse = Constants.getClient().newCall(request).execute();
                    String response = serverresponse.body().string();
                    hideProgressDialog();
                    Log.e("TAG", "doInBackground: " + response);
                    if (response.isEmpty()) {
                        Handler handler = new Handler(context.getMainLooper());
                        handler.post(() -> Constants.showToast(context, "Some thing went wrong Try Again..."));
                    } else {
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            boolean status = jObject.getBoolean("status");
                            if (status) {
                                getActivity().runOnUiThread(() -> {
                                    hideProgressDialog();
                                    try {
                                        Constants.showToast(context, jObject.getString("message"));
                                        loadSliderForProject();
                                        bottomSheetDialog.dismiss();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                });
                            } else {
                                getActivity().runOnUiThread(() -> {
                                    try {
                                        String message = jObject.getString("message");
                                        hideProgressDialog();
                                        Constants.showToast(context, message);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        getActivity().runOnUiThread(() -> hideProgressDialog());
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            getActivity().runOnUiThread(() -> hideProgressDialog());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(() -> hideProgressDialog());
                }
                return null;
            }
        }.execute();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            Uri selectedImageUri = data.getData();
            if (requestCode == SELECT_PICTURE) {
                imageUri = selectedImageUri;
            }
            if (null != selectedImageUri) {
                isUpdateImage = true;
                // update the preview image in the layout
                binding1.sliderImage.setImageURI(selectedImageUri);
            }
        }

    }

    private void loadSliderForProject() {
        Constants.showProgressDialog("Getting Slider Data!", "Please Wait...", context);
        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.GET_SLIDER_IMAGE, response -> {
            Log.e("TAG", "changeMessage: " + response);
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
                JSONArray requestsData;
                requestsData = jsonObject.getJSONArray("data");
                if (requestsData.length() > 0) {
                    sliderItemArrayList.clear();
                    binding.noDataLayout.setVisibility(View.GONE);
                    for (int i = 0; i < requestsData.length(); i++) {
                        JSONObject object = requestsData.getJSONObject(i);
                        SliderItem model = new SliderItem(object.getString("text"), object.getString("image"), object.getString("id"), object.getString("text"), object.getString("project_id"));
                        sliderItemArrayList.add(model);
                    }
                    adapter.notifyDataSetChanged();
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
                params.put("project_id", project_id);
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

    @Override
    public void openBottomSheet(boolean isUpdate, SliderItem sliderItem) {
        openBottomSheetDialog(isUpdate, sliderItem);
    }
}