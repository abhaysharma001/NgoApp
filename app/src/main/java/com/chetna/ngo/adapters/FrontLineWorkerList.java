package com.chetna.ngo.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
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
import com.chetna.ngo.activities.ViewUserReportingActivity;
import com.chetna.ngo.databinding.FrontLineWorkerLayoutBinding;
import com.chetna.ngo.models.FrontLineWorkerListModel;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FrontLineWorkerList extends RecyclerView.Adapter<FrontLineWorkerList.ViewHolder> {
    private ArrayList<FrontLineWorkerListModel> frontLineWorkerListModels;
    private Context context;

    public FrontLineWorkerList(ArrayList<FrontLineWorkerListModel> frontLineWorkerListModels, Context context) {
        this.frontLineWorkerListModels = frontLineWorkerListModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(FrontLineWorkerLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FrontLineWorkerListModel model = frontLineWorkerListModels.get(position);

        holder.binding.userName.setText(model.getUser_name());
        holder.binding.projectName.setText("Project Name : " + model.getProject_name());

        if (Constants.getString(context, Constants.USER_TYPE).equalsIgnoreCase(Constants.USER_TYPE_STREET_CO_ORDINATOR)) {
            holder.binding.status.setVisibility(View.GONE);
            holder.binding.verifyStatus.setVisibility(View.GONE);
            holder.binding.postCount.setText("Posts : " + model.getPost_count());
            holder.binding.viewPost.setVisibility(View.VISIBLE);
        } else {
            if (model.getVerify_status().equalsIgnoreCase("1")) {
                holder.binding.verifyStatus.setVisibility(View.GONE);
                if (model.getStatus().equalsIgnoreCase("1")) {
                    holder.binding.status.setVisibility(View.VISIBLE);
                    holder.binding.status.setText("This user is Already added to this working area");
                    holder.binding.status.setTextColor(ContextCompat.getColor(context, R.color.green));
                    holder.binding.postCount.setText("Posts : " + model.getPost_count());
                    holder.binding.postCount.setVisibility(View.VISIBLE);
                    holder.binding.addFrontlineWorker.setVisibility(View.GONE);
                    holder.binding.viewPost.setVisibility(View.VISIBLE);
                } else {
                    holder.binding.viewPost.setVisibility(View.GONE);
                    holder.binding.status.setVisibility(View.GONE);
                    holder.binding.postCount.setVisibility(View.GONE);
                    holder.binding.verifyStatus.setVisibility(View.GONE);
                    holder.binding.addFrontlineWorker.setVisibility(View.VISIBLE);
                    holder.binding.addFrontlineWorker.setOnClickListener(v -> {
                        // here we assign the worker area to frontline worker
                        new AlertDialog.Builder(context)
                                .setTitle("Are you Sure?").setMessage("To add " + model.getUser_name() + " into this working area")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        addFrontLineWorkerToWorkingArea(model, position);
                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();

                    });
                }
            } else {
                holder.binding.verifyStatus.setText("This user not verify yet");
                holder.binding.verifyStatus.setVisibility(View.VISIBLE);
                holder.binding.verifyStatus.setTextColor(ContextCompat.getColor(context, R.color.red));
                holder.binding.addFrontlineWorker.setVisibility(View.GONE);
            }
        }

        holder.binding.viewPost.setOnClickListener(v -> {
            // here we call the next activity to she the post of this user
            if (Integer.parseInt(model.getPost_count()) > 0) {
                Intent intent = new Intent(context, ViewUserReportingActivity.class);
                intent.putExtra("user_id", model.getUser_id());
                intent.putExtra("user_type", model.getUser_type());
                intent.putExtra("working_area_id", model.getWorking_area_id());
                context.startActivity(intent);
            } else {
                Constants.showToast(context, "No Post Found");
            }
        });

    }

    @Override
    public int getItemCount() {
        return frontLineWorkerListModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private FrontLineWorkerLayoutBinding binding;

        public ViewHolder(@NonNull FrontLineWorkerLayoutBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }


    private void addFrontLineWorkerToWorkingArea(FrontLineWorkerListModel model, int position) {
        Log.e("TAG", "saveWorkingArea: " + Constants.getString(context, Constants.USER_TYPE));

        Constants.showProgressDialog("Adding FrontLine Worker to this working area", "Please Wait...", context);


        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.ASSIGN_WORKING_AREA_TO_F_WORKER, response -> {
            Log.e("TAG", "saveWorkingArea: " + response);
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
                Constants.showToast(context, jsonObject.getString("message"));
                if (jsonObject.getBoolean("status")) {
                    model.setStatus("1");
                    frontLineWorkerListModels.set(position, model);
                    notifyItemChanged(position);
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
                params.put("working_area_name", model.getWorking_area_name());
                params.put("working_area_id", model.getWorking_area_id());
                params.put("frontline_worker_id", model.getUser_id());
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
