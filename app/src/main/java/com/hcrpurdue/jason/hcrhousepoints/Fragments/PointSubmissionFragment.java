package com.hcrpurdue.jason.hcrhousepoints.Fragments;

import android.content.Context;

import com.hcrpurdue.jason.hcrhousepoints.Models.SystemPreferences;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hcrpurdue.jason.hcrhousepoints.Activities.NavigationActivity;
import com.hcrpurdue.jason.hcrhousepoints.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.hcrpurdue.jason.hcrhousepoints.Models.PointType;
import com.hcrpurdue.jason.hcrhousepoints.Utils.Singleton;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.ListenerCallbackInterface;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.SingletonInterface;

public class PointSubmissionFragment extends Fragment implements ListenerCallbackInterface {
    static private Singleton singleton;
    private Context context;
    private AppCompatActivity activity;
    private ProgressBar progressBar;
    private ArrayList<PointType> enabledTypes = new ArrayList<PointType>();
    private boolean isFinished;
    private TextView statUpdateTextView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        singleton = Singleton.getInstance(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_point_submission, container, false);
        singleton.getCachedData();
        statUpdateTextView = view.findViewById(R.id.statusUpdateTextView);

//        try {
        getPointTypes(view);

//            view.findViewById(R.id.noPTypesTxtView).setVisibility(View.GONE);
//            if (enabledTypes.size() == 0) {
//                view.findViewById(R.id.pointTypeSpinner).setVisibility(View.GONE);
//                view.findViewById(R.id.descriptionInput).setVisibility(View.GONE);
//                view.findViewById(R.id.noPTypesTxtView).setVisibility(View.VISIBLE);
//            }

//        catch(Exception e) {
//            Toast.makeText(context, "Issue in onCreateView", Toast.LENGTH_LONG).show();
//        }


        // Sets the house picture
        try {
            int drawableID = getResources().getIdentifier(singleton.getHouse().toLowerCase(), "drawable", context.getPackageName());
            ((ImageView) view.findViewById(R.id.houseLogoImageView)).setImageResource(drawableID);
        } catch (Exception e) {
            Toast.makeText(context, "Failed to load house image", Toast.LENGTH_LONG).show();
            Log.e("PointSubmissionFragment", "Error loading house image", e);
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (AppCompatActivity) getActivity();
        progressBar = Objects.requireNonNull(activity).findViewById(R.id.navigationProgressBar);
        progressBar.setVisibility(View.VISIBLE);
        View view = getView();


        try {

            statUpdateTextView.setVisibility(View.GONE);
            view.findViewById(R.id.pointTypeSpinner).setVisibility(View.GONE);
            view.findViewById(R.id.descriptionInput).setVisibility(View.GONE);

            getPointTypes(view);
            Objects.requireNonNull(activity.getSupportActionBar()).setTitle("Submit Points");
            Objects.requireNonNull(view).findViewById(R.id.submitPointButton).setOnClickListener(v -> submitPoint(view));



//            statUpdateTextView.setVisibility(View.GONE);
//            if (enabledTypes.size() == 0 && !progressBar.isShown()) {       //TODO: Check
//                view.findViewById(R.id.pointTypeSpinner).setVisibility(View.GONE);
//                view.findViewById(R.id.descriptionInput).setVisibility(View.GONE);
//                statUpdateTextView.setVisibility(View.VISIBLE);
//            }

        } catch (Exception e) {
            Toast.makeText(context, "Issue in onActivityCreated", Toast.LENGTH_LONG).show();
        }


        ((EditText) view.findViewById(R.id.descriptionInput)).setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE || (i == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN)) {
                submitPoint(view);
                View currentFocus = activity.getCurrentFocus();
                if (currentFocus != null) {
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    Objects.requireNonNull(imm).hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                }
            }

