/**
 * Navigation ACtivity is the root activity. It holds the fragment manager and controls the navigation menu.
 *
 */

package com.hcrpurdue.jason.hcrhousepoints.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.hcrpurdue.jason.hcrhousepoints.Fragments.Events;
import com.hcrpurdue.jason.hcrhousepoints.Fragments.HousePointHistoryFragment;
import com.hcrpurdue.jason.hcrhousepoints.Fragments.Laundry;
import com.hcrpurdue.jason.hcrhousepoints.Fragments.NotificationListFragment;
import com.hcrpurdue.jason.hcrhousepoints.Fragments.PersonalPointLogListFragment;
import com.hcrpurdue.jason.hcrhousepoints.Fragments.PointApprovalFragment;
import com.hcrpurdue.jason.hcrhousepoints.Fragments.PointTypeListFragment;
import com.hcrpurdue.jason.hcrhousepoints.Fragments.QRCodeListFragment;
import com.hcrpurdue.jason.hcrhousepoints.Fragments.QRCreationFragment;
import com.hcrpurdue.jason.hcrhousepoints.Fragments.QRScannerFragment;
import com.hcrpurdue.jason.hcrhousepoints.Fragments.RHPProfileFragment;
import com.hcrpurdue.jason.hcrhousepoints.Fragments.ResidentProfileFragment;
import com.hcrpurdue.jason.hcrhousepoints.Fragments.SubmitPointsFragment;
import com.hcrpurdue.jason.hcrhousepoints.R;
import com.hcrpurdue.jason.hcrhousepoints.Utils.AlertDialogHelper;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;
import com.hcrpurdue.jason.hcrhousepoints.Utils.FirebaseListenerUtil;

import java.util.Objects;

