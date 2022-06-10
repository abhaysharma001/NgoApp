package com.chetna.ngo.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chetna.ngo.R;
import com.chetna.ngo.adapters.WorkerAreaAdapter;
import com.chetna.ngo.databinding.ActivityWorkingAreasBinding;
import com.chetna.ngo.models.WorkingAreaModel;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WorkingAreasActivity extends AppCompatActivity {

    private Context context;
    private ActivityWorkingAreasBinding binding;
    private ArrayList<WorkingAreaModel> list = new ArrayList<>();
    private WorkerAreaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWorkingAreasBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        getSupportActionBar().setTitle(R.string.working_areas);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_backspace_24);
        if (Constants.getString(context, Constants.USER_TYPE).equalsIgnoreCase(Constants.USER_TYPE_STREET_CO_ORDINATOR)) {
            binding.bAddWorkingArea.setVisibility(View.VISIBLE);
            if (Constants.isReporting) {
                binding.bAddWorkingArea.setText("Submit Report");
            }
        }
        adapter = new WorkerAreaAdapter(context, list);
        binding.rvWorkingAres.setAdapter(adapter);
        binding.rvWorkingAres.setLayoutManager(new LinearLayoutManager(context));
        binding.rvWorkingAres.hasFixedSize();

        binding.bAddWorkingArea.setOnClickListener(v -> {
            if (Constants.isReporting) {
                if (Constants.getSavedPostList(context).isEmpty()) {
                    Constants.showToast(context, "Select Some Reports First");
                } else {
                    startActivity(new Intent(context, SendReportActivity.class));
                }
            } else {
                showAddDialog();
            }
        });

        loadWorkingArea();
    }

    private void showAddDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_dialog_lyt, null);
        dialogBuilder.setView(dialogView);

        EditText editText = (EditText) dialogView.findViewById(R.id.edWorkingAreaName);
        AppCompatButton bAdd = (AppCompatButton) dialogView.findViewById(R.id.bAddArea);
        AlertDialog alertDialog = dialogBuilder.create();
        bAdd.setOnClickListener(v -> {
            if (editText.getText().toString().isEmpty()) {
                editText.setError("Enter Area Name");
                return;
            }
            showConfirmDialog(editText.getText().toString(), alertDialog);
        });
        alertDialog.show();

    }

    private void showConfirmDialog(String name, AlertDialog Dialog) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context);
        dialogBuilder.setTitle("Confirm!");
        dialogBuilder.setMessage("Add Area");
        dialogBuilder.setIcon(R.drawable.ic_check);
        dialogBuilder.setPositiveButton("Yes", (dialog, which) -> {
            dialog.dismiss();
            Dialog.dismiss();
            addWorkingArea(name);
        });
        dialogBuilder.setNegativeButton("No!", (dialog, which) -> {
            dialog.dismiss();
        });
        dialogBuilder.show();
    }


    public void loadWorkingArea() {
        Constants.showProgressDialog("Loading Working Areas!", "Please Wait...", context);
        list.clear();
        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.GET_WORKING_AREA, response -> {
            Log.e("TAG", "loadWorkingArea: " + response);
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
                Constants.showToast(context, jsonObject.getString("message"));
                JSONArray requestsData = jsonObject.getJSONArray("data");
                if (requestsData.length() > 0) {
                    binding.noDataLayout.setVisibility(View.GONE);
                }
                for (int i = 0; i < requestsData.length(); i++) {
                    JSONObject object = requestsData.getJSONObject(i);
                    WorkingAreaModel model = new WorkingAreaModel();
                    if (object.has("working_area_name")) {
                        model.setWorking_area_name(object.getString("working_area_name"));
                    }
                    if (object.has("project_name")) {
                        model.setProject_name(object.getString("project_name"));
                    }
                    if (object.has("streat_cordinator_id")) {
                        model.setStreat_cordinator_id(object.getString("streat_cordinator_id"));
                    }
                    if (object.has("id")) {
                        model.setId(object.getString("id"));
                    }
                    if (object.has("working_area_id")) {
                        model.setWorking_area_id(object.getString("working_area_id"));
                    }
                    if (object.has("from_time")) {
                        model.setFrom_time(object.getString("from_time"));
                    }
                    if (object.has("to_time")) {
                        model.setTo_time(object.getString("to_time"));
                    }
                    if (object.has("updated_at")) {
                        model.setUpdated_at(object.getString("updated_at"));
                    }
                    list.add(model);
                }
                adapter.notifyDataSetChanged();

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
                params.put("user_id", Constants.getString(context, Constants.USER_ID));
                params.put("user_type", Constants.getString(context, Constants.USER_TYPE));

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

    private void addWorkingArea(String name) {
        Log.e("TAG", "addWorkingArea: " + Constants.getString(context, Constants.USER_TYPE));

        Constants.showProgressDialog("Adding Area!", "Please Wait...", context);


        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.ADD_WORKING_AREA, response -> {
            Log.e("TAG", "addWorkingArea: " + response);
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
                Constants.showToast(context, jsonObject.getString("message"));
                loadWorkingArea();

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
                params.put("working_area_name", name);
                params.put("user_id", Constants.getString(context, Constants.USER_ID));
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