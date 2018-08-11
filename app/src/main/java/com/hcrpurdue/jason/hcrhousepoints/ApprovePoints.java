package com.hcrpurdue.jason.hcrhousepoints;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Models.PointLog;
import Utils.ApprovePointListAdapter;
import Utils.Singleton;
import Utils.SingletonInterface;

public class ApprovePoints extends Fragment {
    private Singleton singleton;
    ProgressBar spinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        singleton = Singleton.getInstance();
        spinner = getActivity().findViewById(R.id.navigationProgressBar);
        spinner.setVisibility(View.VISIBLE);
        getUnconfirmedPoints();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.approve_points, container, false);
        return view;
    }

    private void getUnconfirmedPoints() {
        singleton.getUnconfirmedPoints(new SingletonInterface() {
            @Override
            public void onUnconfirmedPointsSuccess(ArrayList<PointLog> logs) {
                ApprovePointListAdapter adapter = new ApprovePointListAdapter(logs, getContext(), singleton.getHouse(), singleton.getName(), spinner);
                ((ListView) Objects.requireNonNull(getActivity()).findViewById(R.id.approve_list)).setAdapter(adapter);
                spinner.setVisibility(View.GONE);
            }
        });
    }
}
