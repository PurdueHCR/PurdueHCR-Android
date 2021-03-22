/**
 * This class provides the base for any profile fragment. It contains member variables for
 * the cache manager, context, listener utility, scroll view, and notification button.
 *
 * The fragment layout xml for a subclass needs to have one scroll view with the id
 * profile_scroll_view that contains the views in cards in the scroll view.
 *
 * getFragmentLayoutId() must return the R.layout name for the .xml file where the view is made
 *
 */

package com.hcrpurdue.jason.hcrhousepoints.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.hcrpurdue.jason.hcrhousepoints.Models.Enums.UserPermissionLevel;
import com.hcrpurdue.jason.hcrhousepoints.R;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;
import com.hcrpurdue.jason.hcrhousepoints.Utils.FirebaseListenerUtil;
import com.hcrpurdue.jason.hcrhousepoints.Views.NotificationsButton;

import java.util.Objects;

public abstract class BaseProfileFragment extends Fragment {
    protected Context context;
    protected FirebaseListenerUtil flu;
    protected CacheManager cacheManager;
    protected final String PROFILE_FRAGMENT = "PROFILE_FRAGMENT";

    protected ScrollView scrollView;
    protected NotificationsButton notificationsButton;

    /**
     * Default override for onAttach. This sets up the context, cachemanager, and listeners.
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        //Load data
        cacheManager = CacheManager.getInstance(getContext());
        flu = FirebaseListenerUtil.getInstance(context);
        createListeners();
    }

    /**
     * Tells the app to create a menu, which adds the notification icon
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Because we have to remove callbacks from listeners when we are done, we have this method.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        flu.removeCallbacks(PROFILE_FRAGMENT);
    }


    /**
     * When the view is created, this will setup the scroll view then the cards
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View baseView = inflater.inflate(getFragmentLayoutId(), container, false);

        scrollView = baseView.findViewById(R.id.profile_scroll_view);
        scrollView.smoothScrollTo(0,0);
        //if(cacheManager.getPermissionLevel() != UserPermissionLevel.FHP && cacheManager.getPermissionLevel() != UserPermissionLevel.PROFESSIONAL_STAFF) {
            setupCards(baseView);
        //}

        return baseView;
    }

    /**
     * Sets the title of the page to be the user's name
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        Objects.requireNonNull(activity.getSupportActionBar()).setTitle(cacheManager.getName());
    }

    /**
     * Creates the notificaion button
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the action bar
        inflater.inflate(getMenuLayoutId(), menu);
        super.onCreateOptionsMenu(menu, inflater);
        setupNotificationsButton(menu);
    }

    /**
     * Overridable method that allows subclasses to use their own menu
     * @param menu
     */
    protected void setupNotificationsButton(Menu menu){
        notificationsButton = new NotificationsButton(context,menu);
    }

    /**
     * If a different menu is used, then this method must be overridden to provide the name for
     * the .xml file
     * @return
     */
    protected int getMenuLayoutId(){
        return R.menu.profile_menu;
    }

    /**
     * Returns the R.layout id which is the xml file name where the view is defined
     * @return integer for the id. IE R.layout.fragment_resident_profile
     */
    abstract protected int getFragmentLayoutId();

    /**
     * Create all of the views that this class will use. Recommendation is to look at
     * Resident Profile View for how to do this.
     * @param view
     */
    abstract protected void setupCards(View view);

    /**
     * Setup the listeners that this profile page will need to use.
     */
    abstract protected void createListeners();

}
