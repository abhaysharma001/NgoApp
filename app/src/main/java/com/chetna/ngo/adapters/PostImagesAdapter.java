package com.chetna.ngo.adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chetna.ngo.R;
import com.chetna.ngo.databinding.ImageViewReportingBinding;
import com.chetna.ngo.utils.BaseUrls;

import java.util.List;

public class PostImagesAdapter extends RecyclerView.Adapter<PostImagesAdapter.ViewHolder> {
    private List<String> imagesList;
    private Context context;

    public PostImagesAdapter(List<String> imagesList, Context context) {
        this.imagesList = imagesList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ImageViewReportingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Glide.with(context)
                .load(BaseUrls.BASE_URL + imagesList.get(position))
                .placeholder(ContextCompat.getDrawable(context, R.drawable.image_error))
                .into(holder.binding.imageView);

        holder.binding.imageView.setOnClickListener(v -> {
            showImageDialog(position);
        });
    }

    private void showImageDialog(int pos) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.image_view_show);

        ImageView image = dialog.findViewById(R.id.image);
        Glide.with(context)
                .load(BaseUrls.BASE_URL + imagesList.get(pos))
                .placeholder(ContextCompat.getDrawable(context, R.drawable.image_error))
                .into(image);

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageViewReportingBinding binding;

        public ViewHolder(@NonNull ImageViewReportingBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
