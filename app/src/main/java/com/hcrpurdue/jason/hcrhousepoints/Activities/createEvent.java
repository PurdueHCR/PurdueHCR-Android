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
import com.hcrpurdue.jason.hcrhousepoints.Models.ResponseMessage;
import com.hcrpurdue.jason.hcrhousepoints.R;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;
import com.hcrpurdue.jason.hcrhousepoints.Utils.HttpNetworking.APIHelper;

import java.io.IOException;
import java.text.ParseException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class createEvent extends AppCompatActivity {
    EditText eventName, location, eventHost, points, eventDescription;
    Spinner floors;
    DatePicker datePicker;
    TimePicker startTimePicker, endTimePicker;
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
                    floor = floors.getSelectedItem().toString();


                    boolean isAllFloors = false;
                    if (floor.equals("All Floors")) {
                        isAllFloors = true;

                    }
                    String[] floorID = {floor};

                    String actualSD = generateDate(startTimePicker);
                    String actualED = generateDate(endTimePicker);

                     System.out.println("Name:" + name);
                    System.out.println("Description:" + description);
                    System.out.println("SD:" + actualSD);
                    System.out.println("ED:" + actualED);
                    System.out.println("Location:" + locationstr);
                    System.out.println("Points:" + Integer.parseInt(pointValue));
                    System.out.println("Floor id:" + floorID[0]);
                    System.out.println("Host:" + host);
                    event = new Event(name, description,actualSD , actualED, locationstr, Integer.parseInt(pointValue), floorID, false, isAllFloors, host);

                    postEvent(event);
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
        });
    }
    private void postEvent(Event event) {
        APIHelper.getInstance(createEvent.this).postEvent(event).enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                System.out.println("Event Posted: " + response.code());
                if (response.isSuccessful()) {
                    System.out.println(" GOT RESPONSE: " + response.body().getMessage());

                } else {
                    try {
                        System.out.println("GOT Error: " + response.errorBody().string());
                    } catch (IOException err) {
                        System.out.println(err.getMessage());
                    }


                }
            }
            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {

                System.out.println("Failure: " + t.getMessage());
            }






        });

    }

    private String generateDate(TimePicker tp) throws ParseException {
        StringBuilder sb = new StringBuilder();
        int day = datePicker.getDayOfMonth();
        //adding 1 because months start at 0
        int month = datePicker.getMonth() + 1;
        int year = datePicker.getYear();
        int hour = tp.getHour();
        int minute = tp.getMinute();
        sb.append(year).append("-");

        if (month < 10) {
            sb.append("0");


        }
        sb.append(month);
        sb.append("-");
        if (day < 10) {

            sb.append("0");

        }
        sb.append(day);
        sb.append("T");
        if (hour < 10) {
            sb.append("0");
        }
        sb.append(hour);
        sb.append(":");
        if (minute < 10) {
            sb.append("0");


        }
        sb.append(minute);
        sb.append(":00+4:00");
        System.out.println(sb.toString());

        return sb.toString();
    }
}