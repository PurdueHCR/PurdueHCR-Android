package com.hcrpurdue.jason.hcrhousepoints;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Models.PointType;
import Utils.Singleton;
import Utils.SingletonInterface;

public class SubmitPoints extends AppCompatActivity {
    private Singleton singleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_points);
        singleton = Singleton.getInstance();
        getPointTypes();
        try {
            int drawableID = getResources().getIdentifier(singleton.getHouse().toLowerCase(), "drawable", this.getPackageName());
            ((ImageView) findViewById(R.id.houseLogoImageView)).setImageResource(drawableID);
        } catch (Exception e) {
            Toast.makeText(this, "Failed to load house image, please screenshot the Logs page and send it to your RHP", Toast.LENGTH_LONG).show();
            Log.e("SubmitPoints", "Error loading house image", e);
        }
    }


    private void getPointTypes() {
        singleton.getPointTypes(new SingletonInterface() {
            public void onPointTypeComplete(List<PointType> data) {
                List<Map<String, String>> formattedPointTypes = new ArrayList<>();
                for (PointType type : data) {
                    Map<String, String> map = new HashMap<>();
                    map.put("text", type.getPointDescription());
                    map.put("subText", String.valueOf(type.getPointValue()) + " points");
                    formattedPointTypes.add(map);
                }
                SimpleAdapter adapter = new SimpleAdapter(SubmitPoints.this, formattedPointTypes, android.R.layout.simple_list_item_2, new String[]{"text", "subText"}, new int[]{android.R.id.text1, android.R.id.text2});
                adapter.setDropDownViewResource(android.R.layout.simple_list_item_2);
                ((Spinner) findViewById(R.id.pointTypeSpinner)).setAdapter(adapter);
            }
        }, this);
    }

    public void submitPoint(View view) {
        singleton.submitPoints(((EditText) findViewById(R.id.descriptionInput)).getText().toString(),
                singleton.getPointTypeList().get(((Spinner) findViewById(R.id.pointTypeSpinner)).getSelectedItemPosition()),
                new SingletonInterface() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(SubmitPoints.this,"Point successfully added", Toast.LENGTH_SHORT).show();
                    }
                });

    }

}
