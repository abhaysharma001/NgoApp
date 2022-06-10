package com.chetna.ngo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chetna.ngo.R;
import com.chetna.ngo.models.PostPreviewModel;

import java.util.List;

public class PostPreViewAdapter extends RecyclerView.Adapter<PostPreViewAdapter.ViewHolder> {

    private List<PostPreviewModel> postPreviewModels;
    private Context context;

    public PostPreViewAdapter(List<PostPreviewModel> postPreviewModels, Context context) {
        this.postPreviewModels = postPreviewModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        int layout;
        layout = R.layout.post_preview_layout;

        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PostPreviewModel model = postPreviewModels.get(position);


        holder.delete.setOnClickListener(v -> {
            postPreviewModels.remove(model);
            notifyDataSetChanged();
        });

        if (model.getAbsPath().endsWith(".png") || model.getAbsPath().endsWith(".jpg")) {
            Glide.with(context)
                    .load(model.getImage())
                    .into(holder.imageView);
        }else {
            holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_file));
        }
    }

    @Override
    public int getItemCount() {
        return postPreviewModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatImageView delete, imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.delete = itemView.findViewById(R.id.delete_post);
            this.imageView = itemView.findViewById(R.id.img_post_recycler_view);
        }
    }
}