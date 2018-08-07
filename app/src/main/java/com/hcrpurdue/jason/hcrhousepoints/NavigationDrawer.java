package com.hcrpurdue.jason.hcrhousepoints;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import Utils.Singleton;

public class NavigationDrawer extends AppCompatActivity {
    private DrawerLayout drawerLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            String houseName = getIntent().getStringExtra("HouseName");
            int themeID = getResources().getIdentifier(houseName.toLowerCase(), "style", this.getPackageName());
            setTheme(themeID);
        } catch (Exception e) {
            Toast.makeText(this, "Error loading house color, please screenshot the Logs page and send it to your RHP", Toast.LENGTH_LONG).show();
            Log.e("NavigationDrawer", "Failed to load house color", e);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    // set item as selected to persist highlight
                    menuItem.setChecked(true);
                    // close drawer when item is tapped
                    drawerLayout.closeDrawers();

                    switch(menuItem.getItemId()){
                        case R.id.nav_signout:
                            signOut();
                            break;
                    }

                    return true;
                });
        Fragment fragment = null;
        try {
            fragment = SubmitPoints.class.newInstance();
        } catch (Exception e) {
            Toast.makeText(this, "An error occured, please screenshot the Logs screen and send it to your RHP", Toast.LENGTH_LONG).show();
            Log.e("NavigationDrawer", "Failed to load initial fragment", e);
        }
        assert fragment != null;
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();
        Singleton.getInstance().clearUserData();
        Intent intent = new Intent(this, Authentication.class);
        startActivity(intent);
        finish();
    }
}
