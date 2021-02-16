package com.hcrpurdue.jason.hcrhousepoints.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hcrpurdue.jason.hcrhousepoints.Fragments.Events;
import com.hcrpurdue.jason.hcrhousepoints.Models.PassEvent;
import com.hcrpurdue.jason.hcrhousepoints.R;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DetailedEventViewActivity extends AppCompatActivity {

    private TextView date;
    private TextView time;
    private TextView title;
    private TextView location;
    private TextView host;
    private TextView details;
    private CheckBox going;
    private Button gcExport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_event_view);
title = findViewById(R.id.title);
        date = findViewById(R.id.date_actual);
        time = findViewById(R.id.time_actual);

        location = findViewById(R.id.loc_actual);
        host = findViewById(R.id.host_actual);
        details = findViewById(R.id.details_actual);

        gcExport = findViewById(R.id.gcexport);
        PassEvent event;
        event = (PassEvent) getIntent().getSerializableExtra("EVENT");
        try {

System.out.println("Execute");


            System.out.println("Event passed successfully");
            title.setText(event.getEventName());
            date.setText(event.getStartDateMonth() +"-" + event.getStartDateDay() + " to " +event.getEndDateMonth() + "-" + event.getEndDateDay());
            if (event.getStartDateMinute().equals("0")) {
                event.setStartDateMinute("00");
            }
            if (event.getEndDateMinute().equals("0")) {
                event.setEndDateMinute("00");
            }
            time.setText(event.getStartDateHour() + ":" + event.getStartDateMinute() +"-" + event.getEndDateHour() + ":"+ event.getEndDateMinute());
            location.setText(event.getLocation());
            host.setText(event.getHost());
            details.setText(event.getDescription());

            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");



           // Calendar calendar = Calendar.getInstance();
          //  calendar.setTime(date2);

            }

        catch (Exception e) {
            event = null;
            System.out.println("Error passing event");
            Intent intent = new Intent(this, Events.class);
            startActivity(intent);

        }


        PassEvent finalEvent = event;
        gcExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                String s1 = finalEvent.getStartDate().substring(0,10);
                String s2 = finalEvent.getStartDate().substring(11,19);
                System.out.println(s1);
                System.out.println(s2);
                s1+=" ";
                s1 += s2;
                Timestamp timestamp = Timestamp.valueOf(s1);
                cal.setTime(timestamp);
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra("beginTime", cal.getTimeInMillis());


                String s3 = finalEvent.getEndDate().substring(0,10);
                String s4 = finalEvent.getEndDate().substring(11,19);

                s3+=" ";
                s3 += s4;
                Timestamp timestamp2 = Timestamp.valueOf(s3);
                cal.setTime(timestamp2);
                intent.putExtra("endTime", cal.getTimeInMillis());
                intent.putExtra("title", finalEvent.getEventName());
                startActivity(intent);
            }
        });
    }
}