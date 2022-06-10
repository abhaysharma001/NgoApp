package com.chetna.ngo.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
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
import com.chetna.ngo.activities.AttendanceActivity;
import com.chetna.ngo.activities.ChildListActivity;
import com.chetna.ngo.activities.SendReportActivity;
import com.chetna.ngo.activities.ViewFrontlineWorkerListActivity;
import com.chetna.ngo.activities.WorkingAreasActivity;
import com.chetna.ngo.databinding.WorkingAreaItemLayoutBinding;
import com.chetna.ngo.models.WorkingAreaModel;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WorkerAreaAdapter extends RecyclerView.Adapter<WorkerAreaAdapter.WorkerAreaViewHolder> {

    Context context;
    private ArrayList<WorkingAreaModel> list;
    private final String userType;


    public WorkerAreaAdapter(Context context, ArrayList<WorkingAreaModel> list) {
        this.context = context;
        this.list = list;
        userType = Constants.getString(context, Constants.USER_TYPE);
    }

    @NonNull
    @Override
    public WorkerAreaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WorkerAreaViewHolder(WorkingAreaItemLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WorkerAreaViewHolder holder, int position) {
        WorkingAreaModel model = list.get(position);
        holder.binding.areaItemName.setText("Area Name : " + list.get(position).getWorking_area_name());
        if (userType.equals(Constants.USER_TYPE_STREET_CO_ORDINATOR)) {
            holder.binding.areaItemProjectName.setText("Project Name : " + list.get(position).getProject_name());
            holder.binding.ivEditWorkingArea.setVisibility(View.VISIBLE);
            holder.binding.ivEditWorkingArea.setOnClickListener(view -> showEditDialog(position));
            /*holder.itemView.setOnClickListener(view ->  context.startActivity(new Intent(context, ViewStreetCoordinatorLIstActivity.class)
                    .putExtra("name", list.get(position).getWorking_area_name())
                    .putExtra("working_area_id", list.get(position).getId())));*/

            holder.itemView.setOnClickListener(v -> context.startActivity(new Intent(context, ViewFrontlineWorkerListActivity.class)
                    .putExtra("name", list.get(position).getWorking_area_name())
                    .putExtra("working_area_id", list.get(position).getId())));

        } else if (userType.equals(Constants.USER_TYPE_FRONT_LINE_WORKER)) {
            holder.binding.areaItemProjectName.setVisibility(View.GONE);
            holder.binding.fromTime.setVisibility(View.VISIBLE);
            holder.binding.toTime.setVisibility(View.VISIBLE);
            holder.binding.updatedAt.setVisibility(View.VISIBLE);
            holder.binding.fromTime.setText("From : " + model.getFrom_time());
            holder.binding.toTime.setText("To : " + model.getTo_time());
            holder.binding.updatedAt.setText("Last Updated : " + model.getUpdated_at());

//            holder.itemView.setOnClickListener(v -> context.startActivity(new Intent(context, WorkingAreaInfoActivity.class)
//                    .putExtra("name", list.get(position).getWorking_area_name())
//                    .putExtra("working_area_id", list.get(position).getId())));


            holder.itemView.setOnClickListener(v -> {
                openBottomSheetDialog(position);
            });
        } else if (userType.equals(Constants.USER_TYPE_PROJECT_CO_ORDINATOR)) {
            holder.itemView.setOnClickListener(v -> context.startActivity(new Intent(context, ViewFrontlineWorkerListActivity.class)
                    .putExtra("name", list.get(position).getWorking_area_name())
                    .putExtra("working_area_id", list.get(position).getId())));
        }
    }

    private void openBottomSheetDialog(int position) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.activity_working_area_info);

        AppCompatButton bAddWorkingAreaAttendance, bViewStudentList, bViewUpdateTiming, sendReport;

        bAddWorkingAreaAttendance = bottomSheetDialog.findViewById(R.id.bAddWorkingAreaAttendance);
        bViewStudentList = bottomSheetDialog.findViewById(R.id.bViewStudentList);
        bViewUpdateTiming = bottomSheetDialog.findViewById(R.id.bViewUpdateTiming);
        sendReport = bottomSheetDialog.findViewById(R.id.sendReport);
        ImageView close = bottomSheetDialog.findViewById(R.id.close);

        if (close != null) {
            close.findViewById(R.id.close).setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
            });
        }
        if (bAddWorkingAreaAttendance != null) {
            bAddWorkingAreaAttendance.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                context.startActivity(new Intent(context, AttendanceActivity.class)
                        .putExtra("working_area_id", list.get(position).getWorking_area_id()));

            });
        }
        if (sendReport != null) {
            sendReport.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                context.startActivity(new Intent(context, SendReportActivity.class)
                        .putExtra("working_area_id", list.get(position).getWorking_area_id()));

            });
        }
        if (bViewStudentList != null) {
            bViewStudentList.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                context.startActivity(new Intent(context, ChildListActivity.class)
                        .putExtra("name", list.get(position).getWorking_area_name())
                        .putExtra("working_area_id", list.get(position).getWorking_area_id()));

            });
        }
        if (bViewUpdateTiming != null) {
            bViewUpdateTiming.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                updateTimeDialog(position);
            });
        }
        bottomSheetDialog.show();
    }

    private void updateTimeDialog(int position) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.update_time_front_line_worker);

        AppCompatButton update;
        EditText from_time, to_time;

        from_time = bottomSheetDialog.findViewById(R.id.from_time);
        to_time = bottomSheetDialog.findViewById(R.id.to_time);
        update = bottomSheetDialog.findViewById(R.id.update);
        ImageView close = bottomSheetDialog.findViewById(R.id.close);

        if (close != null) {
            close.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
            });
        }

        from_time.setText(list.get(position).getFrom_time());
        to_time.setText(list.get(position).getTo_time());

        if (update != null) {
            update.setOnClickListener(v -> {
                if (from_time.getText().toString().trim().equalsIgnoreCase("")) {
                    from_time.setError("Enter Start Time");
                    from_time.requestFocus();
                } else if (to_time.getText().toString().trim().equalsIgnoreCase("")) {
                    to_time.setError("Enter Start Time");
                    to_time.requestFocus();
                } else {
                    bottomSheetDialog.dismiss();
                    updateTime(from_time.getText().toString().trim(), to_time.getText().toString().trim(), position);
                }

            });
        }


        bottomSheetDialog.show();
    }

    private void updateTime(String startTime, String endTime, int postion) {
        Log.e("TAG", "saveWorkingArea: " + Constants.getString(context, Constants.USER_TYPE));

        Constants.showProgressDialog("Updating Timing!", "Please Wait...", context);


        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.UPDATE_TIME_OF_WORKING_AREA, response -> {
            Log.e("TAG", "saveWorkingArea: " + response);
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
                Constants.showToast(context, jsonObject.getString("message"));
                if (jsonObject.getBoolean("status")) {
                    WorkingAreaModel model = list.get(postion);
                    model.setFrom_time(startTime);
                    model.setTo_time(endTime);
                    list.set(postion, model);
                    notifyItemChanged(postion);
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
                params.put("from_time", startTime);
                params.put("to_time", endTime);
                params.put("working_area_id", list.get(postion).getWorking_area_id());
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

    private void showEditDialog(int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = ((WorkingAreasActivity) context).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_dialog_lyt, null);
        dialogBuilder.setView(dialogView);

        EditText editText = (EditText) dialogView.findViewById(R.id.edWorkingAreaName);
        AppCompatButton bAdd = (AppCompatButton) dialogView.findViewById(R.id.bAddArea);
        AlertDialog alertDialog = dialogBuilder.create();
        editText.setText(list.get(position).getWorking_area_name());
        bAdd.setOnClickListener(v -> {
            if (editText.getText().toString().isEmpty()) {
                editText.setError("Enter Area Name");
                return;
            }
            showConfirmDialog(editText.getText().toString(), list.get(position).getId(), alertDialog, position);
        });
        alertDialog.show();

    }

    private void showConfirmDialog(String name, String id, AlertDialog Dialog, int postion) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context);
        dialogBuilder.setTitle("Confirm!");
        dialogBuilder.setMessage("Change Area Name");
        dialogBuilder.setIcon(R.drawable.ic_check);
        dialogBuilder.setPositiveButton("Yes", (dialog, which) -> {
            dialog.dismiss();
            Dialog.dismiss();
            saveWorkingArea(name, id, postion);
        });
        dialogBuilder.setNegativeButton("No!", (dialog, which) -> {
            dialog.dismiss();
        });
        dialogBuilder.show();
    }

    private void saveWorkingArea(String name, String id, int postion) {
        Log.e("TAG", "saveWorkingArea: " + Constants.getString(context, Constants.USER_TYPE));

        Constants.showProgressDialog("Changing Area Name!", "Please Wait...", context);

        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.UPDATE_WORKING_AREA, response -> {
            Log.e("TAG", "saveWorkingArea: " + response);
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
                Constants.showToast(context, jsonObject.getString("message"));

                if (jsonObject.getBoolean("status")) {
                    WorkingAreaModel model = list.get(postion);
                    model.setWorking_area_name(name);
                    notifyItemChanged(postion);
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
                params.put("working_area_name", name);
                params.put("working_area_id", id);
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


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class WorkerAreaViewHolder extends RecyclerView.ViewHolder {
        private WorkingAreaItemLayoutBinding binding;

        public WorkerAreaViewHolder(@NonNull WorkingAreaItemLayoutBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

}