            return true;
        });
    }


    private void getPointTypes(View view) {

        try {
            singleton.getUpdatedPointTypes(new SingletonInterface() {
                public void onPointTypeComplete(List<PointType> data) {
                    List<Map<String, String>> formattedPointTypes = new ArrayList<>();
                    for (PointType type : data) {
                        if (type.getResidentsCanSubmit() && type.isEnabled()) {
                            enabledTypes.add(type);
                            Map<String, String> map = new HashMap<>();
                            map.put("text", type.getPointDescription());
                            map.put("subText", String.valueOf(type.getValue()) + " points");
                            formattedPointTypes.add(map);
                        }
                    }

             singleton.getSystemPreferences(new SingletonInterface() {
//                 @Override
//                 public void onError(Exception e, Context context) {
//
//                 }

                 @Override
                 public void onGetSystemPreferencesSuccess(SystemPreferences systemPreferences) {

                     SimpleAdapter adapter = new SimpleAdapter(context, formattedPointTypes, android.R.layout.simple_list_item_2, new String[]{"text", "subText"}, new int[]{android.R.id.text1, android.R.id.text2});
                     adapter.setDropDownViewResource(android.R.layout.simple_list_item_2);
                     ((Spinner) view.findViewById(R.id.pointTypeSpinner)).setAdapter(adapter);



                     // Objects.requireNonNull(activity.getSupportActionBar()).setTitle("Submit Points");

                     Objects.requireNonNull(view).findViewById(R.id.submitPointButton).setOnClickListener(v -> submitPoint(view));

                     view.findViewById(R.id.statusUpdateTextView).setVisibility(View.GONE);




                      if(!systemPreferences.isHouseEnabled()) {
                         view.findViewById(R.id.pointTypeSpinner).setVisibility(View.GONE);
                         view.findViewById(R.id.descriptionInput).setVisibility(View.GONE);
                         statUpdateTextView.setText(systemPreferences.getHouseIsEnabledMsg());
                         statUpdateTextView.setVisibility(View.VISIBLE);
                     }

                     else if (enabledTypes.size() == 0) {
                         view.findViewById(R.id.pointTypeSpinner).setVisibility(View.GONE);
                         view.findViewById(R.id.descriptionInput).setVisibility(View.GONE);
                         statUpdateTextView.setText(R.string.no_point_types_enabled);
                         statUpdateTextView.setVisibility(View.VISIBLE);
                     }
                     else {
                         view.findViewById(R.id.pointTypeSpinner).setVisibility(View.VISIBLE);
                         view.findViewById(R.id.descriptionInput).setVisibility(View.VISIBLE);
                         statUpdateTextView.setVisibility(View.GONE);
                     }


                     progressBar.setVisibility(View.GONE);
                 }
             });


                }
            });

        } catch (Exception e) {
            Toast.makeText(context, "Failed to load point types", Toast.LENGTH_LONG).show();
            Log.e("PointSubmissionFragment", "Error loading point types", e);
        }
    }

    public void submitPoint(View view) {
        TextView descriptionInput = activity.findViewById(R.id.descriptionInput);
        if (TextUtils.isEmpty(descriptionInput.getText().toString().trim()))
            Toast.makeText(context, "Description is Required", Toast.LENGTH_SHORT).show();
        else if (descriptionInput.length() > 250) {
            Toast.makeText(context, "Description cannot be more than 250 characters", Toast.LENGTH_SHORT).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);


            PointType type = enabledTypes.get(((Spinner) view.findViewById(R.id.pointTypeSpinner)).getSelectedItemPosition());

//            singleton.submitPoints(((EditText) view.findViewById(R.id.descriptionInput)).getText().toString(),
//                    type,
//                    new SingletonInterface() {
//                        @Override
//                        public void onSuccess() {
//                            descriptionInput.setText("");
//                            progressBar.setVisibility(View.GONE);
//                            ((NavigationActivity) activity).animateSuccess();
//                        }
//                    });
        }

    }
}


//TODO: Create variable of all PointTypes in picker,
