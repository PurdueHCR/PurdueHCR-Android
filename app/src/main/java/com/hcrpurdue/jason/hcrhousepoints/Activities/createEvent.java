package com.hcrpurdue.jason.hcrhousepoints.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.hcrpurdue.jason.hcrhousepoints.Models.Event;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointType;
import com.hcrpurdue.jason.hcrhousepoints.Models.ResponseMessage;
import com.hcrpurdue.jason.hcrhousepoints.R;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;
import com.hcrpurdue.jason.hcrhousepoints.Utils.HttpNetworking.APIHelper;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class createEvent extends AppCompatActivity {
    EditText eventName, location, eventHost, points, eventDescription;
    Spinner pointTypeSpinner;
    DatePicker startDatePicker, endDatePicker;
    TimePicker startTimePicker, endTimePicker;
    Button createevent, customButton,deleteButton;
    ProgressBar progressBar;
    String customFloorList;
    SwitchMaterial allFloorsSwitch, myHouseSwitch, myFloorSwitch, isPublicSwitch;
    CacheManager cacheManager = CacheManager.getInstance(createEvent.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        eventName = findViewById(R.id.event_name_input);
        location = findViewById(R.id.location_input);
        eventHost = findViewById(R.id.hostInput);
        pointTypeSpinner = findViewById(R.id.pointsSpinner);

        eventDescription = findViewById(R.id.description_input);

        startDatePicker = findViewById(R.id.dateINput);
        endDatePicker = findViewById(R.id.endDatePicker);

        startTimePicker = findViewById(R.id.timePicker);
        endTimePicker = findViewById(R.id.endTimePIcker);

        createevent = findViewById(R.id.button);
        createevent.setText("Create Event");
        customButton = findViewById(R.id.customButton);
        deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setVisibility(View.INVISIBLE);

        progressBar = findViewById(R.id.progressBar);

        allFloorsSwitch = findViewById(R.id.allFloorsSwitch);
        myFloorSwitch = findViewById(R.id.myFloorSwitch);
        myHouseSwitch = findViewById(R.id.myHouseSwitch);
        isPublicSwitch = findViewById(R.id.isPublicSwitch);

        progressBar.setVisibility(View.INVISIBLE);

        //fetch point types
        fetchPointTypes();
        //getting list of floors from CustomFloorlist
        if (getIntent().getStringExtra("floorList") != null) {
            if (!getIntent().getStringExtra("floorList").equals("")) {
                customFloorList = getIntent().getStringExtra("floorList");
                System.out.println(customFloorList);
                allFloorsSwitch.setChecked(false);
                myFloorSwitch.setChecked(false);
                myHouseSwitch.setChecked(false);
                isPublicSwitch.setChecked(false);
            }
        }
        if (savedInstanceState != null) {
            System.out.println(savedInstanceState.getString("eventName"));
            eventName.setText(savedInstanceState.getString("eventName"));
            eventHost.setText(savedInstanceState.getString("host"));
            eventDescription.setText(savedInstanceState.getString("description"));
            location.setText(savedInstanceState.getString("location"));

        }
        customButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(createEvent.this, CustomFloorList.class);
                startActivity(intent);
            }
        });
        myHouseSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (myHouseSwitch.isChecked()) {
                        allFloorsSwitch.setChecked(false);
                        myFloorSwitch.setChecked(false);
                        isPublicSwitch.setChecked(false);

                        //myHouseSwitch.setChecked(false);
                    } else {
                        //myHouseSwitch.setChecked(true);

                        // The toggle is disabled
                    }

                }
            }
        });
        allFloorsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                   /// allFloorsSwitch.setChecked(false);
                    myHouseSwitch.setChecked(false);
                    myFloorSwitch.setChecked(false);

                } else {
                  //  allFloorsSwitch.setChecked(true);

                    // The toggle is disabled
                }
            }
        });
        myFloorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                 //   myFloorSwitch.setChecked(false);
                    allFloorsSwitch.setChecked(false);
                    myHouseSwitch.setChecked(false);
                    isPublicSwitch.setChecked(false);

                } else {
                  //  myFloorSwitch.setChecked(true);

                }
            }
        });

        createevent.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            if (!myFloorSwitch.isChecked() && !myHouseSwitch.isChecked() && !allFloorsSwitch.isChecked() && customFloorList == null) {
                AlertDialog alertDialog = new AlertDialog.Builder(createEvent.this).create();
                alertDialog.setTitle("Important");
                alertDialog.setMessage("Please make sure one of the house switches are on or you have set a custom list");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

                return;
            }
            if (eventName.getText().toString().length() == 0 || location.getText().toString().length() == 0 || eventDescription.getText().toString().length() == 0) {
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
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                alertDialog.show();


            } else {
                String host, name, locationstr, pointTypeName, description, floor, startTime, endTime, date;
                if (eventHost.getText().toString().trim().length() == 0) {
                    CacheManager cacheManager = CacheManager.getInstance(getApplicationContext());
                    host = cacheManager.getUser().getFirstName() + " " + cacheManager.getUser().getLastName();
                } else {
                    host = eventHost.getText().toString().trim();
                }
                Event event;
                try {


                    name = eventName.getText().toString().trim();
                    locationstr = location.getText().toString().trim();

                    pointTypeName = pointTypeSpinner.getSelectedItem().toString();
                    description = eventDescription.getText().toString().trim();
                    if (allFloorsSwitch.isChecked()) {

                    }


                    boolean isAllFloors = false;
                    boolean publicEvent = false;
                    String[] floorIDS = new String[1];
                    if (isPublicSwitch.isChecked()) {
                        publicEvent = true;

                    }
                    if (allFloorsSwitch.isChecked()) {
                        floorIDS = new String[1];
                        floorIDS[0] = "All Floors";
                        isAllFloors = true;


                    } else if (myHouseSwitch.isChecked()) {
                        String houseName = cacheManager.getUser().getHouseName();
                        floorIDS = new String[2];
                        switch (houseName) {
                            case "Copper":
                                floorIDS[0] = "2N";
                                floorIDS[1] = "2S";
                            case "Palladium":
                                floorIDS[0] = "3N";
                                floorIDS[1] = "3S";
                            case "Silver":
                                floorIDS[0] = "5N";
                                floorIDS[1] = "5S";
                            case "Titanium":
                                floorIDS[0] = "6N";
                                floorIDS[1] = "6S";
                            case "Platinum":
                                floorIDS[0] = "4N";
                                floorIDS[1] = "4S";
                        }
                    } else if (myFloorSwitch.isChecked()) {
                        floorIDS = new String[1];
                        floorIDS[0] = cacheManager.getUser().getFloorId();

                    } else {
                        if (customFloorList == null || customFloorList.equals("")) {
                            AlertDialog alertDialog = new AlertDialog.Builder(createEvent.this).create();
                            alertDialog.setTitle("Important");
                            alertDialog.setMessage("Please make sure one of the house switches are on or you have set a custom list");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            progressBar.setVisibility(View.INVISIBLE);
                                        }
                                    });
                            alertDialog.show();


                        } else {
                            System.out.println(customFloorList);
                            floorIDS = customFloorList.split(",");

                        }
                    }
                    //  if (floor.equals("All Floors")) {
                    //     isAllFloors = true;

                    // }
                    //  String[] floorID = {floor};

                    String actualSD = generateDate(startTimePicker, startDatePicker);
                    String actualED = generateDate(endTimePicker, endDatePicker);

                    System.out.println("Name:" + name);
                    System.out.println("Description:" + description);
                    System.out.println("SD:" + actualSD);
                    System.out.println("ED:" + actualED);
                    System.out.println("Location:" + locationstr);
                    System.out.println("Points:" + fetchPointValue(pointTypeName));
                    //System.out.println("Floor id:" + floorID[0]);
                    System.out.println("Host:" + host);
                    event = new Event(name, description, actualSD, actualED, locationstr, fetchPointValue(pointTypeName), floorIDS, publicEvent, isAllFloors, host);
                    // String[] flooors = new String[1];
                    // flooors[0] = "2N";
                    //event = new Event("A Very Fun Event","A very fun event by me!","2020-11-08T10:00:00+04:00","2020-11-08T10:00:00+04:00","HCRS 1066",22,flooors,false,false,"The Society");
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
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                    alertDialog.show();


                }
            }
        });
    }

    //fetching available point types
    private void fetchPointTypes() {
        List<PointType> pointTypes = cacheManager.getPointTypeList();
        String[] pointTypeTitles = new String[pointTypes.size()];
        for (int i = 0; i < pointTypeTitles.length; i++) {
            pointTypeTitles[i] = pointTypes.get(i).getName();
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                createEvent.this, android.R.layout.simple_spinner_item, pointTypeTitles);
        pointTypeSpinner.setAdapter(spinnerArrayAdapter);


    }
    private int fetchPointValue(String pointTypeName) {
        List<PointType> pointTypes = cacheManager.getPointTypeList();
        for (PointType pointType: pointTypes) {
            if (pointType.getName().equals(pointTypeName)) {

                return pointType.getId();

            }

        }
        return 0;

    }

    private void postEvent(Event event) {
        progressBar.setVisibility(View.VISIBLE);
        APIHelper.getInstance(createEvent.this).postEvent(event).enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                System.out.println("Event Posted: " + response.code());
                if (response.isSuccessful()) {
                    System.out.println(" GOT RESPONSE: " + response.body().getMessage());
                    AlertDialog alertDialog = new AlertDialog.Builder(createEvent.this).create();
                    alertDialog.setTitle("Success");
                    alertDialog.setMessage("You have successfully created an event!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();


                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(createEvent.this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("There was a problem creating the event. Please make sure all the fields are filled out correctly and try again.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
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

    private String generateDate(TimePicker tp, DatePicker datePicker) throws ParseException {
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
        sb.append(":00+04:00");
        System.out.println(sb.toString());

        return sb.toString();
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putString("eventName", eventName.getText().toString());
        savedInstanceState.putString("host", eventHost.getText().toString());
        savedInstanceState.putString("location", location.getText().toString());
        savedInstanceState.putString("description", eventDescription.getText().toString());
        super.onSaveInstanceState(savedInstanceState);
        //@TODO save data from pickers

    }
}