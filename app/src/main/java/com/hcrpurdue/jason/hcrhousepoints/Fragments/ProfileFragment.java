package com.hcrpurdue.jason.hcrhousepoints.Fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.hcrpurdue.jason.hcrhousepoints.ListAdapters.PointLogAdapter;
import com.hcrpurdue.jason.hcrhousepoints.Models.House;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointLog;
import com.hcrpurdue.jason.hcrhousepoints.Models.Reward;
import com.hcrpurdue.jason.hcrhousepoints.R;

import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;
import com.hcrpurdue.jason.hcrhousepoints.Utils.FirebaseListenerUtil;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.CacheManagementInterface;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.ListenerCallbackInterface;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ProfileFragment extends Fragment implements ListenerCallbackInterface {
    private Context context;
    private FirebaseListenerUtil flu;
    private CacheManager cacheManager;
    private Resources resources;
    private final String PERSONAL_LOGS_CALLBACK_KEY = "POINT_LOG_DETAILS";
    private String packageName;

    private BarChart houseChart;
    private TextView userPointTotalTextView;
    private TextView userHouseRankTextView;
    private TextView houseNameTextView;
    private TextView nextRewardNameTextView;
    private TextView nextRewardPPRTextView;
    private ListView recentSubmissionListView;
    private PointLogAdapter adapter;
    private ImageView houseImageView;
    private ImageView nextRewardImageView;
    private ProgressBar nextRewardProgressBar;
    private ScrollView scrollView;
    private Button viewMyPointsButton;

    private MenuItem notificationBarButton;

    private int userPoints;
    private int userRank;
    private List<Reward> allRewards = new ArrayList<>();
    private List<House> allHouses = new ArrayList<>();
    private boolean areViewsCreated = false;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        //Load data
        cacheManager = CacheManager.getInstance(getContext());
        flu = FirebaseListenerUtil.getInstance(context);
        resources = getResources();
        if(cacheManager.getPermissionLevel() == )
        flu.getUserPointLogListener().addCallback(PERSONAL_LOGS_CALLBACK_KEY, new ListenerCallbackInterface() {
            @Override
            public void onUpdate() {
                handleNotificationsUpdate();
            }
        });
        updateData();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        flu.getUserPointLogListener().removeCallback(PERSONAL_LOGS_CALLBACK_KEY);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        packageName = context.getPackageName();
        View baseView = inflater.inflate(R.layout.fragment_new_profile, container, false);

        connectViews(baseView);
        setValuesForViews();
        areViewsCreated = true;

        updateData();
        return baseView;
    }

    /**
     * Connect the class variables to their corresponding views on the screen
     *
     * @param view
     */
    private void connectViews(View view){
        houseChart = view.findViewById(R.id.statistics_point_chart);
        houseNameTextView = view.findViewById(R.id.house_name_text_view);
        recentSubmissionListView = view.findViewById(R.id.recent_submission_list_view);
        houseImageView = view.findViewById(R.id.profile_house_image_view);
        userPointTotalTextView = view.findViewById(R.id.your_points_text_view);
        userHouseRankTextView = view.findViewById(R.id.house_rank_text_view);
        nextRewardImageView = view.findViewById(R.id.next_reward_image_view);
        nextRewardNameTextView = view.findViewById(R.id.next_reward_text_view);
        nextRewardPPRTextView = view.findViewById(R.id.ppr_requirement_label);
        nextRewardProgressBar = view.findViewById(R.id.next_reward_progress_bar);
        scrollView = view.findViewById(R.id.profile_scroll_view);

        viewMyPointsButton = view.findViewById(R.id.more_button);
        viewMyPointsButton.setOnClickListener(view1 -> {
            //Create destination fragment
            Fragment fragment = new PersonalPointLogListFragment();

            //Create Fragment manager
            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment, Integer.toString(R.id.nav_new_profile));
            fragmentTransaction.addToBackStack(Integer.toString(R.id.nav_new_profile));
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
        });

        scrollView.smoothScrollTo(0,0);
    }

    /**
     * Set the values for the class views
     */
    private void setValuesForViews(){

        setupGraph();
        populateTopBar();
        populateRewardCard();
        populateGraphCard();
        createAdapter(cacheManager.getPersonalPointLogs());
        setNotificationBarButtonIcon();

        //TODO Add this swipe layout
//                if (swipeRefreshLayout != null)
//                    swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Initialize the graph of the house points
     */
    private void setupGraph(){
        houseChart.getAxisLeft().setAxisMinimum(0);
        houseChart.getAxisLeft().setEnabled(false);
        houseChart.getAxisRight().setEnabled(false);
        houseChart.getXAxis().setEnabled(false);
        houseChart.getDescription().setEnabled(false);
        houseChart.setScaleEnabled(false);
        houseChart.setTouchEnabled(false);
        houseChart.setDrawBorders(false);
        houseChart.setNoDataText("Loading House Points...");
        houseChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        houseChart.setAutoScaleMinMaxEnabled(true);
        houseChart.invalidate();
    }


    /**
     * SEt text on top Bar
     */
    private void populateTopBar(){
        int drawableID = resources.getIdentifier(cacheManager.getHouseName().toLowerCase(), "drawable", packageName);

        houseImageView.setImageResource(drawableID);
        houseNameTextView.setText(cacheManager.getHouseAndPermissionName());
        String userPointsText = "" + userPoints;
        String houseRankText = (userRank != -1)? "# "+userRank: "";
        userPointTotalTextView.setText(userPointsText);
        userHouseRankTextView.setText(houseRankText);
    }

    /**
     * refresh the data
     */
    private void updateData(){

        refreshRank();
        cacheManager.getPointStatistics(new CacheManagementInterface() {
            @Override
            public void onGetPointStatisticsSuccess(List<House> houses, int points, List<Reward> rewards) {

                userPoints = points;
                allRewards = rewards;
                allHouses = houses;

                Collections.sort(allRewards);

                Collections.sort(allHouses);
                Collections.swap(allHouses, 0, 2);
                Collections.swap(allHouses, 0, 3);
                if(areViewsCreated){
                    setValuesForViews();
                }
            }
        });
    }


    private void refreshRank(){

        cacheManager.getUserRank(context, new CacheManagementInterface() {
            @Override
            public void onError(Exception e, Context context) {
                System.out.println(e.getMessage());
                userRank = -1;

                if(areViewsCreated){
                    setValuesForViews();
                }
            }

            @Override
            public void onGetRank(Integer rank) {
                userRank = rank;

                if(areViewsCreated){
                    setValuesForViews();
                }
            }
        });


    }

    private void populateRewardCard(){
        float requiredPPR = 0F;
        float previousPPR = 0F;
        float pointsPerResident = cacheManager.getUserHouse().getPointsPerResident();
        int numberOfResidents = cacheManager.getUserHouse().getNumResidents();


        for (Reward reward : allRewards) {
            requiredPPR = reward.getRequiredPointsPerResident();
            if (pointsPerResident < requiredPPR) {
                nextRewardPPRTextView.setVisibility(View.VISIBLE);
                nextRewardPPRTextView.setText(String.format(Locale.getDefault(), "%.0f PPR", reward.getRequiredPointsPerResident()));
                nextRewardImageView.setImageResource(reward.getImageResource());
                nextRewardNameTextView.setText(reward.getName());
                nextRewardProgressBar.setMax((int)(requiredPPR * numberOfResidents));
                nextRewardProgressBar.setProgress((int)((pointsPerResident - previousPPR) * numberOfResidents));
                break;
            }
        }
        if (pointsPerResident >= requiredPPR) {
            nextRewardProgressBar.setMax(100);
            nextRewardProgressBar.setProgress(100);
            nextRewardImageView.setImageResource(R.drawable.ic_check);
            nextRewardNameTextView.setText("No rewards left to get!");
            nextRewardPPRTextView.setVisibility(View.INVISIBLE);
        }
    }

    private void populateGraphCard(){
        List<IBarDataSet> dataSetList = new ArrayList<>();
        // So that things are in podium order (4, 2, 1, 3, 5)


        float i = 0f;
        for (House house : allHouses) {
            String houseName = house.getName();
            BarEntry barEntry = new BarEntry(i++, house.getPointsPerResident());
            ArrayList<BarEntry> barEntryList = new ArrayList<>();
            barEntryList.add(barEntry);
            BarDataSet dataSet = new BarDataSet(barEntryList, houseName);
            dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> {
                DecimalFormat formater = new DecimalFormat("###.##");
                return formater.format(value);
            });
            int colorID = ContextCompat.getColor(context, resources.getIdentifier(houseName.toLowerCase(), "color", packageName));
            dataSet.setColor(colorID);
            dataSet.setValueTextSize(12);
            dataSetList.add(dataSet);
        }
        BarData barData = new BarData(dataSetList);

        houseChart.setData(barData);
        houseChart.notifyDataSetChanged();
        houseChart.invalidate();
    }

    private void createAdapter(List<PointLog> logs){
        if(logs == null || logs.size() == 0){
            //emptyMessageTextView.setText("You haven't submitted any points");
            return;
        }
        adapter = new PointLogAdapter(logs.subList(0,3),getContext(), R.id.nav_new_profile);
        recentSubmissionListView.setAdapter(adapter);

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        Objects.requireNonNull(activity.getSupportActionBar()).setTitle(cacheManager.getName());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the action bar
        inflater.inflate(R.menu.profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        notificationBarButton = menu.findItem(R.id.notifications_action_bar_button);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getGroupId()){
            case R.id.notifications_action_bar_button:
                    //Transition to a notifications tab
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method will be called when the userPointLogListener gets activated by Firebase. This will
     *  update the notifications Icon to show if there are new messages.
     */
    private void handleNotificationsUpdate(){
        //TODO refresh the alert icon saying there are alerts
        setNotificationBarButtonIcon();
        //createAdapter(cacheManager.getPersonalPointLogs());
    }

    /**
     * Check if there are any notifications on any of the Point Logs belonging to the resident
     *  and if so, set the notification icon to ic_notifications_active, else set to ic_notifications_none
     */
    private void setNotificationBarButtonIcon(){
        for(PointLog log: cacheManager.getPersonalPointLogs()){
            if(log.getResidentNotifications() > 0){
                notificationBarButton.setIcon(R.drawable.ic_notifications_active);
                return;
            }
        }
        notificationBarButton.setIcon(R.drawable.ic_notifications_none);
    }
}
