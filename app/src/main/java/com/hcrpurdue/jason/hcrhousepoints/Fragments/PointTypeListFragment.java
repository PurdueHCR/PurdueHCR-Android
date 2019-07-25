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
import com.hcrpurdue.jason.hcrhousepoints.Utils.Singleton;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.ListenerCallbackInterface;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.SingletonInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PointTypeListFragment  extends ListFragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener, ListenerCallbackInterface {

    List<PointType> enabledTypes;
    private PointTypeListAdapter adapter;
    private Singleton singleton;
    private TextView emptyMessageTextView;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        singleton = Singleton.getInstance(getContext());
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity) Objects.requireNonNull(getActivity());
        Objects.requireNonNull(activity.getSupportActionBar()).setTitle("Submit Points");

    }

    @Override
    public void onStart() {
        super.onStart();
        getUpdatedPointTypes();
    }

    @Override
    public void onListItemClick(ListView listView, View v, int position, long id) {

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_point_type_list, container, false);
        listView = layout.findViewById(android.R.id.list);
        listView.addFooterView(new View(getContext()), null, true);
        emptyMessageTextView = layout.findViewById(android.R.id.empty);

        listView.setEmptyView(emptyMessageTextView);
        enabledTypes = singleton.getCachedPointTypes();
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
            if (!type.getPointDescription().contains(newText)) {
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
        adapter = new PointTypeListAdapter(types,getContext());
        setListAdapter(adapter);
    }


    private void getUpdatedPointTypes() {
        try {
            singleton.getUpdatedPointTypes(new SingletonInterface() {
                public void onPointTypeComplete(List<PointType> data) {
                    storeEnabledTypes(data);
                    singleton.getSystemPreferences(new SingletonInterface() {
                        @Override
                        public void onGetSystemPreferencesSuccess(SystemPreferences systemPreferences) {
                            if(!systemPreferences.isHouseEnabled()) {
                                listView.setVisibility(View.GONE);
                                emptyMessageTextView.setText(systemPreferences.getHouseIsEnabledMsg());
                                emptyMessageTextView.setVisibility(View.VISIBLE);
                            }

                            else if (enabledTypes.size() == 0) {
                                listView.setVisibility(View.GONE);
                                emptyMessageTextView.setText(R.string.no_point_types_enabled);
                                emptyMessageTextView.setVisibility(View.VISIBLE);
                            }
                            else {
                                listView.setVisibility(View.VISIBLE);
                                emptyMessageTextView.setVisibility(View.GONE);
                            }
                            createAdapter(enabledTypes);
                        }
                    });
                }
            });

        } catch (Exception e) {
            Toast.makeText(getContext(), "Failed to load point types", Toast.LENGTH_LONG).show();
            Log.e("PointSubmissionFragment", "Error loading point types", e);
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
}