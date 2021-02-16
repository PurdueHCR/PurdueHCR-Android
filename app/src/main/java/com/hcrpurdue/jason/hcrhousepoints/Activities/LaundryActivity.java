package com.hcrpurdue.jason.hcrhousepoints.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.hcrpurdue.jason.hcrhousepoints.R;

public class LaundryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundry);
        String building = getIntent().getStringExtra("building");
        System.out.println(building);

    }
}