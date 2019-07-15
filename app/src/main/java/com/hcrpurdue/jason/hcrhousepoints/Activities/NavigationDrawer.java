package com.hcrpurdue.jason.hcrhousepoints.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.ListFragment;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.hcrpurdue.jason.hcrhousepoints.Fragments.HouseOverviewFragment;
import com.hcrpurdue.jason.hcrhousepoints.Fragments.HousePointHistoryFragment;
import com.hcrpurdue.jason.hcrhousepoints.Fragments.PointApprovalFragment;
import com.hcrpurdue.jason.hcrhousepoints.Fragments.PointSubmissionFragment;
import com.hcrpurdue.jason.hcrhousepoints.Fragments.PointTypeListFragment;
import com.hcrpurdue.jason.hcrhousepoints.Fragments.QRCodeListFragment;
import com.hcrpurdue.jason.hcrhousepoints.Fragments.QRCreationFragment;
import com.hcrpurdue.jason.hcrhousepoints.Fragments.QRScannerFragment;

import java.util.Objects;

import com.hcrpurdue.jason.hcrhousepoints.Fragments.SubmitPointsFragment;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointType;
import com.hcrpurdue.jason.hcrhousepoints.R;
import com.hcrpurdue.jason.hcrhousepoints.Utils.Singleton;

public class NavigationDrawer extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    static private Singleton singleton;
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
            Log.e("PointSubmissionFragment", "Error loading house image", e);
        }
        ((TextView) headerView.findViewById(R.id.header_resident_name)).setText(singleton.getName());
        String floorName = singleton.getHouse() + " - " + singleton.getFloorName();
        ((TextView) headerView.findViewById(R.id.header_floor_name)).setText(floorName);
        menu = navigationView.getMenu();


        //Use the permission levels to set the appropriate navigation menu options
        try {
            if (singleton.getPermissionLevel() > 0) {
                menu.findItem(R.id.nav_approve).setVisible(true);
                menu.findItem(R.id.point_history).setVisible(true);
                menu.findItem(R.id.nav_qr_code_list).setVisible(true);
            }
        } catch (Exception e) {
            Toast.makeText(NavigationDrawer.this, "Error loading permission level", Toast.LENGTH_LONG).show();
            Log.e("NavigationDrawer", "Failed to load Permission Level", e);
        }



        fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getFragments().isEmpty()) {
            try {
                fragmentManager.beginTransaction().replace(R.id.content_frame, PointTypeListFragment.class.newInstance(), Integer.toString(R.id.nav_submit)).commit();
            } catch (Exception e) {
                Toast.makeText(this, "Error loading PointSubmissionFragment Frament", Toast.LENGTH_LONG).show();
                Log.e("NavigationDrawer", "Failed to load initial fragment", e);
            }
        }


        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("PointSubmitted") && extras.getBoolean("PointSubmitted"))
            animateSuccess();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        drawerLayout = findViewById(R.id.drawer_layout);


