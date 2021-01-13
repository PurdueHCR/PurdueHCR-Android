package com.hcrpurdue.jason.hcrhousepoints.RecyclerAdapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hcrpurdue.jason.hcrhousepoints.Activities.DetailedEventViewActivity;
import com.hcrpurdue.jason.hcrhousepoints.Activities.editEvent;
import com.hcrpurdue.jason.hcrhousepoints.Models.Enums.UserPermissionLevel;
import com.hcrpurdue.jason.hcrhousepoints.Models.Event;
import com.hcrpurdue.jason.hcrhousepoints.R;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;

import java.sql.Timestamp;
import java.util.List;


public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.MyHolder> {

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    List<Event> events;
    CacheManager cacheManager;

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
        cacheManager = CacheManager.getInstance(holder.itemView.getContext());
        cacheManager.getCachedData();

        final Event data = events.get(position);
        // holder.location.setText("Dfsf");
        //@TODO bind data here
        holder.title.setText(data.getName());
        holder.timeStamp.setText(data.getStartDate());
        Timestamp timestamp = Timestamp.valueOf(data.getStartDate());
        holder.location.setText(data.getLocation());
        holder.eventDescription.setText(data.getDetails());
        holder.points.setText(data.getPoint());
        holder.points.setText(String.valueOf(data.getPoint()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(v.getContext(), DetailedEventViewActivity.class);
                intent2.putExtra("EVENT",data);
                v.getContext().startActivity(intent2);
            }
        });
       //long cick(hold) for editing only works if the user is a priviledged residert or higher
        if (!cacheManager.getUser().getPermissionLevel().equals(UserPermissionLevel.RESIDENT)) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //@TODO launch edit event pages
                    Intent intent = new Intent(view.getContext(), editEvent.class);
                    intent.putExtra("event", data);

                    view.getContext().startActivity(intent);
                    return true;// returning true instead of false, works for me
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        if (events == null) {
            return 0;
        }
        System.out.println("Size" + events.size());
        return events.size();
       // return events.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {

        TextView title, timeStamp, location, points, eventDescription;
        ScrollView scrollView;

        public MyHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            timeStamp = itemView.findViewById(R.id.timeStamp);
            location = itemView.findViewById(R.id.location);
            points = itemView.findViewById(R.id.points);
            eventDescription = itemView.findViewById(R.id.description);
            scrollView = itemView.findViewById(R.id.descriptionScrollView);
            scrollView.bringToFront();


        }
    }

    public List<Event> getEvents() {
        return events;
    }

}

