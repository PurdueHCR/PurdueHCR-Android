package com.hcrpurdue.jason.hcrhousepoints.Fragments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.ListFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;


import com.hcrpurdue.jason.hcrhousepoints.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.hcrpurdue.jason.hcrhousepoints.Models.PointLog;
import com.hcrpurdue.jason.hcrhousepoints.ListAdapters.PointLogAdapter;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.CacheManagementInterface;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.ListenerCallbackInterface;

public class HousePointHistoryFragment extends ListFragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener, ListenerCallbackInterface {

    List<PointLog> allHouseLogs;
    private PointLogAdapter adapter;
    private CacheManager cacheManager;
    private TextView emptyMessageTextView;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cacheManager = CacheManager.getInstance(getContext());
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity) Objects.requireNonNull(getActivity());

        Objects.requireNonNull(activity.getSupportActionBar()).setTitle("House Point History");

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_house_submission_history_list, container, false);
        listView = layout.findViewById(android.R.id.list);
        emptyMessageTextView = layout.findViewById(android.R.id.empty);

        listView.setEmptyView(emptyMessageTextView);
        cacheManager.getAllHousePoints(new CacheManagementInterface() {
            @Override
            public void onGetAllHousePointsSuccess(List<PointLog> houseLogs) {
                allHouseLogs = houseLogs;
                createAdapter(allHouseLogs);
            }
        });

        return layout;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.house_submission_history_list, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search");

        super.onCreateOptionsMenu(menu, inflater);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText == null || newText.trim().isEmpty()) {
            resetSearch();
            return false;
        }

        List<PointLog> filteredValues = new ArrayList<PointLog>(allHouseLogs);
        for (PointLog log : allHouseLogs) {
            if (!(log.getResidentFirstName().toLowerCase().contains(newText.toLowerCase()) ||
                    log.getResidentLastName().toLowerCase().contains(newText.toLowerCase()) ||
                    log.getPointDescription().toLowerCase().contains(newText.toLowerCase()) ||
                    log.getPointType().getPointDescription().toLowerCase().contains(newText.toLowerCase()))) {
                filteredValues.remove(log);
            }
        }

        createAdapter(filteredValues);

        return false;
    }

    public void resetSearch() {
        createAdapter(allHouseLogs);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return true;
    }

    private void createAdapter(List<PointLog> logs){
        adapter = new PointLogAdapter(logs,getContext(), R.id.nav_point_history);
        setListAdapter(adapter);
        if (logs.size() == 0) {
            listView.setVisibility(View.GONE);
            emptyMessageTextView.setText("No Points to approve!");
            emptyMessageTextView.setVisibility(View.VISIBLE);
        }
        handleSystemPreferences();
    }

    private void handleSystemPreferences(){
        boolean isHouseEnabled = cacheManager.getCachedSystemPreferences().isHouseEnabled();

        if(!isHouseEnabled) {
            listView.setVisibility(View.GONE);
            emptyMessageTextView.setText(cacheManager.getCachedSystemPreferences().getHouseIsEnabledMsg());
            emptyMessageTextView.setVisibility(View.VISIBLE);

        }

        else {
            listView.setVisibility(View.VISIBLE);
            emptyMessageTextView.setVisibility(View.GONE);
        }
    }
}