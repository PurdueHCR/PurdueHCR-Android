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
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;

import java.text.ParseException;
import java.util.Calendar;

public class createEvent extends AppCompatActivity {
EditText eventName,location,eventHost,points,eventDescription;
Spinner floors;
DatePicker datePicker;
TimePicker startTimePicker,endTimePicker;
Button createevent;
ProgressBar progressBar;

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
        createevent = findViewById(R.id.button);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        createevent.setOnClickListener(v -> {
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
                AlertDialog alertDialog = new AlertDialog.Builder(createEvent.this).create();
                alertDialog.setTitle("Important");
                alertDialog.setMessage("Please make sure to fill in all of the required fields");
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
                    CacheManager cacheManager = CacheManager.getInstance(getApplicationContext());
                   host = cacheManager.getUser().getFirstName() + " " + cacheManager.getUser().getLastName();
                } else {
                    host = eventHost.getText().toString().trim();
                }
                Event event;
                try {


                    name = eventName.getText().toString().trim();
                    locationstr = location.getText().toString().trim();

                    pointValue = points.getText().toString().trim();
                    description = eventDescription.getText().toString().trim();
                    floor = floors.toString().trim();
                        generateDate();

                    //generating start date in required format
                    //generating end date in required format


                    // event = new Event(name, description, sd, ed, locationstr, Integer.parseInt(pointValue), null, host);
                } catch (NullPointerException nullPointerException) {
                    //@TODO show dialog reminding users to fill in all fields
                } catch (Exception e) {
                    AlertDialog alertDialog = new AlertDialog.Builder(createEvent.this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage(e.getMessage());
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();


                }


            }
            progressBar.setVisibility(View.INVISIBLE);
        });

    }

    private String generateDate() throws ParseException {

        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();
       // Instant instant = new Instant(year,month,day);

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
      //  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        System.out.println("Fsdf");
    startTimePicker.
        //System.out.println(df.parse(String.valueOf(calendar.getTimeInMillis())));
        System.out.println("Time" + calendar.getTime());
        System.out.println("STring Date: " + calendar.getTime().toString());

       return calendar.getTime().toString();
    }
}