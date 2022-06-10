package com.chetna.ngo.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chetna.ngo.databinding.GalleryItemLytBinding;
import com.chetna.ngo.models.GalleryModel;
import com.chetna.ngo.utils.BaseUrls;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<GalleryModel> list;

    public GalleryAdapter(Context context, ArrayList<GalleryModel> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(GalleryItemLytBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GalleryModel model = list.get(position);
        Log.e("TAG", "onBindViewHolder: ");
        Picasso.with(context).load(BaseUrls.BASE_URL + model.getImage()).into(holder.binding.image);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private GalleryItemLytBinding binding;

        public ViewHolder(@NonNull GalleryItemLytBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
