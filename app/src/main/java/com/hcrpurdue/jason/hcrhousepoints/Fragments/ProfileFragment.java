package com.hcrpurdue.jason.hcrhousepoints.Fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.appcompat.widget.Toolbar;

import com.hcrpurdue.jason.hcrhousepoints.R;

import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;
import com.hcrpurdue.jason.hcrhousepoints.Utils.FirebaseListenerUtil;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.ListenerCallbackInterface;

import java.util.Objects;

public class ProfileFragment extends Fragment implements ListenerCallbackInterface {
    private Context context;
    private FirebaseListenerUtil flu;
    private CacheManager cacheManager;
    private final String PERSONAL_LOGS_CALLBACK_KEY = "POINT_LOG_DETAILS";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cacheManager = CacheManager.getInstance(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        flu.getUserPointLogListener().removeCallback(PERSONAL_LOGS_CALLBACK_KEY);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_new_profile, container, false);
       // Toolbar toolbar = view.findViewById(R.id.profile_toolbar);
//        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        //((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
//        actionBar.setTitle(cacheManager.getName());
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        Objects.requireNonNull(activity.getSupportActionBar()).setTitle("Profile: "+cacheManager.getName());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_settings_menu, menu);
        MenuItem settingsButton = menu.findItem(R.id.action_settings_button);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
