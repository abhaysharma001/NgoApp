package com.chetna.ngo.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
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
import com.chetna.ngo.activities.EditProject;
import com.chetna.ngo.activities.GalleryActivity;
import com.chetna.ngo.activities.OpenFragmentsActivity;
import com.chetna.ngo.activities.ProjectListActivity;
import com.chetna.ngo.activities.ViewAttendanceActivity;
import com.chetna.ngo.activities.WorkerRequestsActivity;
import com.chetna.ngo.databinding.ProjectItemListBinding;
import com.chetna.ngo.models.ProjectModel;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.ViewHolder> {

    private ArrayList<ProjectModel> list;
    private Context context;
    private String from;

    public ProjectListAdapter(ArrayList<ProjectModel> list, Context context, String from) {
        this.list = list;
        this.context = context;
        this.from = from;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ProjectItemListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProjectModel model = list.get(position);

        if (Constants.getString(context, Constants.USER_TYPE).equals(Constants.USER_TYPE_ADMIN)) {
            if ((Constants.getString(context, Constants.LOGIN).equals(Constants.YES))) {
                holder.binding.ivEdit.setVisibility(View.VISIBLE);
                holder.binding.ivDelete.setVisibility(View.VISIBLE);
                holder.binding.ivEdit.setOnClickListener(view -> context.startActivity(new Intent(context, EditProject.class)
                        .putExtra("status", "update")
                        .putExtra("projectId", model.getProject_id())));
                holder.binding.ivDelete.setOnClickListener(view -> createDeleteBottomDialog(model));
            }

        }
        holder.binding.projectName.setText(model.getProject_name());


        holder.binding.projectName.setOnClickListener(view -> {
            if (from.equals("g")) {
                Log.e("TAG", "onBindViewHolder: " + model.getId());
                context.startActivity(new Intent(context, GalleryActivity.class)
                        .putExtra("project_id", model.getId()));
            } else {
                if (from.equalsIgnoreCase("assign_sub_admin")) {
                    context.startActivity(new Intent(context, OpenFragmentsActivity.class)
                            .putExtra("type", "assign_sub_admin")
                            .putExtra("project_id", model.getId())
                            .putExtra("project_name", model.getProject_name()));
                } else {
                    if (Constants.getString(context, Constants.USER_TYPE).equals(Constants.USER_TYPE_ADMIN) || Constants.getString(context, Constants.USER_TYPE).equals(Constants.USER_TYPE_SUB_ADMIN)) {
                        openBottomSheet(model);
                    } else {
                        context.startActivity(new Intent(context, ViewAttendanceActivity.class)
                                .putExtra("project_id", model.getId()));
                    }
                }
            }

        });


    }


    private void createDeleteBottomDialog(ProjectModel model) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_delete_proj);

        ImageView close;
        AppCompatButton bDeleteProject;
        close = bottomSheetDialog.findViewById(R.id.close);
        bDeleteProject = bottomSheetDialog.findViewById(R.id.bDeleteProject);

        close.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });
        bDeleteProject.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            deleteProject(model.getId());
        });


        bottomSheetDialog.show();
    }

    private void deleteProject(String project_id) {


        Log.e("TAG", "deleteProject: " + Constants.getString(context, Constants.USER_TYPE));
        Constants.showProgressDialog("Deleting Project".toUpperCase(), "Please Wait...", context);
        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.ADMIN_CREATE_PROJECT, response -> {
            Log.e("TAG", "deleteProject: " + response);
            try {

                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
                Constants.showToast(context, jsonObject.getString("message"));
                if (jsonObject.getBoolean("status")) {
                    ((ProjectListActivity) context).getProjectList(null, false);
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
                params.put("delete", "any");
                params.put("project_id", project_id);
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

    private void openBottomSheet(ProjectModel model) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_for_admin);

        ImageView close;
        AppCompatButton bChangeSliderImage, bViewProjectCordinatorList;
        close = bottomSheetDialog.findViewById(R.id.close);
        bChangeSliderImage = bottomSheetDialog.findViewById(R.id.bChangeSliderImage);
        bViewProjectCordinatorList = bottomSheetDialog.findViewById(R.id.bViewProjectCordinatorList);

        close.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });
        bChangeSliderImage.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            context.startActivity(new Intent(context, OpenFragmentsActivity.class)
                    .putExtra("project_id", model.getId())
                    .putExtra("type", "Change Slider Images"));
        });
        bViewProjectCordinatorList.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            context.startActivity(new Intent(context, WorkerRequestsActivity.class)
                    .putExtra("project_id", model.getId()));
        });


        bottomSheetDialog.show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ProjectItemListBinding binding;

        public ViewHolder(@NonNull ProjectItemListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
