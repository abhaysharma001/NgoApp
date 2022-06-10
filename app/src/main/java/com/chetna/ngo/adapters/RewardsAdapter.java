package com.chetna.ngo.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chetna.ngo.databinding.RewardItemLayoutBinding;
import com.chetna.ngo.models.RewardModel;
import com.chetna.ngo.models.SliderItem;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;

import java.util.ArrayList;

public class RewardsAdapter extends RecyclerView.Adapter<RewardsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<RewardModel> list;

    public RewardsAdapter(Context context, ArrayList<RewardModel> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(RewardItemLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RewardModel model = list.get(position);
        holder.binding.title.setText(model.getTitle());
        holder.binding.description.setText(model.getDescription());
        loadSlider(holder.binding, holder.getAdapterPosition());
    }

    private void loadSlider(RewardItemLayoutBinding binding, int i) {

        SliderAdapterExample adapter = new SliderAdapterExample(context);
        for (String img : list.get(i).getPhotos()) {
            Log.e("TAG", "loadSlider: " + img);
            adapter.addItem(new SliderItem("", img));
        }
        binding.imageSlider.setSliderAdapter(adapter);
        binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        binding.imageSlider.setAutoCycleDirection(binding.imageSlider.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        binding.imageSlider.setIndicatorSelectedColor(Color.WHITE);
        binding.imageSlider.setIndicatorUnselectedColor(Color.GRAY);
        binding.imageSlider.setScrollTimeInSec(4); //set scroll delay in seconds :
        binding.imageSlider.startAutoCycle();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RewardItemLayoutBinding binding;

        public ViewHolder(@NonNull RewardItemLayoutBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
