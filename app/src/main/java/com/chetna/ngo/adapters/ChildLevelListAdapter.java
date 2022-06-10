package com.chetna.ngo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chetna.ngo.R;
import com.chetna.ngo.models.ChildLevelModel;
import com.chetna.ngo.utils.Constants;

import java.util.ArrayList;

public class ChildLevelListAdapter extends RecyclerView.Adapter<ChildLevelListAdapter.ChildLevelListViewHolder> {

    Context context;
    private ArrayList<ChildLevelModel> list;
    private final String userType;


    public ChildLevelListAdapter(Context context, ArrayList<ChildLevelModel> list) {
        this.context = context;
        this.list = list;
        userType = Constants.getString(context, Constants.USER_TYPE);
    }

    @NonNull
    @Override
    public ChildLevelListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.student_level_list_item_layout, parent, false);
        return new ChildLevelListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildLevelListViewHolder holder, int position) {

        holder.tvName.setText(list.get(holder.getAdapterPosition()).getLevel_information());
        holder.counter.setText(""+(position+1));

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ChildLevelListViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvName,counter;

        public ChildLevelListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.levelName);
            counter = itemView.findViewById(R.id.tvLevelCounter);
        }
    }

}
