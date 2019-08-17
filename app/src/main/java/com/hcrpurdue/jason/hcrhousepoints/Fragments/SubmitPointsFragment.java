/**
 *  SubmitPointsFragment displays the form where a user can submit a point. Contains label for
 *      point type, and description. Also date and time input and description input
 */

package com.hcrpurdue.jason.hcrhousepoints.Fragments;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.hcrpurdue.jason.hcrhousepoints.Activities.NavigationActivity;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointType;
import com.hcrpurdue.jason.hcrhousepoints.R;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.ListenerCallbackInterface;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.CacheManagementInterface;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;

public class SubmitPointsFragment extends Fragment implements ListenerCallbackInterface {
    static private CacheManager cacheManager;
    private Context context;
    private AppCompatActivity activity;
    private ProgressBar progressBar;
    private PointType pointType;
    private EditText descriptionEditText;
    private TextView pointTypeTextView;
    private TextView pointTypeDescriptionTextView;
    private Button submitPointButton;
    private Button setDateButton;
    //private Button setTimeButton;

    private Calendar calendar;
    private boolean dateSet;
    //private boolean timeSet;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cacheManager = CacheManager.getInstance(getContext());
        calendar = new GregorianCalendar();
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_submit_point, container, false);
        retrieveBundleData();
        cacheManager.getCachedData();

        pointTypeTextView = view.findViewById(R.id.submit_point_type_text_view);
        pointTypeDescriptionTextView = view.findViewById(R.id.submit_point_type_description_text_view);
        descriptionEditText = view.findViewById(R.id.description_edit_text);

        pointTypeTextView.setText(pointType.getName());
        pointTypeDescriptionTextView.setText(pointType.getPointDescription());
        submitPointButton = view.findViewById(R.id.submit_point_button);
        submitPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitPoint();
            }
        });

        setDateButton = view.findViewById(R.id.date_button);
        setDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDatePicker();
            }
        });

//        setTimeButton = view.findViewById(R.id.time_button);
//        setTimeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                displayTimePicker();
//            }
//        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        activity = (AppCompatActivity) getActivity();
        progressBar = Objects.requireNonNull(activity).findViewById(R.id.navigationProgressBar);

    }


    public void submitPoint() {
        InputMethodManager inputManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null && getActivity().getCurrentFocus() != null) // Avoids null pointer exceptions
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

        if (TextUtils.isEmpty(descriptionEditText.getText().toString().trim()))
            Toast.makeText(context, "Description is Required", Toast.LENGTH_SHORT).show();
        else if (descriptionEditText.length() > 250) {
            Toast.makeText(context, "Description cannot be more than 250 characters", Toast.LENGTH_SHORT).show();
        }
        else if (!dateSet){
            Toast.makeText(context, "Please set a date before you submit.", Toast.LENGTH_SHORT).show();
        }
//        else if(!timeSet){
//            Toast.makeText(context, "Please set a time before you submit.", Toast.LENGTH_SHORT).show();
//        }
        else{
            progressBar.setVisibility(View.VISIBLE);

            cacheManager.submitPoints(descriptionEditText.getText().toString(), calendar.getTime(),
                    pointType,
                    new CacheManagementInterface() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.INVISIBLE);
                            ((NavigationActivity) activity).animateSuccess();
                            getFragmentManager().popBackStackImmediate();
                        }
                    });
        }
    }

    /**
     * Attempts to retrieve a PointType model from the Bundle. Call this when initializing the view.
     * To put values in the correct place, when you build a fragmentTransatction, put a Bundle into the fragment with fragment.setArguments(Bundle);
     */
    private void retrieveBundleData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            pointType = (PointType) bundle.getSerializable("POINTTYPE");
        }
        if(pointType == null){
            pointType = new PointType(0,"Fake","Fake Description",true,1000,true,3);
        }
    }

    private void displayDatePicker(){
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        int currentMonth = c.get(Calendar.MONTH);
        int currentDay = c.get(Calendar.DAY_OF_MONTH);
        new SpinnerDatePickerDialogBuilder()
                .context(context)
                .callback(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        setDateButton.setText(""+(monthOfYear+1)+"/"+dayOfMonth+"/"+year);
                        calendar.set(year,monthOfYear,dayOfMonth);
                        dateSet = true;
                    }
                })
                .spinnerTheme(R.style.NumberPickerStyle)
                .showTitle(true)
                .showDaySpinner(true)
                .defaultDate(currentYear, currentMonth, currentDay)
                .maxDate(currentYear, 11, 31)
                .minDate(currentYear, 0, 1)
                .build()
                .show();

    }

//    private void displayTimePicker(){
//        final Calendar c = Calendar.getInstance();
//        int hour = c.get(Calendar.HOUR_OF_DAY);
//        int minute = c.get(Calendar.MINUTE);
//
//        new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
//            @Override
//            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
//                int formattedHour = ((hour%12) == 0)?12:(hour%12);
//                String formattedMinute = (minute < 10)?("0"+minute):""+minute;
//                String time = ""+formattedHour+":"+formattedMinute+((hour > 11)?"pm":"am");
//                setTimeButton.setText(time);
//                calendar.set(Calendar.HOUR,hour);
//                calendar.set(Calendar.MINUTE,minute);
//                timeSet = true;
//            }
//        },hour,minute, false).show();
//    }
}
