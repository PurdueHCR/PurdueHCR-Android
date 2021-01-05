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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    cacheManager = CacheManager.getInstance(getContext());
    //fetches the events from the server and stores the json string in the cacheManager
    cacheManager.getEvents(getContext());
    System.out.println(cacheManager.getEventsString());


        SimpleDateFormat sdg = new SimpleDateFormat("EEE, MMM d h:mm a");
        Date date;
        try {
           date = sdg.parse("Wed, Jul 4 08:00 PM");
        } catch (ParseException e) {
            System.out.println("Date couldn't be formatted");
            e.printStackTrace();
            date = null;
        }

        events.add(new Event("Snack and chat","Free snacks and you can get to know RHPs and other students.",date,null,"Lobby",4,new String[]{"1N","2N"},"Honors Society"));
        events.add(new Event("Snack and chat","Free snacks and you can get to know RHPs and other students.",date,null,"Lobby",4,new String[]{"1N","2N"},"Honors Society"));
        events.add(new Event("Snack and chat","Free snacks and you can get to know RHPs and other students.",date,null,"Lobby",4,new String[]{"1N","2N"},"Honors Society"));

        // Hiding plus button if permissionlevel is equal to resident

        //shows the menu if the user has a non-resident permisson level
        setHasOptionsMenu(!cacheManager.getUser().getPermissionLevel().equals(UserPermissionLevel.RESIDENT));
        retrieveData();
        return baseView;
    }

    private void retrieveData() {
        final EventsAdapter recycler = new EventsAdapter(events);
        RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(getContext());
        //@TODO fetch event data here
        recycler.notifyDataSetChanged();
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
       System.out.println(APIHelper.getInstance(getContext()).getEvents());
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