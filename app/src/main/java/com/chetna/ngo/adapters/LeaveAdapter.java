package com.chetna.ngo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.chetna.ngo.R;
import com.chetna.ngo.databinding.LeaveItemLayoutBinding;
import com.chetna.ngo.models.LeaveModel;

import java.util.ArrayList;

public class LeaveAdapter extends RecyclerView.Adapter<LeaveAdapter.ViewHolder> {

    private ArrayList<LeaveModel> leaveAdapterArrayList;
    private Context context;

    public LeaveAdapter(ArrayList<LeaveModel> leaveAdapterArrayList, Context context) {
        this.leaveAdapterArrayList = leaveAdapterArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LeaveItemLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LeaveModel model = leaveAdapterArrayList.get(position);
        holder.binding.appliedOn.setText("Applied On : " + model.getApplied_on());
        holder.binding.leaveAddress.setText("Leave Address : " + model.getLeave_address());
        holder.binding.toDate.setText("To : " + model.getTo_date());
        holder.binding.fromDate.setText("From : " + model.getFrom_date());
        holder.binding.reason.setText("Reason : " + model.getReason());

        if (model.getStatus().equalsIgnoreCase("0")) {
            holder.binding.status.setText("Pending");
            holder.binding.status.setTextColor(ContextCompat.getColor(context, R.color.yellow));
        } else if (model.getStatus().equalsIgnoreCase("1")) {
            holder.binding.status.setText("Approve");
            holder.binding.status.setTextColor(ContextCompat.getColor(context, R.color.green));
        } else if (model.getStatus().equalsIgnoreCase("2")) {
            holder.binding.status.setText("Cancel");
            holder.binding.status.setTextColor(ContextCompat.getColor(context, R.color.red));
        }


    }

    @Override
    public int getItemCount() {
        return leaveAdapterArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private LeaveItemLayoutBinding binding;

        public ViewHolder(@NonNull LeaveItemLayoutBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
