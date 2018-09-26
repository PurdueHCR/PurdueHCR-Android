package com.hcrpurdue.jason.hcrhousepoints;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
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
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import Models.House;
import Models.Reward;
import Utils.Singleton;
import Utils.SingletonInterface;

public class Statistics extends Fragment {
    static private Singleton singleton;
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
        singleton = Singleton.getInstance(getContext());
        resources = getResources();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.statistics, container, false);
        BarChart chart = view.findViewById(R.id.statistics_point_chart);
        packageName = context.getPackageName();

        singleton.getCachedData();
        String house = singleton.getHouse();
        String floorText = house + " - " + singleton.getFloorName();
        int drawableID = resources.getIdentifier(singleton.getHouse().toLowerCase(), "drawable", packageName);
        ((ImageView) view.findViewById(R.id.statistics_house_icon)).setImageResource(drawableID);
        ((ImageView) view.findViewById(R.id.statistics_house_icon_small)).setImageResource(drawableID);
        ((TextView) view.findViewById(R.id.statistics_user_name)).setText(singleton.getName());
        ((TextView) view.findViewById(R.id.statistics_floor_name)).setText(floorText);

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

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        progressBar = Objects.requireNonNull(activity).findViewById(R.id.navigationProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        View view = Objects.requireNonNull(getView());
        updatePointData(view, view.findViewById(R.id.statistics_point_chart), null);
        progressBar = Objects.requireNonNull(getActivity()).findViewById(R.id.navigationProgressBar);
        progressBar.setVisibility(View.VISIBLE);
        Objects.requireNonNull(activity.getSupportActionBar()).setTitle("Statistics");
    }

    private void updatePointData(View view, BarChart chart, SwipeRefreshLayout swipeRefreshLayout) {
        TextView housePointsTextView = view.findViewById(R.id.statistics_user_points);
        singleton.getPointStatistics(new SingletonInterface() {
            @Override
            public void onGetPointStatisticsSuccess(List<House> houses, int userPoints, List<Reward> rewards) {
                List<IBarDataSet> dataSetList = new ArrayList<>();

                int housePoints = 0;
                // So that things are in podium order (4, 2, 1, 3, 5)
                Collections.sort(houses);
                Collections.swap(houses, 0, 2);
                Collections.swap(houses, 0, 3);

                float i = 0f;
                for (House house : houses) {
                    String houseName = house.getName();
                    if (houseName.equals(singleton.getHouse())) {
                        housePoints = house.getTotalPoints();
                        housePointsTextView.setText(String.format(Locale.getDefault(),
                                "%d House Points | %d Individual Points", housePoints, userPoints));
                    }

                    BarEntry barEntry = new BarEntry(i++, house.getPointsPerResident());
                    ArrayList<BarEntry> barEntryList = new ArrayList<>();
                    barEntryList.add(barEntry);
                    BarDataSet dataSet = new BarDataSet(barEntryList, houseName);
                    int colorID = ContextCompat.getColor(context, resources.getIdentifier(houseName.toLowerCase(), "color", packageName));
                    dataSet.setColor(colorID);
                    dataSet.setValueTextSize(12);
                    dataSetList.add(dataSet);
                }
                BarData barData = new BarData(dataSetList);

                chart.setData(barData);
                chart.notifyDataSetChanged();
                chart.invalidate();

                Collections.sort(rewards); // Just in case
                int requiredPoints = 0;
                for (Reward reward : rewards) {
                    requiredPoints = reward.getRequiredPoints();
                    if (housePoints < requiredPoints) {
                        String nextRewardText = "Next Reward: " + reward.getName();
                        String progressText = housePoints + "/" + requiredPoints;
                        ((TextView) view.findViewById(R.id.statistics_next_reward)).setText(nextRewardText);
                        ((TextView) view.findViewById(R.id.statistics_reward_progress)).setText(progressText);
                        ((ImageView) view.findViewById(R.id.statistics_reward_icon)).setImageResource(reward.getImageResource());
                        ProgressBar progressBar = view.findViewById(R.id.statistics_progress_bar);
                        progressBar.setMax(requiredPoints);
                        progressBar.setProgress(housePoints);
                        break;
                    }
                }
                if (housePoints >= requiredPoints) {
                    ((ProgressBar) view.findViewById(R.id.statistics_progress_bar)).setProgress(100);
                    ((ImageView) view.findViewById(R.id.statistics_reward_icon)).setImageResource(R.drawable.ic_check);
                    ((TextView) view.findViewById(R.id.statistics_next_reward)).setText("No rewards left to get!");
                    ((TextView) view.findViewById(R.id.statistics_reward_progress)).setText("");
                }

                progressBar.setVisibility(View.GONE);
                if (swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
