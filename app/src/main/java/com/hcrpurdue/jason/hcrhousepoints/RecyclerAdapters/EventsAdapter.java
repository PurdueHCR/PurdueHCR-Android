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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_events, parent, false);

        EventsAdapter.MyHolder myHolder = new EventsAdapter.MyHolder(view);
        return myHolder;
    }


    public void onBindViewHolder(EventsAdapter.MyHolder holder, final int position) {

        final Event data = events.get(position);
        // holder.location.setText("Dfsf");
        //@TODO bind data here
        //@TODO enable if user is privilidged resident or above
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
              //@TODO launch edit event page
                return true;// returning true instead of false, works for me
            }
        });
    }


    @Override
    public int getItemCount() {
        return events.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {

        TextView title, timeStamp, location, points, eventDescription;

        public MyHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            timeStamp = itemView.findViewById(R.id.status);
            location = itemView.findViewById(R.id.location);
            points = itemView.findViewById(R.id.points);
            eventDescription = itemView.findViewById(R.id.description);

        }
    }

}
