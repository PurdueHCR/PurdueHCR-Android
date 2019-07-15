package com.hcrpurdue.jason.hcrhousepoints.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.hcrpurdue.jason.hcrhousepoints.Activities.NavigationDrawer;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointType;
import com.hcrpurdue.jason.hcrhousepoints.R;
import com.hcrpurdue.jason.hcrhousepoints.Utils.Singleton;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.SingletonInterface;

import java.util.Objects;

public class SubmitPointsFragment extends Fragment {
    static private Singleton singleton;
    private Context context;
    private AppCompatActivity activity;
    private ProgressBar progressBar;
    private PointType pointType;
    private EditText descriptionEditText;
    private TextView pointTypeTextView;
    private TextView pointTypeDescriptionTextView;
    private Button submitPointButton;

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
        View view = inflater.inflate(R.layout.fragment_submit_point, container, false);
        retrieveBundleData();
        singleton.getCachedData();

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
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        activity = (AppCompatActivity) getActivity();
        progressBar = Objects.requireNonNull(activity).findViewById(R.id.navigationProgressBar);

    }


    public void submitPoint() {

        if (TextUtils.isEmpty(descriptionEditText.getText().toString().trim()))
            Toast.makeText(context, "Description is Required", Toast.LENGTH_SHORT).show();
        else if (descriptionEditText.length() > 250) {
            Toast.makeText(context, "Description cannot be more than 250 characters", Toast.LENGTH_SHORT).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);

            singleton.submitPoints(descriptionEditText.getText().toString(),
                    pointType,
                    new SingletonInterface() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.INVISIBLE);
                            ((NavigationDrawer) activity).animateSuccess();
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
}
