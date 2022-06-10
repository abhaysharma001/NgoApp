package com.chetna.ngo.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.chetna.ngo.activities.ViewUserProfileActivity;
import com.chetna.ngo.activities.ViewUserReportingActivity;
import com.chetna.ngo.models.ProjectModel;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WorkerRequestAdapter extends RecyclerView.Adapter<WorkerRequestAdapter.WorkerViewHolder> {

    Context context;
    private ArrayList<ProjectModel> list;


    public WorkerRequestAdapter(Context context, ArrayList<ProjectModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public WorkerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.verify_user_item_layout, parent, false);
        return new WorkerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkerViewHolder holder, int position) {
        holder.tvProjectName.setText("Project Name : " + list.get(holder.getAdapterPosition()).getProject_name());
        holder.tvUsername.setText("Name : " + list.get(holder.getAdapterPosition()).getUser_name());
        holder.itemView.setOnClickListener(v -> {
            context.startActivity(new Intent(context, ViewUserProfileActivity.class)
                    .putExtra("userId", list.get(holder.getAdapterPosition()).getUser_id())
                    .putExtra("type", list.get(holder.getAdapterPosition()).getUser_type()));
        });
        holder.bAccept.setOnClickListener(v -> {
            MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context);
            dialogBuilder.setTitle("Confirm!");
            dialogBuilder.setMessage("Accept Request");

            dialogBuilder.setIcon(R.drawable.ic_check);
            dialogBuilder.setPositiveButton("Confirm", (dialog, which) -> {
                dialog.dismiss();
                changeRequestStatus(list.get(holder.getAdapterPosition()).getProject_id(), list.get(holder.getAdapterPosition()).getUser_id(), "Accepting", holder.getAdapterPosition());
                //((WorkerRequestsActivity)context).getRequests();
            });
            dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {
                dialog.dismiss();
            });
            dialogBuilder.show();
        });

        holder.bReject.setOnClickListener(v -> {
            MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context);
            dialogBuilder.setTitle("Confirm!");
            Toast.makeText(context, "Rejecting Request!", Toast.LENGTH_SHORT).show();

            dialogBuilder.setMessage("Reject Request");
            dialogBuilder.setIcon(R.drawable.ic_cancel);
            dialogBuilder.setPositiveButton("Confirm", (dialog, which) -> {
                dialog.dismiss();
                changeRequestStatus(list.get(holder.getAdapterPosition()).getProject_id(), list.get(holder.getAdapterPosition()).getUser_id(), "Rejecting", holder.getAdapterPosition());
            });
            dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {
                dialog.dismiss();
            });
            dialogBuilder.show();
        });

        if (Constants.isReporting) {
            holder.bAccept.setVisibility(View.GONE);
            holder.bReject.setVisibility(View.GONE);
            holder.post_count.setVisibility(View.VISIBLE);
            holder.post_count.setText("Total Report : " + list.get(holder.getAdapterPosition()).getPost_count());
            holder.view_post.setVisibility(View.VISIBLE);
            holder.view_post.setOnClickListener(v -> {
                Intent intent = new Intent(context, ViewUserReportingActivity.class);
                intent.putExtra("user_id", list.get(holder.getAdapterPosition()).getUser_id());
                intent.putExtra("user_type", list.get(holder.getAdapterPosition()).getUser_type());
                intent.putExtra("working_area_id", "0");
                context.startActivity(intent);
            });

        }
    }

    private void changeRequestStatus(String projectId, String userId, String status, int position) {
        Log.e("TAG", "getRequests: " + Constants.getString(context, Constants.USER_TYPE));
        String url = "";
        String user_id = Constants.getString(context, Constants.USER_ID);
        if (Constants.getString(context, Constants.USER_TYPE).equals(Constants.USER_TYPE_ADMIN) || Constants.getString(context, Constants.USER_TYPE).equals(Constants.USER_TYPE_SUB_ADMIN)) {
            url = BaseUrls.VERIFY_VIA_ADMIN;
            user_id = userId;
        } else if (Constants.getString(context, Constants.USER_TYPE).equals(Constants.USER_TYPE_PROJECT_CO_ORDINATOR))
            url = BaseUrls.VERIFY_VIA_PROJECT_COORDINATOR;
        else {
            Toast.makeText(context, "Invalid User Type", Toast.LENGTH_SHORT).show();
            return;
        }

        Constants.showProgressDialog(status + " Request!", "Please Wait...", context);


        String finalUser_id = user_id;
        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            Log.e("TAG", "acceptRequest: " + response);
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
                Constants.showToast(context, jsonObject.getString("message"));
                if (jsonObject.getBoolean("status")) {
                    list.remove(position);
                    notifyItemRemoved(position);
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
                params.put("verify", "pass");
                if (!Constants.getString(context,Constants.USER_TYPE).equals(Constants.USER_TYPE_ADMIN)) {
                    params.put("user_id", finalUser_id);
                    params.put("verify_user_id", userId);
                }else {
                    params.put("user_id", userId);
                }
                String s = "";
                if (status.equals("Accepting")) {
                    s = "1";
                } else {
                    s = "0";
                }
                params.put("status", s);
                params.put("project_id", projectId);
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

    public class WorkerViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvUsername, tvProjectName, post_count, view_post;
        private final AppCompatButton bAccept, bReject;

        public WorkerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProjectName = itemView.findViewById(R.id.requestProjectName);
            tvUsername = itemView.findViewById(R.id.requestUsername);
            bAccept = itemView.findViewById(R.id.bRequestAccept);
            bReject = itemView.findViewById(R.id.bRequestReject);
            post_count = itemView.findViewById(R.id.post_count);
            view_post = itemView.findViewById(R.id.view_post);
        }
    }

}
