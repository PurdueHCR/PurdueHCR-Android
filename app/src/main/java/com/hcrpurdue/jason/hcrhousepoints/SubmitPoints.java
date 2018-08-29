package com.hcrpurdue.jason.hcrhousepoints;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import Models.PointType;
import Utils.Singleton;
import Utils.SingletonInterface;

public class SubmitPoints extends Fragment {
    static private Singleton singleton;
    private Context context;
    private AppCompatActivity activity;
    private ProgressBar progressBar;

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
        View view = inflater.inflate(R.layout.submit_points, container, false);
        singleton.getCachedData();
        // Sets the house picture
        try {
            int drawableID = getResources().getIdentifier(singleton.getHouse().toLowerCase(), "drawable", context.getPackageName());
            ((ImageView) view.findViewById(R.id.houseLogoImageView)).setImageResource(drawableID);
        } catch (Exception e) {
            Toast.makeText(context, "Failed to load house image", Toast.LENGTH_LONG).show();
            Log.e("SubmitPoints", "Error loading house image", e);
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

        getPointTypes(view);
        Objects.requireNonNull(activity.getSupportActionBar()).setTitle("Submit Points");
        Objects.requireNonNull(view).findViewById(R.id.submitPointButton).setOnClickListener(v -> submitPoint(view));

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
            singleton.getPointTypes(new SingletonInterface() {
                public void onPointTypeComplete(List<PointType> data) {
                    List<Map<String, String>> formattedPointTypes = new ArrayList<>();
                    for (PointType type : data) {
                        if (type.getResidentsCanSubmit()) {
                            Map<String, String> map = new HashMap<>();
                            map.put("text", type.getPointDescription());
                            map.put("subText", String.valueOf(type.getPointValue()) + " points");
                            formattedPointTypes.add(map);
                        } else
                            break;
                    }
                    SimpleAdapter adapter = new SimpleAdapter(context, formattedPointTypes, android.R.layout.simple_list_item_2, new String[]{"text", "subText"}, new int[]{android.R.id.text1, android.R.id.text2});
                    adapter.setDropDownViewResource(android.R.layout.simple_list_item_2);
                    ((Spinner) view.findViewById(R.id.pointTypeSpinner)).setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            Toast.makeText(context, "Failed to load point types", Toast.LENGTH_LONG).show();
            Log.e("SubmitPoints", "Error loading point types", e);
        }
    }

    public void submitPoint(View view) {
        TextView descriptionInput = activity.findViewById(R.id.descriptionInput);
        if (TextUtils.isEmpty(descriptionInput.getText().toString()))
            Toast.makeText(context, "Description is Required", Toast.LENGTH_SHORT).show();
        else {
            progressBar.setVisibility(View.VISIBLE);
            singleton.submitPoints(((EditText) view.findViewById(R.id.descriptionInput)).getText().toString(),
                    singleton.getPointTypeList().get(((Spinner) view.findViewById(R.id.pointTypeSpinner)).getSelectedItemPosition()),
                    new SingletonInterface() {
                        @Override
                        public void onSuccess() {
                            descriptionInput.setText("");
                            progressBar.setVisibility(View.GONE);
                            ((NavigationDrawer) activity).animateSuccess();
                        }
                    });
        }
    }
}