public class NavigationActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    static private CacheManager cacheManager;
    private FragmentManager fragmentManager;
    private FirebaseListenerUtil flu;
    private Menu menu;
    private NavigationView navigationView;
    private final String RHP_NOTIFICATION_CALLBACK_KEY = "NAVIGATION_ACTIVITY_RHP_NOTIFICATION";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setUtilities();
        setupAppTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        setupNavigationView();

        fragmentManager = getSupportFragmentManager();
        setDefaultFragment();

        //Look to see if the intent contains a qr code to submit
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("PointSubmitted") && extras.getBoolean("PointSubmitted")){
            animateSuccess();
        }

        //Setup the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        drawerLayout = findViewById(R.id.drawer_layout);


        //Add the OnClickListener to the navigation menu to respond when a tab is tapped
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    //Get the Menu Item that was tapped
                    MenuItem checkedItem = navigationView.getCheckedItem();

                    int currentItem = checkedItem == null ? 0 : checkedItem.getItemId();

                    drawerLayout.closeDrawers();
                    int selectedItem = menuItem.getItemId();

                    //Declare a variable to hold the class of the new fragment
                    Class fragmentClass = null;
                    switch (selectedItem) {
                        case R.id.nav_signout:
                            //If signout is tapped, display a prompt to make sure they want to log out
                            new AlertDialog.Builder(this)
                                    .setTitle("Sign Out")
                                    .setMessage("Are you sure you want to sign out?")
                                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> signOut())
                                    .setNegativeButton(android.R.string.no, null).show();
                            break;
                        case R.id.nav_point_type_list:
                            //If Submit points is tapped, display the Point Type list
                            fragmentClass = PointTypeListFragment.class;
                            break;
                        case R.id.nav_approve_point:
                            //If RHP taps approve option, display PointApproval list
                            fragmentClass = PointApprovalFragment.class;
                            break;
                        case R.id.nav_profile:
                            switch (cacheManager.getUser().getPermissionLevel()){
                                case RESIDENT:
                                    fragmentClass = ResidentProfileFragment.class;
                                    break;
                                case RHP:
                                    fragmentClass = RHPProfileFragment.class;
                                    break;
                                case PRIVILEGED_RESIDENT:
                                    fragmentClass = ResidentProfileFragment.class;
                                    break;
                                default:
                                    throw new Error("UNIMPLEMENTED PERMISSION DEFAULT FRAGMENT");
                            }
                            break;
                        case R.id.nav_scan_code:
                            //If the QR scanner is selected, check permission and display if approved
                            if (ContextCompat.checkSelfPermission(Objects.requireNonNull(this), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 10);
                                return true;
                            }
                            else
                                fragmentClass = QRScannerFragment.class;
                            break;
                        case R.id.nav_qr_code_list:
                            //If qr code list is selected, display the QR code list
                            fragmentClass = QRCodeListFragment.class;
                            break;
                        case R.id.generateQRCode:
                            //If generate QR Code is selected, transition appropriately
                            fragmentClass = QRCreationFragment.class;
                            break;
                        case R.id.nav_report_issue:
                            //If report issue button is selected, open browser to display web page
                            Intent reportIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/hcr-points/home"));
                            reportIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(reportIntent);
                            break;
                        case R.id.nav_point_history:
                            //Open Point history fragment
                            fragmentClass = HousePointHistoryFragment.class;
                            break;
                        case R.id.nav_submit_point:
                            //Set fragment class as submit points fragment
                            fragmentClass = SubmitPointsFragment.class;
                            break;
                        case R.id.nav_personal_point_log_list:
                            //Display fragment with all of a user's submitted point
                            fragmentClass = PersonalPointLogListFragment.class;
                            break;
                        case R.id.nav_notification_fragment:
                            fragmentClass = NotificationListFragment.class;
                            break;
                        case R.id.nav_events:
                            fragmentClass = Events.class;
                            break;
                        case R.id.nav_laundry:
                            fragmentClass = Laundry.class;
                            break;
                        default:

                            //By default display the house overview
                            fragmentClass = ResidentProfileFragment.class;
                            break;
                    }
                    if (fragmentClass != null && currentItem != selectedItem ) {
                        //Get the fragment from the fragment manager
                        Fragment fragment = fragmentManager.findFragmentByTag(Integer.toString(selectedItem));
                        //If no fragment then create one
                        if (fragment == null) {
                            try {
                                //Create a new instance of the chosen fragment
                                fragment = (Fragment) fragmentClass.newInstance();
                            } catch (Exception e) {
                                Toast.makeText(this, "Failed to load Fragment while changing views", Toast.LENGTH_LONG).show();
                                Log.e("NavigationActivity", "Failed to load fragment on menu select", e);
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

    /**
     * set the cachemanager and FirestoreListenerUtil
     */
    private void setUtilities(){
        cacheManager = CacheManager.getInstance(getApplicationContext());
        cacheManager.getCachedData();
        flu = FirebaseListenerUtil.getInstance(this);
    }

    /**
     * Based on the house that the user is in, set the theme (color)
     */
    private void setupAppTheme(){
        try {
            int themeID = getResources().getIdentifier(cacheManager.getHouseName().toLowerCase(), "style", this.getPackageName());
            setTheme(themeID);
        } catch (Exception e) {
            Toast.makeText(NavigationActivity.this, "Error loading house theme", Toast.LENGTH_LONG).show();
            Log.e("NavigationActivity", "Failed to load house color", e);
        }
    }

    private void setupNavigationView(){
        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        try {
            //Draw the house icon on the navigation menu
            int drawableID = getResources().getIdentifier(cacheManager.getHouseName().toLowerCase(), "drawable", getPackageName());
            ((ImageView) headerView.findViewById(R.id.header_house_image)).setImageResource(drawableID);
        } catch (Exception e) {
            Toast.makeText(NavigationActivity.this, "Failed to load house image", Toast.LENGTH_LONG).show();
        }
        //Set the name and house of the user in the navigation menu
        ((TextView) headerView.findViewById(R.id.header_resident_name)).setText(cacheManager.getName());
        String floorName = cacheManager.getHouseName() + " - " + cacheManager.getFloorName();
        ((TextView) headerView.findViewById(R.id.header_floor_name)).setText(floorName);
        menu = navigationView.getMenu();

        //Use the permission levels to set the appropriate navigation menu options
        try {
            // cases one through three intentionally cascade down
            switch (cacheManager.getPermissionLevel()){
                case PRIVILEGED_RESIDENT: //privileged Resident Case
                    menu.findItem(R.id.nav_qr_code_list).setVisible(true);
                    break;
                case FHP://Facility Member
                case PROFESSIONAL_STAFF://Professional Staff
                case RHP://RHP
                    menu.findItem(R.id.nav_approve_point).setVisible(true);
                    menu.findItem(R.id.nav_point_history).setVisible(true);
                    menu.findItem(R.id.nav_qr_code_list).setVisible(true);
                default:break;// Resident
            }
        } catch (Exception e) {
            Toast.makeText(NavigationActivity.this, "Error loading permission level", Toast.LENGTH_LONG).show();
            Log.e("NavigationActivity", "Failed to load Permission Level", e);
        }
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
        FirebaseListenerUtil.getInstance(getApplicationContext()).resetFirebaseListeners();
        findViewById(R.id.navigationProgressBar).setVisibility(View.VISIBLE);
        AsyncTask.execute(() -> cacheManager.clearUserData());
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 10) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, QRScannerFragment.class.newInstance(), Integer.toString(R.id.nav_scan_code)).addToBackStack(null).commit();
                } catch (Exception e) {
                    Log.e("Navigation", "Failed to launch QR fragment", e);
                }
            } else {
                ((NavigationView) findViewById(R.id.nav_view)).getMenu().findItem(R.id.nav_scan_code).setChecked(false);
                cameraPermissionDenied();
            }
        }
    }


    /**
     * When the back button is pressed, transition to previous fragment
     */
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

    /**
     * Used when a task is successful to display to the user that it is a success
     */
    public void animateSuccess() {
        RelativeLayout successLayout = findViewById(R.id.success_layout);
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

    private void cameraPermissionDenied(){
        AlertDialogHelper.showSingleButtonDialog(this, "Could Not Launch Camera", "Camera permissions denied, please accept them in your settings.", "OK", null)
                .show();
    }

    private void setDefaultFragment(){
        if (fragmentManager.getFragments().isEmpty()) {
            try {
                switch (cacheManager.getUser().getPermissionLevel()){
                    case RESIDENT:
                        fragmentManager.beginTransaction().replace(R.id.content_frame, ResidentProfileFragment.class.newInstance(), Integer.toString(R.id.nav_profile)).addToBackStack("BASE").commit();
                        break;
                    case RHP:
                        fragmentManager.beginTransaction().replace(R.id.content_frame, RHPProfileFragment.class.newInstance(), Integer.toString(R.id.nav_profile)).addToBackStack("BASE").commit();
                        break;
                    case PRIVILEGED_RESIDENT:
                        fragmentManager.beginTransaction().replace(R.id.content_frame, ResidentProfileFragment.class.newInstance(), Integer.toString(R.id.nav_profile)).addToBackStack("BASE").commit();
                        break;
                    default:
                            throw new Error("UNIMPLEMENTED PERMISSION DEFAULT FRAGMENT");
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error loading Profile Frament", Toast.LENGTH_LONG).show();
                Log.e("NavigationActivity", "Failed to load initial fragment", e);
            }
        }
        else{
            fragmentManager.popBackStackImmediate("BASE", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }
}
