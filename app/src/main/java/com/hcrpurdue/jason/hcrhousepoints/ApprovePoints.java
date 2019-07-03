package com.hcrpurdue.jason.hcrhousepoints;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import Models.SystemPreferences;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

import Models.PointLog;
import Utils.ApprovePointListAdapter;
import Utils.Singleton;
import Utils.SingletonInterface;

public class ApprovePoints extends Fragment {
    static private Singleton singleton;
    private Context context;
    private ProgressBar progressBar;
    private ListView approveList;
    private TextView houseDisabledTextView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        singleton = Singleton.getInstance(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.approve_points, container, false);
        singleton.getCachedData();
        approveList = view.findViewById(R.id.approve_list);
        houseDisabledTextView = view.findViewById(R.id.houseDisabledTextView);
        SwipeRefreshLayout swipeRefresh = view.findViewById(R.id.approve_list_swipe_refresh);
        swipeRefresh.setOnRefreshListener(() -> getUnconfirmedPoints(swipeRefresh));
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity) Objects.requireNonNull(getActivity());
        progressBar = activity.findViewById(R.id.navigationProgressBar);

        boolean isHouseEnabled = singleton.getCachedSystemPreferences().isHouseEnabled();

        if (!isHouseEnabled) {
            approveList.setVisibility(View.GONE);
            houseDisabledTextView.setText(singleton.getCachedSystemPreferences().getHouseIsEnabledMsg());
            houseDisabledTextView.setVisibility(View.VISIBLE);

        } else {
            approveList.setVisibility(View.VISIBLE);
            houseDisabledTextView.setVisibility(View.GONE);
        }

        getUnconfirmedPoints(null);
        progressBar.setVisibility(View.VISIBLE);
        Objects.requireNonNull(activity.getSupportActionBar()).setTitle("Approve Points");

    }

    private void getUnconfirmedPoints(SwipeRefreshLayout swipeRefresh) {
        singleton.getUnconfirmedPoints(new SingletonInterface() {
            @Override
            public void onUnconfirmedPointsSuccess(ArrayList<PointLog> logs) {
                ApprovePointListAdapter adapter = new ApprovePointListAdapter(logs, context, singleton.getHouse(), singleton.getName(), progressBar);
                approveList.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
                if (swipeRefresh != null)
                    swipeRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        MenuInflater inflater1 = new MenuInflater(context);

        inflater1.inflate(R.menu.approve_list, menu);
        super.onCreateOptionsMenu(menu, inflater1);
    }


    //TODO: Check
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_show_pt_history) {
            startActivity(new Intent(ApprovePoints.this.getActivity(), UpdatePoints.class));
            return true;
        }

        return false;
    }


}

