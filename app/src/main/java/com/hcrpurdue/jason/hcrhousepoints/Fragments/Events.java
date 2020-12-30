package com.hcrpurdue.jason.hcrhousepoints.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcrpurdue.jason.hcrhousepoints.Activities.createEvent;
import com.hcrpurdue.jason.hcrhousepoints.Models.Enums.UserPermissionLevel;
import com.hcrpurdue.jason.hcrhousepoints.Models.Event;
import com.hcrpurdue.jason.hcrhousepoints.R;
import com.hcrpurdue.jason.hcrhousepoints.RecyclerAdapters.EventsAdapter;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;
import com.hcrpurdue.jason.hcrhousepoints.Utils.FirebaseListenerUtil;
import com.hcrpurdue.jason.hcrhousepoints.Utils.HttpNetworking.APIHelper;

import java.util.ArrayList;
import java.util.Objects;


public class Events extends Fragment {
    static private CacheManager cacheManager;
    private FirebaseListenerUtil flu;
    private RecyclerView recyclerView;
    private final ArrayList<Event> events = new ArrayList<>();

    public Events() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View baseView = inflater.inflate(R.layout.fragment_events, container, false);
        setUtilities();
        recyclerView = baseView.findViewById(R.id.rvEvents);
        System.out.println(APIHelper.getInstance(getContext()).getEvents());

        // Hiding plus button if permissionlevel is equal to resident
        //hides the menu
        //shows the menu
        setHasOptionsMenu(!cacheManager.getUser().getPermissionLevel().equals(UserPermissionLevel.RESIDENT));
        retrieveData();
        return baseView;
    }

    private void retrieveData() {
        final EventsAdapter recycler = new EventsAdapter(events);
        RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutmanager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recycler);
    }

    //  @Override
    //  public void onCreate(Bundle savedInstanceState) {
    //      super.onCreate(savedInstanceState);
    //      setHasOptionsMenu(true);
    //  }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the action bar
        inflater.inflate(getMenuLayoutId(), menu);
        super.onCreateOptionsMenu(menu, inflater);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                //code should be added here for opening create activity page

                startActivity(new Intent(getContext(), createEvent.class));
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        Objects.requireNonNull(activity.getSupportActionBar()).setTitle("Events");
    }

    private void setUtilities() {
        cacheManager = CacheManager.getInstance(getContext());
        cacheManager.getCachedData();
        flu = FirebaseListenerUtil.getInstance(getContext());
    }

    protected int getMenuLayoutId() {
        return R.menu.eventmenu;
    }
}