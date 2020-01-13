package com.hcrpurdue.jason.hcrhousepoints.Fragments;

import android.os.Bundle;
import android.util.Log;
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

import com.hcrpurdue.jason.hcrhousepoints.ListAdapters.PointTypeListAdapter;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointType;
import com.hcrpurdue.jason.hcrhousepoints.Models.SystemPreferences;
import com.hcrpurdue.jason.hcrhousepoints.R;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;
import com.hcrpurdue.jason.hcrhousepoints.Utils.FirebaseListenerUtil;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.CacheManagementInterface;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.ListenerCallbackInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PointTypeListFragment  extends ListFragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener, ListenerCallbackInterface {

    List<PointType> enabledTypes;
    private PointTypeListAdapter adapter;
    private CacheManager cacheManager;
    private TextView emptyMessageTextView;
    private ListView listView;
    private FirebaseListenerUtil flu;
    private final String POINT_TYPE_CALLBACK_KEY = "POINT_TYPE_CALLBACK";

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
        Objects.requireNonNull(activity.getSupportActionBar()).setTitle("Submit Points");
        flu = FirebaseListenerUtil.getInstance(getContext());
        flu.getSystemPreferenceListener().addCallback(POINT_TYPE_CALLBACK_KEY, new ListenerCallbackInterface() {
            @Override
            public void onUpdate() {
                handleSystemPreferencesUpdate();
            }
        });
        flu.getPointTypeListener().addCallback(POINT_TYPE_CALLBACK_KEY, new ListenerCallbackInterface() {
            @Override
            public void onUpdate() {
                handlePointTypeUpdate();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        handlePointTypeUpdate();
        handleSystemPreferencesUpdate();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        flu.removeCallbacks(POINT_TYPE_CALLBACK_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_point_type_list, container, false);
        listView = layout.findViewById(android.R.id.list);
        emptyMessageTextView = layout.findViewById(android.R.id.empty);

        listView.setEmptyView(emptyMessageTextView);
        storeEnabledTypes(cacheManager.getPointTypeList());
        handleSystemPreferencesUpdate();
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

        List<PointType> filteredValues = new ArrayList<>(enabledTypes);
        for (PointType type : enabledTypes) {
            if (!type.getPointDescription().toLowerCase().contains(newText.toLowerCase())
                    && !type.getName().toLowerCase().contains(newText.toLowerCase())) {
                filteredValues.remove(type);
            }
        }

        createAdapter(filteredValues);

        return false;
    }

    public void resetSearch() {
        createAdapter(enabledTypes);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return true;
    }

    private void createAdapter(List<PointType> types){
        //Hide the list if house is disabled
        adapter = new PointTypeListAdapter(types,getContext());
        setListAdapter(adapter);
        if (types.size() == 0) {
            listView.setVisibility(View.GONE);
            emptyMessageTextView.setText("No Points with that name");
            emptyMessageTextView.setVisibility(View.VISIBLE);
        }
        handleSystemPreferencesUpdate();
    }

    private void handlePointTypeUpdate() {
        storeEnabledTypes(cacheManager.getPointTypeList());
        createAdapter(enabledTypes);
        if (enabledTypes.size() == 0) {
            listView.setVisibility(View.GONE);
            emptyMessageTextView.setText(R.string.no_point_types_enabled);
            emptyMessageTextView.setVisibility(View.VISIBLE);
        }
        else {
            listView.setVisibility(View.VISIBLE);
            emptyMessageTextView.setVisibility(View.GONE);
        }
    }

    private void storeEnabledTypes(List<PointType> types){
        enabledTypes = new ArrayList<>();
        for(PointType type: types) {
            if (type.getResidentsCanSubmit() && type.isEnabled()) {
                enabledTypes.add(type);
            }
        }
    }

    private void handleSystemPreferencesUpdate(){
        SystemPreferences systemPreferences = cacheManager.getSystemPreferences();
        if(!systemPreferences.isHouseEnabled()) {
            listView.setVisibility(View.GONE);
            emptyMessageTextView.setVisibility(View.VISIBLE);
            emptyMessageTextView.setText(systemPreferences.getHouseIsEnabledMsg());
        }
        else {
            listView.setVisibility(View.VISIBLE);
            emptyMessageTextView.setVisibility(View.GONE);
        }
    }
}