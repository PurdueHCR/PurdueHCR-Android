package com.hcrpurdue.jason.hcrhousepoints.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hcrpurdue.jason.hcrhousepoints.Fragments.Events;
import com.hcrpurdue.jason.hcrhousepoints.Models.Event;
import com.hcrpurdue.jason.hcrhousepoints.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailedEventViewActivity extends AppCompatActivity {

    private TextView date;
    private TextView time;
    private TextView attendees;
    private TextView location;
    private TextView host;
    private TextView details;
    private CheckBox going;
    private Button gcExport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_event_view);

        date = findViewById(R.id.date_actual);
        time = findViewById(R.id.time_actual);
        attendees = findViewById(R.id.attendees_actual);
        location = findViewById(R.id.loc_actual);
        host = findViewById(R.id.host_actual);
        details = findViewById(R.id.details_actual);
        going = findViewById(R.id.going);
        gcExport = findViewById(R.id.gcexport);
        Event event;
        try {

System.out.println("Execute");

            event = (Event) getIntent().getSerializableExtra("EVENT");
            System.out.println("Event passed successfully");
            location.setText(event.getLocation());
            host.setText(event.getHost());
            details.setText("fdsjfdslkfjlkjfglkdjga;klf;jg;oufijgheriujvgriojdifjdslkfjlkgjfk;gjdflkgjfdklgjfdlg;jaklg;");

            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");


                Date date2 = format.parse(event.getStartDate());
                System.out.println(date2.toString());
           // Calendar calendar = Calendar.getInstance();
          //  calendar.setTime(date2);

            }
        catch (ParseException parseException){
            System.out.println("Error");
            parseException.printStackTrace();
        }
        catch (Exception e) {
            event = null;
            System.out.println("Error passing event");
            Intent intent = new Intent(this, Events.class);
            startActivity(intent);

        }


        gcExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //code to export to google calendar
            }
        });
    }
}