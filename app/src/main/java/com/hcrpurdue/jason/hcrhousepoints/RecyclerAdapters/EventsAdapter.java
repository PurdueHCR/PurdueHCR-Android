package com.hcrpurdue.jason.hcrhousepoints.RecyclerAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hcrpurdue.jason.hcrhousepoints.Models.Event;
import com.hcrpurdue.jason.hcrhousepoints.R;

import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.MyHolder> {

    List<Event> events;



    public EventsAdapter(List<Event> events) {
        this.events = events;
    }

    @Override
    public EventsAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_events,parent,false);

        EventsAdapter.MyHolder myHolder = new EventsAdapter.MyHolder(view);
        return myHolder;
    }


    public void onBindViewHolder(LaundryRv.MyHolder holder, final int position) {

        final Event data = events.get(position);
       //@TODO bind data here




        }

    


    @Override
    public int getItemCount() {
        return events.size();
    }


    class MyHolder extends RecyclerView.ViewHolder{

        TextView unit,status;

        public MyHolder(View itemView) {
            super(itemView);
            unit =  itemView.findViewById(R.id.name);
            status = itemView.findViewById(R.id.status);

        }
    }

}
