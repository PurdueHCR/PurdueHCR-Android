package com.hcrpurdue.jason.hcrhousepoints.Fragments;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.hcrpurdue.jason.hcrhousepoints.Models.Enums.UserPermissionLevel;
import com.hcrpurdue.jason.hcrhousepoints.R;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;
import com.hcrpurdue.jason.hcrhousepoints.Utils.FirebaseListenerUtil;
import com.hcrpurdue.jason.hcrhousepoints.Utils.HttpNetworking.APIHelper;

import java.util.Objects;


public class Events extends Fragment {
    static private CacheManager cacheManager;
    private FirebaseListenerUtil flu;
    public Events() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View baseView = inflater.inflate(R.layout.fragment_events, container, false);
setUtilities();
       System.out.println(APIHelper.getInstance(getContext()).getEvents());

        // Hiding plus button if permissionlevel is equal to resident
        if (cacheManager.getUser().getPermissionLevel().equals(UserPermissionLevel.RESIDENT)) {
            //hides the menu
            setHasOptionsMenu(false);

        } else {
            //shows the menu
            setHasOptionsMenu(true);
        }
        return baseView;
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
                Toast.makeText(getContext(),"Opening create event activity",Toast.LENGTH_LONG).show();

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
    private void setUtilities(){
        cacheManager = CacheManager.getInstance(getContext());
        cacheManager.getCachedData();
        flu = FirebaseListenerUtil.getInstance(getContext());
    }
    protected int getMenuLayoutId(){
        return R.menu.eventmenu;
    }
}