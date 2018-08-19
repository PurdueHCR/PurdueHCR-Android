package com.hcrpurdue.jason.hcrhousepoints;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Objects;

import Models.PointLog;
import Utils.ApprovePointListAdapter;
import Utils.Singleton;
import Utils.SingletonInterface;

public class ApprovePoints extends Fragment {
    private Singleton singleton;
    private AppCompatActivity activity;
    private ProgressBar spinner;
    private ListView approveList;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        activity = (AppCompatActivity) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        singleton = Singleton.getInstance(getContext());
        spinner = activity.findViewById(R.id.navigationProgressBar);
        spinner.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.approve_points, container, false);
        singleton.getCachedData();
        getUnconfirmedPoints(null);
        Objects.requireNonNull(activity.getSupportActionBar()).setTitle("Approve Points");
        SwipeRefreshLayout swipeRefresh = view.findViewById(R.id.approve_list_swipe_refresh);
        swipeRefresh.setOnRefreshListener(() -> getUnconfirmedPoints(swipeRefresh));
        approveList = view.findViewById(R.id.approve_list);
        return view;
    }

    private void getUnconfirmedPoints(SwipeRefreshLayout swipeRefresh) {
        singleton.getUnconfirmedPoints(new SingletonInterface() {
            @Override
            public void onUnconfirmedPointsSuccess(ArrayList<PointLog> logs) {
                ApprovePointListAdapter adapter = new ApprovePointListAdapter(logs, getContext(), singleton.getHouse(), singleton.getName(), spinner);
                approveList.setAdapter(adapter);
                spinner.setVisibility(View.GONE);
                if(swipeRefresh != null)
                    swipeRefresh.setRefreshing(false);
            }
        });
    }
}
