package com.hcrpurdue.jason.hcrhousepoints;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
    private Singleton singleton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        singleton = Singleton.getInstance();
        getPointTypes();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.submit_points, container, false);
        // Sets the house picture
        try {
            int drawableID = getResources().getIdentifier(singleton.getHouse().toLowerCase(), "drawable", Objects.requireNonNull(getActivity()).getPackageName());
            ((ImageView) view.findViewById(R.id.houseLogoImageView)).setImageResource(drawableID);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Failed to load house image, please screenshot the Logs page and send it to your RHP", Toast.LENGTH_LONG).show();
            Log.e("SubmitPoints", "Error loading house image", e);
        }
        view.findViewById(R.id.submitPointButton).setOnClickListener(this::submitPoint);
        return view;
    }


    private void getPointTypes() {
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
                SimpleAdapter adapter = new SimpleAdapter(getContext(), formattedPointTypes, android.R.layout.simple_list_item_2, new String[]{"text", "subText"}, new int[]{android.R.id.text1, android.R.id.text2});
                adapter.setDropDownViewResource(android.R.layout.simple_list_item_2);
                ((Spinner) Objects.requireNonNull(getView()).findViewById(R.id.pointTypeSpinner)).setAdapter(adapter);
            }
        }, getContext());
    }

    public void submitPoint(View view) {
        TextView descriptionInput = Objects.requireNonNull(getActivity()).findViewById(R.id.descriptionInput);
        if (TextUtils.isEmpty(descriptionInput.getText().toString()))
            Toast.makeText(getContext(), "Description is Required", Toast.LENGTH_SHORT).show();
        else {
            getActivity().findViewById(R.id.navigationProgressBar).setVisibility(View.VISIBLE);
            singleton.submitPoints(((EditText) Objects.requireNonNull(getView()).findViewById(R.id.descriptionInput)).getText().toString(),
                    singleton.getPointTypeList().get(((Spinner) Objects.requireNonNull(getView()).findViewById(R.id.pointTypeSpinner)).getSelectedItemPosition()),
                    new SingletonInterface() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getContext(), "Point successfully added", Toast.LENGTH_SHORT).show();
                            descriptionInput.setText("");
                            getActivity().findViewById(R.id.navigationProgressBar).setVisibility(View.GONE);
                        }
                    });
        }

    }

}
