package com.chetna.ngo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chetna.ngo.R;
import com.chetna.ngo.models.EventsModel;

import java.util.ArrayList;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {
    private ArrayList<EventsModel> eventsModelArrayList;

    public EventsAdapter(ArrayList<EventsModel> eventsModelArrayList) {
        this.eventsModelArrayList = eventsModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.events_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventsModel model= eventsModelArrayList.get(position);
        holder.event_name.setText(model.getEvent_name());
        holder.event_desc.setText(model.getEvent_desc());
        holder.event_date.setText("On : "+model.getEvent_date());
        holder.event_time.setText("At : "+model.getEvent_time());
    }

    @Override
    public int getItemCount() {
        return eventsModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView event_time,event_date,event_desc,event_name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            event_time=itemView.findViewById(R.id.event_time);
            event_date=itemView.findViewById(R.id.event_date);
            event_desc=itemView.findViewById(R.id.event_desc);
            event_name=itemView.findViewById(R.id.event_name);
        }
    }
}