//        if (singleton.showDialog()) {
////            new AlertDialog.Builder(this)
////                    .setTitle("Development Committee")
////                    .setMessage("Are you interested in helping development for the Purdue HCR app? If so, click \"I'm In\" below to join the Discord where we'll be coordinating everything!")
////                    .setPositiveButton("I'm in!", (dialog, whichButton) -> {
////                        Intent reportIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://discordapp.com/invite/jptXrYG"));
////                        reportIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                        startActivity(reportIntent);
////                    })
////                    .setNegativeButton("No thanks", null).show();
////        }


        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    MenuItem checkedItem = navigationView.getCheckedItem();
                    int currentItem = checkedItem == null ? 0 : checkedItem.getItemId();

                    drawerLayout.closeDrawers();
                    int selectedItem = menuItem.getItemId();

                    Class fragmentClass = null;
                    switch (selectedItem) {
                        case R.id.nav_signout:
                            new AlertDialog.Builder(this)
                                    .setTitle("Sign Out")
                                    .setMessage("Are you sure you want to sign out?")
                                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> signOut())
                                    .setNegativeButton(android.R.string.no, null).show();
                            break;
                        case R.id.nav_submit:
                            fragmentClass = PointTypeListFragment.class;
                            break;
                        case R.id.nav_approve:
                            fragmentClass = PointApprovalFragment.class;
                            break;
                        case R.id.nav_statistics:
                            fragmentClass = HouseOverviewFragment.class;
                            break;
                        case R.id.nav_scanner:
                            if (ContextCompat.checkSelfPermission(Objects.requireNonNull(this), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 10);
                            else
                                fragmentClass = QRScannerFragment.class;
                            break;
                        case R.id.nav_qr_code_list:
                            fragmentClass = QRCodeListFragment.class;
                            break;
                        case R.id.generateQRCode:
                            fragmentClass = QRCreationFragment.class;
                            break;
                        case R.id.nav_report_issue:
                            Intent reportIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/hcr-points/home"));
                            reportIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(reportIntent);
                            break;
                        case R.id.point_history:
                            fragmentClass = HousePointHistoryFragment.class;
                            break;
                        case R.id.nav_submit_point:
                            fragmentClass = SubmitPointsFragment.class;
                            break;
                        default:
                            fragmentClass = HouseOverviewFragment.class;
                            break;
                    }
                    if(fragmentClass == HousePointHistoryFragment.class) {
                        ListFragment listFragment = (ListFragment) fragmentManager.findFragmentByTag(Integer.toString(selectedItem));
                        if (listFragment == null) {
                            try {
                                listFragment = (ListFragment) fragmentClass.newInstance();
                            } catch (Exception e) {
                                Toast.makeText(this, "Failed to load Fragment while changing views", Toast.LENGTH_LONG).show();
                                Log.e("NavigationDrawer", "Failed to load fragment on menu select", e);
                            }
                        }

                        Objects.requireNonNull(listFragment);
                        fragmentManager.beginTransaction().replace(R.id.content_frame, listFragment, Integer.toString(selectedItem)).addToBackStack(Integer.toString(currentItem)).commit();
                        return true;
                    }
                    else if (fragmentClass != null && currentItem != selectedItem ) {
                        Fragment fragment = fragmentManager.findFragmentByTag(Integer.toString(selectedItem));
                        if (fragment == null) {
                            try {
                                fragment = (Fragment) fragmentClass.newInstance();
                            } catch (Exception e) {
                                Toast.makeText(this, "Failed to load Fragment while changing views", Toast.LENGTH_LONG).show();
                                Log.e("NavigationDrawer", "Failed to load fragment on menu select", e);
                            }
                        }

                        Objects.requireNonNull(fragment);
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, Integer.toString(selectedItem)).addToBackStack(Integer.toString(currentItem)).commit();
                        return true;
                    }
                    else {
                        return false;
                    }
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
        findViewById(R.id.navigationProgressBar).setVisibility(View.VISIBLE);
        AsyncTask.execute(() -> singleton.clearUserData());
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == 10) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, QRScannerFragment.class.newInstance(), Integer.toString(R.id.nav_scanner)).addToBackStack(null).commit();
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

    // This exists here and not in the PointSubmissionFragment fragment to show properly when you scan QR codes.
    public void animateSuccess() {
        LinearLayout successLayout = findViewById(R.id.success_layout);
        Animation showAnim = new AlphaAnimation(0.0f, 1.0f);
        showAnim.setDuration(1000);
        showAnim.setInterpolator(new DecelerateInterpolator());
        showAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                successLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        AlphaAnimation hideAnim = new AlphaAnimation(1.0f, 0.0f);
        hideAnim.setDuration(1000);
        hideAnim.setStartOffset(1000);
        hideAnim.setInterpolator(new AccelerateInterpolator());
        hideAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                successLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(showAnim);
        animation.addAnimation(hideAnim);

        successLayout.startAnimation(animation);
    }
}
