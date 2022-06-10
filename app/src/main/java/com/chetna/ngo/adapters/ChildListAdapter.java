package com.chetna.ngo.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chetna.ngo.R;
import com.chetna.ngo.activities.ViewChildProfileActivity;
import com.chetna.ngo.models.ChildModel;
import com.chetna.ngo.utils.Constants;

import java.util.ArrayList;

public class ChildListAdapter extends RecyclerView.Adapter<ChildListAdapter.ChildListViewHolder> {

    Context context;
    private ArrayList<ChildModel> list;
    private final String userType;


    public ChildListAdapter(Context context, ArrayList<ChildModel> list) {
        this.context = context;
        this.list = list;
        userType = Constants.getString(context, Constants.USER_TYPE);
    }

    @NonNull
    @Override
    public ChildListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.child_list_item_layout, parent, false);
        return new ChildListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildListViewHolder holder, int position) {
        holder.tvName.setText("Name : " + list.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ViewChildProfileActivity.class)
                        .putExtra("name", list.get(holder.getAdapterPosition()).getName())
                        .putExtra("child_id", list.get(holder.getAdapterPosition()).getId())
                        .putExtra("age", list.get(holder.getAdapterPosition()).getAge())
                        .putExtra("gender", list.get(holder.getAdapterPosition()).getGender()));
            }
        });


    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ChildListViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvName;

        public ChildListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.childItemName);
        }
    }

}
