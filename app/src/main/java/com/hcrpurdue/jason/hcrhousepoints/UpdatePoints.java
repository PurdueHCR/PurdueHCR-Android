package com.hcrpurdue.jason.hcrhousepoints;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Models.PointLog;
import Utils.HouseSubmissionHistoryAdapter;
import Utils.Singleton;
import Utils.SingletonInterface;

public class UpdatePoints extends ListFragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {

    List<PointLog> allHouseLogs;
    private HouseSubmissionHistoryAdapter adapter;
    private Singleton singleton;
    private ProgressBar progressBar;

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
        progressBar = activity.findViewById(R.id.navigationProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        Objects.requireNonNull(activity.getSupportActionBar()).setTitle("House Point History");

    }

    @Override
    public void onListItemClick(ListView listView, View v, int position, long id) {
//        String item = (String) listView.getAdapter().getItem(position);
//        if (getActivity() instanceof OnItem1SelectedListener) {
//            ((OnItem1SelectedListener) getActivity()).OnItem1SelectedListener(item);
//        }
//        getFragmentManager().popBackStack();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.house_submission_history_list_fragment, container, false);
        ListView listView = (ListView) layout.findViewById(android.R.id.list);
        TextView emptyTextView = (TextView) layout.findViewById(android.R.id.empty);

        listView.setEmptyView(emptyTextView);
        singleton.getAllHousePoints(new SingletonInterface() {
            @Override
            public void onGetAllHousePointsSuccess(List<PointLog> houseLogs) {
                allHouseLogs = houseLogs;
                createAdapter(allHouseLogs);
                progressBar.setVisibility(View.GONE);
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
            if (!(log.getResident().toLowerCase().contains(newText.toLowerCase()) ||
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

    public interface OnItem1SelectedListener {
        void OnItem1SelectedListener(String item);
    }

    private void createAdapter(List<PointLog> logs){
        adapter = new HouseSubmissionHistoryAdapter(logs,getContext(),progressBar);
        setListAdapter(adapter);
    }
}