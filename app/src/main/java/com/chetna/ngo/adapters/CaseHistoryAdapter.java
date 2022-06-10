package com.chetna.ngo.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chetna.ngo.databinding.RewardItemLayoutBinding;
import com.chetna.ngo.models.CaseHistoryModel;
import com.chetna.ngo.models.SliderItem;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;


public class CaseHistoryAdapter extends RecyclerView.Adapter<CaseHistoryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CaseHistoryModel> list;
    private SliderView sliderView;
    CaseHistoryModel model;

    public CaseHistoryAdapter(Context context, ArrayList<CaseHistoryModel> list) {
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
        model = list.get(position);
        holder.binding.title.setText(model.getTitle());
        holder.binding.description.setText(model.getDescription());
        sliderView = holder.binding.imageSlider;
        loadSlider();

    }

    private void loadSlider() {

        SliderAdapterExample adapter = new SliderAdapterExample(context);

        for (String i : model.getImage()) {
            adapter.addItem(new SliderItem("", i));
        }

        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
        sliderView.startAutoCycle();
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
