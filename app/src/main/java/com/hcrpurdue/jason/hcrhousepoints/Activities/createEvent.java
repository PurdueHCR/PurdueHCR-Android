package com.hcrpurdue.jason.hcrhousepoints.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.hcrpurdue.jason.hcrhousepoints.R;

public class createEvent extends AppCompatActivity {
EditText eventName,location,eventHost,points,eventDescription;
Spinner floors;
DatePicker datePicker;
TimePicker startTimePicker,endTimePicker;
Button createEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        eventName = findViewById(R.id.event_name_input);
        location = findViewById(R.id.location_input);
        eventHost = findViewById(R.id.hostInput);
        points = findViewById(R.id.points_input);
        eventDescription = findViewById(R.id.description_input);
        floors = findViewById(R.id.houses_list);
        datePicker = findViewById(R.id.dateINput);
        startTimePicker = findViewById(R.id.timePicker);
        endTimePicker = findViewById(R.id.endTimePIcker);
        createEvent = findViewById(R.id.button);
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String host,name,locationstr,pointValue,description,floor,startTime,endTime,date;
                if (eventHost.getText().toString().trim().length() == 0) {
                    //@TODO get creators name
                }
                else {
                    host = eventHost.getText().toString().trim();
                }
                try {


                    name = eventName.getText().toString().trim();
                    locationstr = location.getText().toString().trim();

                    pointValue = points.getText().toString().trim();
                   description = eventDescription.getText().toString().trim();
                 floor = floors.toString().trim();
                     startTime = startTimePicker.toString();
                     endTime = endTimePicker.toString();
                     date = datePicker.toString();
                }catch (NullPointerException nullPointerException) {
                    //@TODO show dialog reminding users to fill in all fields
                }

                //@TODO get input for date and time pickers
            }
        });
    }
}