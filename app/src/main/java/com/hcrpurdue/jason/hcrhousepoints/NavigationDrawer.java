package com.hcrpurdue.jason.hcrhousepoints;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import Utils.Singleton;

public class NavigationDrawer extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Singleton singleton;
    private FragmentManager fragmentManager;
    private Menu menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        singleton = Singleton.getInstance(getApplicationContext());
        singleton.getCachedData();
        try {
            int themeID = getResources().getIdentifier(singleton.getHouse().toLowerCase(), "style", this.getPackageName());
            setTheme(themeID);
        } catch (Exception e) {
            Toast.makeText(NavigationDrawer.this, "Error loading house theme", Toast.LENGTH_LONG).show();
            Log.e("NavigationDrawer", "Failed to load house color", e);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        try {
            int drawableID = getResources().getIdentifier(singleton.getHouse().toLowerCase(), "drawable", getPackageName());
            ((ImageView) headerView.findViewById(R.id.header_house_image)).setImageResource(drawableID);
        } catch (Exception e) {
            Toast.makeText(NavigationDrawer.this, "Failed to load house image", Toast.LENGTH_LONG).show();
            Log.e("SubmitPoints", "Error loading house image", e);
        }
        ((TextView) headerView.findViewById(R.id.header_resident_name)).setText(singleton.getName());
        String floorName = singleton.getHouse() + " - " + singleton.getFloorName();
        ((TextView) headerView.findViewById(R.id.header_floor_name)).setText(floorName);

        menu = navigationView.getMenu();

        try {
            if (singleton.getPermissionLevel() > 0) {
                menu.findItem(R.id.nav_approve).setVisible(true);
            }
        } catch (Exception e) {
            Toast.makeText(NavigationDrawer.this, "Error loading permission level", Toast.LENGTH_LONG).show();
            Log.e("NavigationDrawer", "Failed to load Permission Level", e);
        }

        fragmentManager = getSupportFragmentManager();

        try {
            fragmentManager.beginTransaction().replace(R.id.content_frame, SubmitPoints.class.newInstance()).commit();
        } catch (Exception e) {
            Toast.makeText(this, "Error loading SubmitPoints Frament", Toast.LENGTH_LONG).show();
            Log.e("NavigationDrawer", "Failed to load initial fragment", e);
        }


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        drawerLayout = findViewById(R.id.drawer_layout);

        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    MenuItem checkedItem = navigationView.getCheckedItem();
                    int currentItem = checkedItem == null ? 0 : checkedItem.getItemId();

                    menuItem.setChecked(true);
                    drawerLayout.closeDrawers();
                    int selectedItem = menuItem.getItemId();

                    Class fragmentClass = null;
                    switch (selectedItem) {
                        case R.id.nav_signout:
                            signOut();
                            break;
                        case R.id.nav_submit:
                            fragmentClass = SubmitPoints.class;
                            break;
                        case R.id.nav_approve:
                            fragmentClass = ApprovePoints.class;
                            break;
                        case R.id.nav_statistics:
                            fragmentClass = Statistics.class;
                            break;
                        case R.id.nav_scanner:
                            if (ContextCompat.checkSelfPermission(Objects.requireNonNull(this), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 10);
                            else
                                fragmentClass = QRScan.class;
                            break;
                        case R.id.nav_report_issue:
                            if (checkedItem != null)
                                checkedItem.setChecked(true);
                            Intent reportIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/hcr-points/home"));
                            reportIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(reportIntent);
                            break;
                        default:
                            fragmentClass = SubmitPoints.class;
                            break;
                    }

                    if (fragmentClass != null && currentItem != selectedItem) {
                        Fragment fragment = null;
                        try {
                            fragment = (Fragment) fragmentClass.newInstance();
                        } catch (Exception e) {
                            Toast.makeText(this, "Failed to load Fragment while changing views", Toast.LENGTH_LONG).show();
                            Log.e("NavigationDrawer", "Failed to load initial fragment", e);
                        }
                        assert fragment != null;
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(Integer.toString(currentItem)).commit();
                    }
                    return true;
                });
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

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        singleton.clearUserData();
        Intent intent = new Intent(this, Authentication.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == 10) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, QRScan.class.newInstance()).addToBackStack(null).commit();
                } catch (Exception e) {
                    Log.e("Navigation", "Failed to launch QR fragment", e);
                }
            } else {
                ((NavigationView) findViewById(R.id.nav_view)).getMenu().findItem(R.id.nav_scanner).setChecked(false);
                Toast.makeText(this, "Camera permissions denied, please allow camera permissions to scan QR codes", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        fragmentManager.executePendingTransactions();
        int count = fragmentManager.getBackStackEntryCount();
        if (count > 0) {
            FragmentManager.BackStackEntry backStackEntry = fragmentManager.getBackStackEntryAt(count - 1);
            String tag = backStackEntry.getName();
            if (tag != null) {
                int itemID = Integer.parseInt(tag);
                menu.findItem(itemID).setChecked(true);
            }
        }
        super.onBackPressed();
    }
}
