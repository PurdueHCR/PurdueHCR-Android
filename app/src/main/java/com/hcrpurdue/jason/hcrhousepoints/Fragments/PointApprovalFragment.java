package com.hcrpurdue.jason.hcrhousepoints.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
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
import android.widget.SearchView;
import android.widget.TextView;

import com.hcrpurdue.jason.hcrhousepoints.ListAdapters.PointLogAdapter;
import com.hcrpurdue.jason.hcrhousepoints.Models.SystemPreferences;
import com.hcrpurdue.jason.hcrhousepoints.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.hcrpurdue.jason.hcrhousepoints.Models.PointLog;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;
import com.hcrpurdue.jason.hcrhousepoints.Utils.FirebaseListenerUtil;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.ListenerCallbackInterface;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.CacheManagementInterface;

public class PointApprovalFragment extends ListFragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener, ListenerCallbackInterface{
    static private CacheManager cacheManager;
    private Context context;
    private List<PointLog> pointLogList = new ArrayList<>();
    private TextView emptyMessageTextView;
    private ListView listView;
    private FirebaseListenerUtil flu;
    private final String POINT_TYPE_CALLBACK_KEY = "POINT_TYPE_CALLBACK";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        cacheManager = CacheManager.getInstance(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_point_approval, container, false);
        listView = layout.findViewById(android.R.id.list);
        listView.addFooterView(new View(getContext()), null, true);
        emptyMessageTextView = layout.findViewById(android.R.id.empty);

        listView.setEmptyView(emptyMessageTextView);
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity) Objects.requireNonNull(getActivity());
        flu = FirebaseListenerUtil.getInstance(getContext());
        flu.getSystemPreferenceListener().addCallback(POINT_TYPE_CALLBACK_KEY, new ListenerCallbackInterface() {
            @Override
            public void onUpdate() {
                handleSystemPreferencesUpdate();
            }
        });

        getUnconfirmedPoints();
        handleSystemPreferencesUpdate();
        Objects.requireNonNull(activity.getSupportActionBar()).setTitle("Approve Points");

    }

    private void handleSystemPreferencesUpdate(){
        SystemPreferences systemPreferences = cacheManager.getSystemPreferences();
        if(!systemPreferences.isHouseEnabled()) {
            listView.setVisibility(View.GONE);
            emptyMessageTextView.setText(systemPreferences.getHouseIsEnabledMsg());
            emptyMessageTextView.setVisibility(View.VISIBLE);
        }
        else if (pointLogList.size() == 0) {
            listView.setVisibility(View.GONE);
            emptyMessageTextView.setText("No Points to approve!");
            emptyMessageTextView.setVisibility(View.VISIBLE);
        }
        else {
            listView.setVisibility(View.VISIBLE);
            emptyMessageTextView.setVisibility(View.GONE);
        }
    }

    private void getUnconfirmedPoints() {
        cacheManager.getUnconfirmedPoints(new CacheManagementInterface() {
            @Override
            public void onUnconfirmedPointsSuccess(ArrayList<PointLog> logs) {
                pointLogList = logs;
                createAdapter(pointLogList);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.point_type_list_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search");

        super.onCreateOptionsMenu(menu, inflater);

        super.onCreateOptionsMenu(menu, inflater);
    }


    //TODO: Check
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_show_pt_history) {
            startActivity(new Intent(PointApprovalFragment.this.getActivity(), HousePointHistoryFragment.class));
            return true;
        }

        return false;
    }


    @Override
    public boolean onMenuItemActionExpand(MenuItem menuItem) {
        return false;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText == null || newText.trim().isEmpty()) {
            resetSearch();
            return false;
        }

        List<PointLog> filteredValues = new ArrayList<PointLog>(pointLogList);
        for (PointLog log : pointLogList) {
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
        createAdapter(pointLogList);
    }

    public void createAdapter(List<PointLog> logs){
        Collections.sort(logs);
        PointLogAdapter adapter = new PointLogAdapter(logs,context, R.id.nav_approve_point);
        listView.setAdapter(adapter);
        if (logs.size() == 0) {
            listView.setVisibility(View.GONE);
            emptyMessageTextView.setText("No unhandled point that has a name that matches.");
            emptyMessageTextView.setVisibility(View.VISIBLE);
        }
        handleSystemPreferencesUpdate();
    }
}

