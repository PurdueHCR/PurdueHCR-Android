package com.hcrpurdue.jason.hcrhousepoints.Fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointLog;
import com.hcrpurdue.jason.hcrhousepoints.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com.hcrpurdue.jason.hcrhousepoints.Models.House;
import com.hcrpurdue.jason.hcrhousepoints.Models.Reward;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;
import com.hcrpurdue.jason.hcrhousepoints.Utils.FirebaseListenerUtil;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.ListenerCallbackInterface;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.CacheManagementInterface;

public class HouseOverviewFragment extends Fragment implements ListenerCallbackInterface {
    static private CacheManager cacheManager;
    private Context context;
    private Resources resources;
    private String packageName;
    private ProgressBar progressBar;
    private Button viewMyPointsButton;
    private FirebaseListenerUtil flu;
    private final String PERSONAL_LOGS_CALLBACK_KEY = "POINT_LOG_DETAILS";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cacheManager = CacheManager.getInstance(getContext());
        flu = FirebaseListenerUtil.getInstance(context);
        resources = getResources();
        flu.getUserPointLogListener().addCallback(PERSONAL_LOGS_CALLBACK_KEY, new ListenerCallbackInterface() {
            @Override
            public void onUpdate() {
                refreshPointsButtonLabel();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        flu.getUserPointLogListener().removeCallback(PERSONAL_LOGS_CALLBACK_KEY);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_house_overview, container, false);
        BarChart chart = view.findViewById(R.id.statistics_point_chart);
        packageName = context.getPackageName();

        cacheManager.getCachedData();
        String house = cacheManager.getHouse();
        String floorText = house + " - " + cacheManager.getFloorName();
        int drawableID = resources.getIdentifier(cacheManager.getHouse().toLowerCase(), "drawable", packageName);
        ((ImageView) view.findViewById(R.id.statistics_house_icon)).setImageResource(drawableID);
        ((ImageView) view.findViewById(R.id.statistics_house_icon_small)).setImageResource(drawableID);
        ((TextView) view.findViewById(R.id.statistics_user_name)).setText(cacheManager.getName());
        ((TextView) view.findViewById(R.id.statistics_floor_name)).setText(floorText);
        viewMyPointsButton = view.findViewById(R.id.user_log_history_button);
        viewMyPointsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create destination fragment
                Fragment fragment = new PersonalPointLogListFragment();

                //Create Fragment manager
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, fragment, Integer.toString(R.id.nav_personal_point_log_list));
                fragmentTransaction.addToBackStack(Integer.toString(R.id.nav_profile));
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.commit();
            }
        });

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
        refreshPointsButtonLabel();
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
        Objects.requireNonNull(activity.getSupportActionBar()).setTitle("Competition Overview");
    }

    private void updatePointData(View view, BarChart chart, SwipeRefreshLayout swipeRefreshLayout) {
        TextView housePointsTextView = view.findViewById(R.id.statistics_user_points);
        cacheManager.getPointStatistics(new CacheManagementInterface() {
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
                    if (houseName.equals(cacheManager.getHouse())) {
                        housePoints = house.getTotalPoints();
                        housePointsTextView.setText(String.format(Locale.getDefault(),
                                "%d House Points | %d Individual Points", housePoints, userPoints));
                    }

                    BarEntry barEntry = new BarEntry(i++, house.getTotalPoints());
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

    /**
     * On personal Point log update, check if there are any new notifications
     */
    private void refreshPointsButtonLabel(){
        for(PointLog log: cacheManager.getPersonalPointLogs()){
            if(log.getResidentNotifications() > 0){
                viewMyPointsButton.setText(R.string.view_points_alert);
                return;
            }
        }
        viewMyPointsButton.setText(R.string.view_points);
    }
}
