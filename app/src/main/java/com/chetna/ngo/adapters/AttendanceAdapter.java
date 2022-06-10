package com.chetna.ngo.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.chetna.ngo.R;
import com.chetna.ngo.databinding.AttendanceLytBinding;
import com.chetna.ngo.databinding.RewardItemLayoutBinding;
import com.chetna.ngo.models.AttendanceModel;
import com.chetna.ngo.models.SliderItem;
import com.chetna.ngo.utils.BaseUrls;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;

import java.util.ArrayList;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {

    private Context context;
    private ArrayList<AttendanceModel> list;

    public AttendanceAdapter(Context context, ArrayList<AttendanceModel> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(AttendanceLytBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AttendanceModel model = list.get(position);
        holder.binding.tvName.setText("Name : " + model.getUsername());

        if (Integer.parseInt(model.getCount()) == 0) {
            holder.binding.presentView.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
        }

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private AttendanceLytBinding binding;

        public ViewHolder(@NonNull AttendanceLytBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
