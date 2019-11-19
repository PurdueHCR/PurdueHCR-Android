/**
 * SubmitPointsFragment displays the form where a user can submit a point. Contains label for
 * point type, and description. Also date and time input and description input
 */

package com.hcrpurdue.jason.hcrhousepoints.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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

    private Calendar calendar;
    private boolean dateSet;

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

        DatePicker dp = view.findViewById(R.id.date_button);
        final Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        int currentMonth = c.get(Calendar.MONTH);
        int currentDay = c.get(Calendar.DAY_OF_MONTH);

        c.set(Calendar.YEAR, currentYear - 1);
        c.set(Calendar.MONTH, currentMonth);
        c.set(Calendar.DAY_OF_MONTH, currentDay);
        dp.setMinDate(c.getTimeInMillis());
        //setting the minimum Date that can be chosen

        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, currentYear);
        cal.set(Calendar.MONTH, currentMonth);
        cal.set(Calendar.DAY_OF_MONTH, currentDay);
        dp.setMaxDate(cal.getTimeInMillis());
        //setting the maximum Date that can be chosen::wq
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
        return view;
    } //onCreateView

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
        } else {
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
        if (pointType == null) {
            pointType = new PointType(0, "Fake", "Fake Description", true, 1000, true, 3);
        }
    }

    private void displayDatePicker() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        int currentMonth = c.get(Calendar.MONTH);
        int currentDay = c.get(Calendar.DAY_OF_MONTH);
        calendar.set(currentYear, currentMonth, currentDay);

        new SpinnerDatePickerDialogBuilder()
                .context(context)
                .callback((view, year, monthOfYear, dayOfMonth) -> calendar.set(year, monthOfYear, dayOfMonth))
                .spinnerTheme(R.style.NumberPickerStyle)
                .showTitle(true)
                .showDaySpinner(true)
                .defaultDate(currentYear, currentMonth, currentDay)
                .maxDate(currentYear, currentMonth, currentDay)
                .minDate(currentYear, 0, 1)
                .build()
                .show();
    }
}
