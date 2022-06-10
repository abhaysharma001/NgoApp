package com.chetna.ngo.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chetna.ngo.R;
import com.chetna.ngo.adapters.PostPreViewAdapter;
import com.chetna.ngo.models.FilesModel;
import com.chetna.ngo.models.GalleryModel;
import com.chetna.ngo.models.PostPreviewModel;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddEventActivity extends AppCompatActivity {

    private Context context;
    private final ArrayList<PostPreviewModel> postPreviewList = new ArrayList<>();
    private final ArrayList<FilesModel> files = new ArrayList<>();
    public static final int IMAGE_REQUEST_CODE = 121;
    private PostPreViewAdapter adapter;
    private RecyclerView rvImages;
    private AppCompatButton bAddEvent;
    private ImageView bAddImages;
    private EditText edTaskName, edDescription;
    private TextView start_date, event_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        context = this;

        edDescription = findViewById(R.id.edDescription);
        edTaskName = findViewById(R.id.edName);
        start_date = findViewById(R.id.start_date);
        event_time = findViewById(R.id.event_time);
        rvImages = findViewById(R.id.rvImages);
        bAddImages = findViewById(R.id.bAddImages);
        getSupportActionBar().setTitle(R.string.add_event);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_backspace_24);
        rvImages.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 2);
        rvImages.setLayoutManager(layoutManager);
        adapter = new PostPreViewAdapter(postPreviewList, context);
        rvImages.setAdapter(adapter);

        bAddEvent = findViewById(R.id.bAddEvent);
        bAddEvent.setOnClickListener(view -> {
            String title, des, start_date1, start_time;
            title = edTaskName.getText().toString();
            des = edDescription.getText().toString();
            start_date1 = start_date.getText().toString();
            start_time = event_time.getText().toString();
            if (title.isEmpty()) {
                edTaskName.setError("Enter Event Name!");
                return;
            }
            if (des.isEmpty()) {
                edTaskName.setError("Enter Description!");
                return;
            }
            if (start_date1.isEmpty()) {
                edTaskName.setError("Enter Event Name!");
                return;
            }
            if (start_time.isEmpty()) {
                edTaskName.setError("Enter Description!");
                return;
            }

            createPost(title, des, start_date1, start_time);
        });

        start_date.setOnClickListener(v -> {
            showDatePicker();
        });
        event_time.setOnClickListener(v -> {
            timePicker();
        });
    }

    private void timePicker() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE) + 5;
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                Log.e("TAG", "onTimeSet: " + selectedHour);
                String am_pm;
                if (selectedHour > 12) {
                    selectedHour = selectedHour - 12;
                    am_pm = "PM";
                } else if (selectedHour == 12) {
                    am_pm = "PM";
                } else {
                    am_pm = "AM";
                }
                event_time.setText(selectedHour + ":" + selectedMinute + " " + am_pm);
            }
        }, hour, minute, false);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }


    private void showDatePicker() {
        // check if activity not null otherwise your app is crashed
        if (AddEventActivity.this != null) {
            // create new object of calendar everytime else when you reselect the date it bydefault chose prevoius one.
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
                // TODO Auto-generated method stub
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "yyyy/MM/dd"; //Here you can change you desire format of date
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                // here pass the select date to matchDate method
                start_date.setText(sdf.format(calendar.getTime()));
            };

            DatePickerDialog datePickerDialog = new DatePickerDialog(AddEventActivity.this, date, calendar
                    .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            // this line for restrict your calendar to select future dates
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        }
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

    private void createPost(String title, String des, String date, String time) {
        Constants.showProgressDialog(" Adding Event!", "Please Wait...", context);
        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.CRAETE_EVENT, response -> {
            Log.e("TAG", "gallery: " + response);
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.has("status")){
                    if(jsonObject.getBoolean("status")){
                        start_date.setText("");
                        event_time.setText("");
                        edDescription.setText("");
                        edTaskName.setText("");
                        Constants.showToast(context,jsonObject.getString("message"));
                    }else{
                        Constants.showToast(context,jsonObject.getString("message"));
                    }
                }else{
                    Constants.showToast(context,"Something Went Wrong Try Again...");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Constants.showToast(context,"Something Went Wrong Try Again...");
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
                params.put("event_name", title);
                params.put("event_desc", des);
                params.put("event_date", date);
                params.put("event_time", time);
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