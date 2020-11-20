package com.hcrpurdue.jason.hcrhousepoints.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.hcrpurdue.jason.hcrhousepoints.R;

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
        gcExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //code to export to google calendar
            }
        });
    }
}