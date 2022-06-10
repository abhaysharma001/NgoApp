package com.chetna.ngo.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chetna.ngo.R;
import com.chetna.ngo.databinding.ActivityAddStudentBinding;
import com.chetna.ngo.databinding.ActivityAddSuggestionsBinding;
import com.chetna.ngo.models.StudentModel;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddStudentActivity extends AppCompatActivity {

    private RadioButton radioSingle, radioCsv;
    private LinearLayout layoutSingle, layoutCsv;
    private ActivityAddStudentBinding binding;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = this;

        radioSingle = binding.radioSingleStudent;
        radioCsv = binding.radioStudentCsv;
        layoutCsv = binding.selectCsvLyt;
        layoutSingle = binding.addSingleStudentLyt;


        getSupportActionBar().setTitle(R.string.add_student);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_backspace_24);


        radioCsv.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                if (radioSingle.isChecked())
                    radioSingle.setChecked(false);

                if (layoutSingle.getVisibility() == View.VISIBLE)
                    layoutSingle.setVisibility(View.GONE);

                if (layoutCsv.getVisibility() == View.GONE)
                    layoutCsv.setVisibility(View.VISIBLE);

            }
        });
        radioSingle.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                if (radioCsv.isChecked())
                    radioCsv.setChecked(false);

                if (layoutCsv.getVisibility() == View.VISIBLE)
                    layoutCsv.setVisibility(View.GONE);

                if (layoutSingle.getVisibility() == View.GONE)
                    layoutSingle.setVisibility(View.VISIBLE);

            }
        });

        binding.bAddChild.setOnClickListener(v -> {
            String name, age, gender = "";

            name = binding.edChildName.getText().toString();
            age = binding.edChildAge.getText().toString();

            if (binding.maleChild.isChecked())
                gender = "Male";
            if (binding.femaleChild.isChecked())
                gender = "Female";
            if (binding.otherChild.isChecked())
                gender = "Others";

            if (name.isEmpty()) {
                binding.edChildName.setError("Enter Name!");
                return;
            }
            if (age.isEmpty()) {
                binding.edChildAge.setError("Enter Age!");
                return;
            }

            addSingleStudent(name, age, gender, binding.remarkChild.getText().toString());


        });
    }

    private void addSingleStudent(String name, String age, String gender, String remarkChild) {
        Constants.showProgressDialog("Adding Student!", "Please Wait...", context);
        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.ADD_Student, response -> {
            Log.e("TAG", "addStudent: " + response);
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
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
                params.put("name", name);
                params.put("gender", gender);
                params.put("remark", remarkChild);
                params.put("age", age);
                params.put("working_area_id", getIntent().getStringExtra("working_area_id"));
                params.put("user_id", Constants.getString(context,Constants.USER_ID));
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        ((Activity) context).finish();
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<StudentModel> readStudentCsv(String path){
        ArrayList<StudentModel> list = new ArrayList<>();
        String line  = "";

        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            while((line = br.readLine())!=null){
                String[] values = line.split(",");
                StudentModel model = new StudentModel();
                model.setName(values[0]);
                model.setAge(values[1]);
                model.setGender(values[2]);
                model.setRemark(values[3]);
                list.add(model);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

}