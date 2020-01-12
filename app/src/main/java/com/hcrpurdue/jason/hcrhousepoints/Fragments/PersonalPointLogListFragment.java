package com.hcrpurdue.jason.hcrhousepoints.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.ListFragment;

import com.hcrpurdue.jason.hcrhousepoints.ListAdapters.PointLogAdapter;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointLog;
import com.hcrpurdue.jason.hcrhousepoints.R;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;
import com.hcrpurdue.jason.hcrhousepoints.Utils.FirebaseListenerUtil;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.ListenerCallbackInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PersonalPointLogListFragment extends ListFragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener{

    protected List<PointLog> logs;
    protected PointLogAdapter adapter;
    protected CacheManager cacheManager;
    protected FirebaseListenerUtil flu;
    protected TextView emptyMessageTextView;
    protected ListView listView;
    protected Boolean isSearching = false;
    protected final String CALLBACK_KEY = "PERSONAL_POINT_LOGS";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity) Objects.requireNonNull(getActivity());
        Objects.requireNonNull(activity.getSupportActionBar()).setTitle("My Points");
        isSearching = false;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        cacheManager = CacheManager.getInstance(getContext());
        flu = FirebaseListenerUtil.getInstance(getContext());
        flu.getUserPointLogListener().addCallback(CALLBACK_KEY, new ListenerCallbackInterface() {
            @Override
            public void onUpdate() {
                handleUpdate();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        flu.removeCallbacks(CALLBACK_KEY);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_personal_point_list, container, false);
        listView = layout.findViewById(android.R.id.list);
        listView.addFooterView(new View(getContext()), null, true);
        emptyMessageTextView = layout.findViewById(android.R.id.empty);

        listView.setEmptyView(emptyMessageTextView);
        logs = new ArrayList<>();
        handleUpdate();
        createAdapter(logs);
        return layout;
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

        List<PointLog> filteredValues = new ArrayList<>(logs);
        for (PointLog log : logs) {
            if (!log.getPointDescription().contains(newText)) {
                filteredValues.remove(log);
            }
        }
        isSearching = true;
        createAdapter(filteredValues);

        return false;
    }

    public void resetSearch() {
        isSearching = false;
        createAdapter(logs);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return true;
    }

    protected void createAdapter(List<PointLog> logs){
        if(logs.size() == 0){
            emptyMessageTextView.setText("You haven't submitted any points");
        }
        adapter = new PointLogAdapter(logs,getContext(), R.id.nav_personal_point_log_list);
        setListAdapter(adapter);
    }


    public void handleUpdate() {
        logs = cacheManager.getPersonalPointLogs();
        if(!isSearching){
            createAdapter(logs);
        }
    }

}