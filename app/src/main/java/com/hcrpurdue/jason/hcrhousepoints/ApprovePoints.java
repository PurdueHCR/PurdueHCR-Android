package com.hcrpurdue.jason.hcrhousepoints;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Objects;

import Utils.Singleton;

public class ApprovePoints extends Fragment {
    private Singleton singleton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        singleton = Singleton.getInstance();
        getUnconfirmedPoints();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.approve_points, container, false);
        view.findViewById(R.id.submitPointButton).setOnClickListener(this::submitPoint);
        return view;
    }

    private void getUnconfirmedPoints() {

    }
}
