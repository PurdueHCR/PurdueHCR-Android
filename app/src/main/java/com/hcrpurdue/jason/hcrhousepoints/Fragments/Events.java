package com.hcrpurdue.jason.hcrhousepoints.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hcrpurdue.jason.hcrhousepoints.Activities.createEvent;
import com.hcrpurdue.jason.hcrhousepoints.Models.Enums.UserPermissionLevel;
import com.hcrpurdue.jason.hcrhousepoints.Models.Event;
import com.hcrpurdue.jason.hcrhousepoints.Models.EventList;
import com.hcrpurdue.jason.hcrhousepoints.R;
import com.hcrpurdue.jason.hcrhousepoints.RecyclerAdapters.EventsAdapter;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;
import com.hcrpurdue.jason.hcrhousepoints.Utils.FirebaseListenerUtil;
import com.hcrpurdue.jason.hcrhousepoints.Utils.HttpNetworking.APIHelper;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;


public class Events extends Fragment {
    static private CacheManager cacheManager;
    private FirebaseListenerUtil flu;
    private RecyclerView recyclerView;
    private EventsAdapter eventsAdapter;
    private ProgressBar progressBar;
    private ArrayList<Event> events;

    public Events() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
       getEvents(getContext(),eventsAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View baseView = inflater.inflate(R.layout.fragment_events, container, false);
        setUtilities();
        recyclerView = baseView.findViewById(R.id.rvEvents);
        progressBar = baseView.findViewById(R.id.progressBar2);
    cacheManager = CacheManager.getInstance(getContext());
    //fetches the events from the server and stores the json string in the cacheManager

        //shows the plus button if the user has a non-resident permisson level
        setHasOptionsMenu(!cacheManager.getUser().getPermissionLevel().equals(UserPermissionLevel.RESIDENT));
        retrieveData();
        return baseView;
    }

    private void retrieveData() {


         eventsAdapter = new EventsAdapter(events);
        RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(getContext());
        //@TODO fetch event data here
        getEvents(getContext(),eventsAdapter);
        eventsAdapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(layoutmanager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(eventsAdapter);
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
       System.out.println(APIHelper.getInstance(getContext()).getEvents());
        Objects.requireNonNull(activity.getSupportActionBar()).setTitle("Events");
    }
    public void getEvents(Context context, EventsAdapter eventsAdapter){
        progressBar.setVisibility(View.VISIBLE);
        APIHelper.getInstance(context).getEvents().enqueue(new retrofit2.Callback<EventList>() {
            @Override
            public void onResponse(Call<EventList> call, Response<EventList> response) {

                //saves events
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    System.out.println("Getting events");
                    System.out.println(response.body().toString());
                    System.out.println(response.body().getEvents());
                    for(Event event: response.body().getEvents()) {
                        System.out.println("Points" + event.getPoint());
                        System.out.println(event.getPointTypeId());
                    }
                    setEvents(response.body().getEvents());

                    eventsAdapter.setEvents(response.body().getEvents());
                    eventsAdapter.notifyDataSetChanged();

                    System.out.println("Body" + response.body());

                    System.out.println(response.message());
                }

                else {
                    System.out.println(response.code() + ": " + response.message());
                    //cmi.onError(new Exception(response.code() + ": " + response.message()), context);
                }
            }

            @Override
            public void onFailure(Call<EventList> call, Throwable t) {
                System.out.println("ERROR getting events "+t.getMessage());

            }
        });
    }

    private void setUtilities() {
        cacheManager = CacheManager.getInstance(getContext());
        cacheManager.getCachedData();
        flu = FirebaseListenerUtil.getInstance(getContext());
    }

    protected int getMenuLayoutId() {
        return R.menu.eventmenu;
    }
    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }
}