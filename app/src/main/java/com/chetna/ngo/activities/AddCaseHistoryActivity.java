package com.chetna.ngo.activities;

import static com.chetna.ngo.utils.Constants.hideProgressDialog;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chetna.ngo.R;
import com.chetna.ngo.adapters.PostPreViewAdapter;
import com.chetna.ngo.databinding.ActivityAddCaseHistoryBinding;
import com.chetna.ngo.models.FilesModel;
import com.chetna.ngo.models.PostPreviewModel;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddCaseHistoryActivity extends AppCompatActivity {

    private Context context;
    private ActivityAddCaseHistoryBinding binding;
    private final ArrayList<PostPreviewModel> postPreviewList = new ArrayList<>();
    private final ArrayList<FilesModel> files = new ArrayList<>();
    public static final int IMAGE_REQUEST_CODE = 121;
    private PostPreViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCaseHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        getSupportActionBar().setTitle(R.string.add_case_history);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_backspace_24);

        binding.rvImages.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 2);
        binding.rvImages.setLayoutManager(layoutManager);
        adapter = new PostPreViewAdapter(postPreviewList, context);
        binding.rvImages.setAdapter(adapter);

        binding.bAddImages.setOnClickListener(view -> selectImage());
        binding.bAddCaseHistory.setOnClickListener(view -> {
            String title, des;
            title = binding.edName.getText().toString();
            des = binding.edDescription.getText().toString();
            if (title.isEmpty()) {
                binding.edName.setError("Enter Title!");
                return;
            }
            if (des.isEmpty()) {
                binding.edName.setError("Enter Description!");
                return;
            }
            if (files.size() == 0) {
                Toast.makeText(context, "Select Images!", Toast.LENGTH_SHORT).show();
                return;
            }
            createPost(title, des);
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    String path = Constants.getPath(context, imageUri);
                    File file = new File(path);
                    PostPreviewModel previewModel = new PostPreviewModel(imageUri, file, path);
                    postPreviewList.add(previewModel);
                }
            } else {
                Uri imagePath = data.getData();
                String path = Constants.getPath(context, imagePath);
                File file = new File(path);
                PostPreviewModel previewModel = new PostPreviewModel(imagePath, file, path);
                postPreviewList.add(previewModel);
            }
            for (int i = 0; i < postPreviewList.size(); i++) {
                String filePath = postPreviewList.get(i).getAbsPath();
                File file = new File(filePath);
                files.add(new FilesModel(postPreviewList.get(i).getAbsPath().substring(postPreviewList.get(i).getAbsPath().lastIndexOf("/") + 1),
                        file,
                        postPreviewList.get(i).getAbsPath().substring(postPreviewList.get(i).getAbsPath().lastIndexOf(".") + 1)));
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void checkPermission() {
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createPost(String title, String des) {
        checkPermission();
        try {
            MultipartBody.Builder multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
            for (int i = 0; i < files.size(); i++) {
                RequestBody requestFile = null;
                if (files.get(i).getContent_type().equalsIgnoreCase("png") || files.get(i).getContent_type().equalsIgnoreCase("jpg") || files.get(i).getContent_type().equalsIgnoreCase("jpeg")) {
                    requestFile = RequestBody.create(Compressor.getDefault(context).compressToFile(files.get(i).getFile()), MediaType.parse(files.get(i).getContent_type()));
                } else {
                    requestFile = RequestBody.create(files.get(i).getFile(), MediaType.parse(files.get(i).getContent_type()));
                }

                multipartBody.addFormDataPart("image[" + i + "]", files.get(i).getFile_name(), requestFile);
            }
            multipartBody.addFormDataPart("title", title);
            multipartBody.addFormDataPart("desc", des);
            multipartBody.addFormDataPart("user_id", Constants.getString(context,Constants.USER_ID));

            MultipartBody requestBody = multipartBody.build();
            attemptPost(requestBody);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void selectImage() {
        if (Constants.isStoragePermissionGranted(this)) {
            Intent intent = new Intent();
            intent.setType("*/*");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            }
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_REQUEST_CODE);
        }
    }

    private void attemptPost(RequestBody requestBody) {
        Constants.showProgressDialog("Adding Case History", "Please Wait...", context);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    okhttp3.Request request = new Request.Builder()
                            .header("Accept", "application/json")
                            .url(BaseUrls.CREATE_CASE_HISTORY)
                            .post(requestBody)
                            .build();
                    Response serverresponse = Constants.getClient().newCall(request).execute();
                    String response = serverresponse.body().string();
                    Log.e("TAG", "doInBackground: " + response);
                    if (response.isEmpty()) {
                        runOnUiThread(() -> {
                            hideProgressDialog();
                            Constants.showToast(context, "Some thing went wrong Try Again...");
                        });
                    } else {
                        try {
                            Log.e("ADD_POST_TAG", "doInBackground: " + response);
                            final JSONObject jsonObject = new JSONObject(response);
                            boolean status = jsonObject.getBoolean("status");
                            if (status) {
                                runOnUiThread(() -> {
                                    try {
                                        //  Constant.showToast(context, jsonObject.getString("message"), 0);
                                        hideProgressDialog();
                                        Constants.showToast(context, jsonObject.getString("message"));
                                        files.clear();
                                        adapter.notifyDataSetChanged();
                                        binding.edName.setText("");
                                        binding.edDescription.setText("");
                                        if (Constants.isReporting) {
                                            Constants.savePosts(new ArrayList<String>(), context);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                });
                            } else {
                                runOnUiThread(() -> {
                                    try {
                                        hideProgressDialog();
                                        Constants.showToast(context, jsonObject.getString("message"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            runOnUiThread(() -> {
                                try {
                                    hideProgressDialog();
                                    Constants.showToast(context, "Something Went Wrong Try Again...");
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }
}

