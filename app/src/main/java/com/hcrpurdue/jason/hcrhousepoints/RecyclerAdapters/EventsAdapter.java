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
import com.hcrpurdue.jason.hcrhousepoints.Models.PassEvent;
import com.hcrpurdue.jason.hcrhousepoints.R;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    System.out.println(data.getStartDate().substring(0, 19));
        String s1 = data.getStartDate().substring(0, 10);
        String s2 = data.getStartDate().substring(11, 19);
        System.out.println(s1);
        System.out.println(s2);
        s1 += " ";
        s1 += s2;

        System.out.println(s1);
        //parsing end date and getting required information
        String s3 = data.getEndDate().substring(0, 10);
        String s4 = data.getEndDate().substring(11, 19);

        s3 += " ";
        s3 += s4;

        System.out.println(s3);


        StringBuilder sb = new StringBuilder();
        //creating timestamp for start date
        Timestamp timestamp = Timestamp.valueOf(s1);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        int month = calendar.get(Calendar.MONTH);
        int dayOFMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        System.out.println("hour" + hour);
        int minute = calendar.get(Calendar.MINUTE);
        System.out.println("minute" + hour);
        //creating timestamp for end date
        Timestamp timestamp2 = Timestamp.valueOf(s3);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(timestamp2);
        int month2 = calendar.get(Calendar.MONTH);
        int dayOFMonth2 = calendar2.get(Calendar.DAY_OF_MONTH);
        int hour2 = calendar2.get(Calendar.HOUR_OF_DAY);
        int minute2 = calendar2.get(Calendar.MINUTE);

        SimpleDateFormat sfd = new SimpleDateFormat("EEE, MMM d");
        String date4 = sfd.format(timestamp);
        System.out.println(calendar.get(Calendar.DAY_OF_MONTH));
        System.out.println(calendar2.get(Calendar.DAY_OF_MONTH));
        sb.append(date4);
        if (dayOFMonth != dayOFMonth2) {
            sb.append(" to ");
            sb.append(sfd.format(timestamp2));
            sb.append(" ");
        } else {
            sb.append(" from ");
        }

        if (hour > 12) {

            hour -= 12;
        } else if (hour == 0) {
            hour = 12;
        }
        sb.append(hour).append(":");
        if (minute < 10) {
            sb.append("0");
        }
        sb.append(minute);
        sb.append("-");


        String ampm = "AM";
        if (hour2 >= 12) {
            ampm = "PM";

            hour2 -= 12;
        } else if (hour2 == 0) {
            hour2 = 12;

        }
        sb.append(hour2);
        sb.append(":");
        if (minute2 < 10) {
            sb.append("0");
        }
        sb.append(minute2).append(ampm);


        //calendar.get(Calendar.)
        // SimpleDateFormat sfd = new SimpleDateFormat("EEE, MMM d HH:mm:ss");

        System.out.println("TimeStamp" + sb.toString());
        holder.timeStamp.setText(sb.toString());
        holder.location.setText(data.getLocation());
        holder.eventDescription.setText(data.getDetails());
        //holder.points.setText(data.getPoint());
        System.out.println("Point value" + data.getPoint());
        holder.points.setText(String.valueOf(data.getPoint()));

        int ifinalHour2 = hour2;
        int ifinalHour1 = hour;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(v.getContext(), DetailedEventViewActivity.class);

                intent2.putExtra("EVENT", new PassEvent(data.getName(), data.getHost(), data.getDetails(), data.getLocation(), data.getPoint(), data.getStartDate(), data.getEndDate(), String.valueOf(month + 1), String.valueOf(dayOFMonth), String.valueOf(ifinalHour1), String.valueOf(minute), String.valueOf(month2 + 1), String.valueOf(dayOFMonth2), String.valueOf(ifinalHour2), String.valueOf(minute2), data.getFloorIds(), data.isPublicEvent(), data.getId()));

                v.getContext().startActivity(intent2);
            }
        });
        //long cick(hold) for editing only works if the user is a priviledged residert or higher
        if (!cacheManager.getUser().getPermissionLevel().equals(UserPermissionLevel.RESIDENT)) {
            int finalHour = hour;
            int finalHour1 = hour2;
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //@TODO launch edit event pages
                    Intent intent = new Intent(view.getContext(), editEvent.class);
                    intent.putExtra("event", new PassEvent(data.getName(), data.getHost(), data.getDetails(), data.getLocation(), data.getPointTypeName(), data.getStartDate(), data.getEndDate(), String.valueOf(month), String.valueOf(dayOFMonth), String.valueOf(finalHour), String.valueOf(minute), String.valueOf(month2), String.valueOf(dayOFMonth2), String.valueOf(finalHour1), String.valueOf(minute2), data.getFloorIds(), data.isPublicEvent(), data.getId()));

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

