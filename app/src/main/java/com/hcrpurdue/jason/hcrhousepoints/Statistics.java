package com.hcrpurdue.jason.hcrhousepoints;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import Models.House;
import Utils.Singleton;
import Utils.SingletonInterface;

public class Statistics extends Fragment {
    private Singleton singleton;
    private Context context;
    private Resources resources;
    private String packageName;
    private ProgressBar progressBar;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        singleton = Singleton.getInstance();
        resources = getResources();
        progressBar = Objects.requireNonNull(getActivity()).findViewById(R.id.navigationProgressBar);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.statistics, container, false);

        String house = singleton.getHouse();
        String floorText = house + " - " + singleton.getFloorName();
        packageName = context.getPackageName();

        int drawableID = getResources().getIdentifier(singleton.getHouse().toLowerCase(), "drawable", packageName);
        ((ImageView) view.findViewById(R.id.statistics_house_logo)).setImageResource(drawableID);
        ((TextView) view.findViewById(R.id.statistics_user_name)).setText(singleton.getName());
        ((TextView) view.findViewById(R.id.statistics_floor_name)).setText(floorText);

        BarChart chart = view.findViewById(R.id.statistics_point_chart);

        SwipeRefreshLayout swipeRefresh = view.findViewById(R.id.statistics_swipe_refresh);
        swipeRefresh.setOnRefreshListener(() -> updatePointData(view, chart, swipeRefresh));


        chart.getAxisLeft().setAxisMinimum(0);
        chart.getAxisLeft().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.setScaleEnabled(false);
        chart.setTouchEnabled(false);
        chart.setDrawBorders(false);
        chart.setNoDataText("Loading House Points...");
        chart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        chart.setAutoScaleMinMaxEnabled(true);
        chart.invalidate();

        updatePointData(view, chart, null);

        return view;
    }

    private void updatePointData(View view, BarChart chart, SwipeRefreshLayout swipeRefreshLayout) {
        singleton.getPointStatistics(new SingletonInterface() {
            @Override
            public void onGetPointStatisticsSuccess(List<House> data) {
                List<IBarDataSet> dataSetList = new ArrayList<>();
                float i = 0f;
                for (House house : singleton.getHouseList()) {
                    String houseName = house.getName();
                    if (houseName.equals(singleton.getHouse()))
                        ((TextView) view.findViewById(R.id.statistics_user_points)).setText(String.format(Locale.getDefault(),
                                "%d House Points | %d Individual Points", house.getTotalPoints(), singleton.getTotalPoints()));
                    BarEntry barEntry = new BarEntry(i++, house.getPointsPerResident());
                    ArrayList<BarEntry> barEntryList = new ArrayList<>();
                    barEntryList.add(barEntry);
                    BarDataSet dataSet = new BarDataSet(barEntryList, houseName);
                    int colorID = resources.getColor(resources.getIdentifier(houseName.toLowerCase(), "color", packageName));
                    dataSet.setColor(colorID);
                    dataSet.setValueTextSize(12);
                    dataSetList.add(dataSet);
                }
                BarData barData = new BarData(dataSetList);

                chart.setData(barData);
                chart.notifyDataSetChanged();
                chart.invalidate();

                progressBar.setVisibility(View.GONE);
                if (swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
