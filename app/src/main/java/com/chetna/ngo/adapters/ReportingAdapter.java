package com.chetna.ngo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chetna.ngo.databinding.RepotingLayoutBinding;
import com.chetna.ngo.models.PostModel;
import com.chetna.ngo.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReportingAdapter extends RecyclerView.Adapter<ReportingAdapter.ViewHolder> {
    private ArrayList<PostModel> postModels;
    private Context context;
    private boolean isReporting = false;

    public ReportingAdapter(ArrayList<PostModel> postModels, Context context, boolean isReporting) {
        this.postModels = postModels;
        this.context = context;
        this.isReporting = isReporting;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(RepotingLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PostModel model = postModels.get(position);

        holder.binding.postId.setText("Report Number : " + model.getId());
        holder.binding.userName.setText(model.getUser_name());
        holder.binding.details.setText("Details : " + model.getDetails());
        holder.binding.text.setText("Title : " + model.getText());
        holder.binding.location.setText("Location : " + model.getLocation());
        holder.binding.postImageRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        if (!model.getPhoto().equalsIgnoreCase("")) {
            List<String> postImageModelsList = Arrays.asList(model.getPhoto().split(","));
            PostImagesAdapter postIMagesAdapter = new PostImagesAdapter(postImageModelsList, context);
            holder.binding.postImageRecyclerView.setAdapter(postIMagesAdapter);
            holder.binding.postImageRecyclerView.setVisibility(View.VISIBLE);
        } else {
            holder.binding.postImageRecyclerView.setVisibility(View.GONE);
        }


        if (isReporting) {
            holder.binding.checkBox.setChecked(model.isAddToReporting());
            holder.binding.checkBox.setVisibility(View.VISIBLE);
        } else {
            holder.binding.checkBox.setVisibility(View.GONE);
        }

        holder.binding.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            ArrayList<String> savedList = Constants.getSavedPostList(context);
            if (b) {
                savedList.add(model.getId());
                model.setAddToReporting(true);
            } else {
                savedList.remove(model.getId());
                model.setAddToReporting(false);
            }
            postModels.set(position, model);
            notifyItemChanged(position);
            Constants.savePosts(savedList, context);
        });

    }

    @Override
    public int getItemCount() {
        return postModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RepotingLayoutBinding binding;

        public ViewHolder(@NonNull RepotingLayoutBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
