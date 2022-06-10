package com.chetna.ngo.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
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
import com.chetna.ngo.databinding.SubAdminLayoutBinding;
import com.chetna.ngo.models.UserModel;
import com.chetna.ngo.utils.BaseUrls;
import com.chetna.ngo.utils.Constants;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SubAdminAdapter extends RecyclerView.Adapter<SubAdminAdapter.Viewholder> {
    private final ArrayList<UserModel> userModels;
    private final Context context;
    private final String project_id;
    private final String project_name;

    public SubAdminAdapter(ArrayList<UserModel> userModels, Context context, String project_id, String project_name) {
        this.userModels = userModels;
        this.context = context;
        this.project_id = project_id;
        this.project_name = project_name;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Viewholder(SubAdminLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        UserModel model = userModels.get(position);
        holder.binding.userName.setText(model.getName());
        holder.binding.userEmail.setText(model.getEmail());
        holder.binding.userPhone.setText(model.getPhone());

        holder.binding.AssignSubAdmin.setText(model.isIs_added_to_this_project() ? "Remove From This Project" : "Assign");

        holder.binding.AssignSubAdmin.setOnClickListener(v -> {
            String msg = model.isIs_added_to_this_project() ? "To Remove " + model.getName() + " as Sub Admin from " + project_name : "To Add " + model.getName() + " as Sub Admin into " + project_name;
            MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(context);
            dialogBuilder.setTitle("Confirm!");
            dialogBuilder.setMessage(msg);
            dialogBuilder.setIcon(R.drawable.ic_check);
            dialogBuilder.setPositiveButton("Yes", (dialog, which) -> {
                dialog.dismiss();
                assign_project_to_subadmin(project_id, project_name, model, position);
            });
            dialogBuilder.setNegativeButton("No!", (dialog, which) -> {
                dialog.dismiss();
            });
            dialogBuilder.show();
        });

    }

    private void assign_project_to_subadmin(String project_id, String project_name, UserModel model, int position) {
        Constants.showProgressDialog("Assiging Project To Sub Admin!", "Please Wait...", context);
        StringRequest request = new StringRequest(Request.Method.POST, BaseUrls.ADD_SUB_ADMIN_TO_PROJECT, response -> {
            Log.e("TAG", "changeMessage: " + response);
            try {
                Constants.hideProgressDialog();
                JSONObject jsonObject = new JSONObject(response);
                Constants.showToast(context, jsonObject.getString("message"));
                model.setIs_added_to_this_project(!model.isIs_added_to_this_project());
                userModels.set(position, model);
                notifyItemChanged(position);
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
                params.put("user_name", model.getName());
                params.put("user_id", model.getId());
                params.put("project_id", project_id);
                params.put("project_name", project_name);
                params.put("status", model.isIs_added_to_this_project() ? "2" : "1");
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
        return userModels.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        private final SubAdminLayoutBinding binding;

        public Viewholder(@NonNull SubAdminLayoutBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
