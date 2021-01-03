package com.hcrpurdue.jason.hcrhousepoints.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.hcrpurdue.jason.hcrhousepoints.Models.Event;
import com.hcrpurdue.jason.hcrhousepoints.R;

public class editEvent extends AppCompatActivity {
    EditText eventName,location,eventHost,points,eventDescription;
    Spinner floors;
    DatePicker datePicker;
    TimePicker startTimePicker,endTimePicker;
    Button updateEvent;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getting intent
        Event event = (Event) getIntent().getSerializableExtra("event");
        setContentView(R.layout.activity_edit_event);
        eventName = findViewById(R.id.event_name_input);
        location = findViewById(R.id.location_input);
        eventHost = findViewById(R.id.hostInput);
        points = findViewById(R.id.points_input);
        eventDescription = findViewById(R.id.description_input);
        floors = findViewById(R.id.houses_list);
        datePicker = findViewById(R.id.dateINput);
        startTimePicker = findViewById(R.id.timePicker);
        endTimePicker = findViewById(R.id.endTimePIcker);
        updateEvent = findViewById(R.id.button);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        //setting text below to previous values
        //@TODO set initial values
        /*
        eventName.setText();
        location.setText();
        eventHost.setText();
        points.setText();
        eventDescription.setText();
        //@TODO need to compare floor value to position in @array house/floor in strings file to get position for spinner
        floors.setSelection();
        startTimePicker.setHour();
        startTimePicker.setMinute();
        endTimePicker.setHour();
        endTimePicker.setMinute();*/
        updateEvent.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);

            if (eventName.getText().toString().length() == 0 || location.getText().toString().length() == 0 || eventDescription.getText().toString().length() == 0 || points.getText().toString().length() == 0 || floors.toString().length() == 0) {
              /*  AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(getApplicationContext());
                //builder.setIcon(R.drawable.open_browser);
                builder.setTitle(" Please make sure to fill in all fields");
                builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                builder.setCancelable(true);
                builder.show();
*/
                AlertDialog alertDialog = new AlertDialog.Builder(getApplication()).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Alert message to be shown");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();


            } else {
                String host, name, locationstr, pointValue, description, floor, startTime, endTime, date;
                if (eventHost.getText().toString().trim().length() == 0) {
                    //@TODO get creators name
                    host = "BOB";
                } else {
                    host = eventHost.getText().toString().trim();
                }
                Event event2;
                try {


                    name = eventName.getText().toString().trim();
                    locationstr = location.getText().toString().trim();

                    pointValue = points.getText().toString().trim();
                    description = eventDescription.getText().toString().trim();
                    floor = floors.toString().trim();
                    startTime = startTimePicker.toString();
                    endTime = endTimePicker.toString();
                    date = datePicker.toString();
                    //event = new Event(name,description,null,null,locationstr,Integer.parseInt(pointValue),null,null,null,null,null,null,host,null);
                    event2 = new Event(name, description, null, null, locationstr, Integer.parseInt(pointValue), null, host);
                } catch (NullPointerException nullPointerException) {
                    //@TODO show dialog reminding users to fill in all fields
                } catch (Exception e) {
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(getApplicationContext());
                    //builder.setIcon(R.drawable.open_browser);
                    builder.setTitle(" Please make sure the point value is a number");
                    builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    builder.setCancelable(true);
                    builder.show();

                }


            }
            progressBar.setVisibility(View.INVISIBLE);
        });

    }
}