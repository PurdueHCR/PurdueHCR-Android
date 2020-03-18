/**
 * AppInitializationACtivity - Displays the loading screen and waits until all required data from
 *      the Firestore database is initialized. If the app is out of date, a message will be displayed here.
 *
 *
 */

package com.hcrpurdue.jason.hcrhousepoints.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.hcrpurdue.jason.hcrhousepoints.Models.AuthRank;
import com.hcrpurdue.jason.hcrhousepoints.Models.House;
import com.hcrpurdue.jason.hcrhousepoints.Models.Link;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointLog;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointType;
import com.hcrpurdue.jason.hcrhousepoints.Models.Reward;
import com.hcrpurdue.jason.hcrhousepoints.Models.SystemPreferences;
import com.hcrpurdue.jason.hcrhousepoints.R;
import com.hcrpurdue.jason.hcrhousepoints.Utils.AlertDialogHelper;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;
import com.hcrpurdue.jason.hcrhousepoints.Utils.FirebaseListenerUtil;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.AlertDialogInterface;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.CacheManagementInterface;

import java.util.List;

public class AppInitializationActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private CacheManager cacheManager;
    private ProgressBar loadingBar;

    /**
     * When the activity is created, run this code
     * @param savedInstanceState    - Any saved information from previous instances
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_app_initialization);
        auth = FirebaseAuth.getInstance();
        cacheManager = CacheManager.getInstance(getApplicationContext());
        initializeViews();
        initializeUserData();

    }

    /**
     * Link the views to their local variables and set visibility
     */
    private void initializeViews(){
        loadingBar = findViewById(R.id.initialization_progress_bar);
        loadingBar.setVisibility(View.VISIBLE);
    }

    /**
     * Method will ensure data is cached before loading is complete
     */
    private void initializeUserData(){
        //Is the user logged in with Firestore
        if(isLoggedIn()){
            //Check if the User information is cached on the
            if (cacheManager.cacheFileExists()) {
                //Get the specific data from the Users collection
                cacheManager.getUserData(new CacheManagementInterface() {
                    public void onSuccess() {
                        //Got the user data, now lets get the Firebase token to use with the API
                        auth.getAccessToken(true).addOnCompleteListener(task -> {
                            System.out.println("KEY!!!: "+ task.getResult().getToken());
                            cacheManager.getUser().setFirebaseToken(task.getResult().getToken());
                            //Once User data is cached, start initializing the competition data
                            initializeCompetitionData();
                        });

                    }
                    public void onError(Exception e, Context context){
                        if(e.getMessage().equals("User does not exist.")){
                            //If the user does not exist, then they have a Firestore account but are not
                            // registered with a house for this year. Transition to house sign up
                            handleMissingUserInformation();
                        }
                        else{
                            //If there is another error, make a toast
                            Toast.makeText(AppInitializationActivity.this,
                                    e.getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                            launchSignInActivity();
                        }
                    }
                });
            } else {
                // Get the specific data from the Users collection
                cacheManager.getUserDataNoCache(new CacheManagementInterface() {
                    public void onSuccess() {
                        //Once User data is cached, start initializing the competition data
                        initializeCompetitionData();
                    }
                    public void onError(Exception e, Context context){
                        if(e.getMessage().equals("User does not exist.")){
                            //If the user does not exist, then they have a Firestore account but are not
                            // registered with a house for this year. Transition to house sign up
                            handleMissingUserInformation();
                        }
                        else{
                            //If there is another error, make a toast
                            Toast.makeText(AppInitializationActivity.this,
                                    e.getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                            launchSignInActivity();
                        }
                    }
                });
            }
        }
        else{
            //If the user is not logged into a Firestore account, transition to the sign in activity
            launchSignInActivity();
        }
    }

    /**
     * Make sure that the house competition data is cached
     */
    private void initializeCompetitionData(){

        try {
            System.out.println("RANK 1");
            //get point types from Firestore
            cacheManager.getUpdatedPointTypes(new CacheManagementInterface() {
                public void onPointTypeComplete(List<PointType> data) {
                    System.out.println("RANK 2");
                    //get System Preferences from Firestore
                    cacheManager.getSystemPreferences(new CacheManagementInterface() {
                        @Override
                        public void onGetSystemPreferencesSuccess(SystemPreferences sp) {
                            System.out.println("RANK 3");
                            //Get the rewards for the house competition
                            cacheManager.initPersonalPointLogs(new CacheManagementInterface() {
                                @Override
                                public void onGetPersonalPointLogs(List<PointLog> personalLogs) {
                                    System.out.println("RANK 4");
                                    //Refresh the user rank
                                    cacheManager.refreshUserRank(getBaseContext(), new CacheManagementInterface() {
                                        @Override
                                        public void onError(Exception e, Context context) {
                                            System.out.println("RANK FAILED!!!!!!");
                                            handleDataInitializationError(e);
                                        }

                                        @Override
                                        public void onGetRank(AuthRank rank) {
                                            System.out.println("RANK: "+rank.getHouseRank()+" sem: "+rank.getSemesterRank());
                                            //Check the system preferences for app version, and if not in sync, post update message
                                            if(!sp.isAppUpToDate()){
                                                alertOutOfDateApp();
                                            }
                                            else{
                                                //If everything checks out, transition to the main activity
                                                checkForLinks();
                                            }
                                        }
                                    });

                                }

                                @Override
                                public void onError(Exception e, Context context) {
                                    handleDataInitializationError(e);
                                }
                            });
                        }

                        @Override
                        public void onError(Exception e, Context context) {
                            handleDataInitializationError(e);
                        }
                    });
                }

                @Override
                public void onError(Exception e, Context context) {
                    handleDataInitializationError(e);
                }
            });

        } catch (Exception e) {
            handleDataInitializationError(e);
        }

    }

    /**
     * Start a time so if the loading takes longer than 30 seconds, it can go back to log in
     */
    private void startTimer(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                alertAppIsTakingALongTimeToLoad();
            }
        }, 30000);
    }

    /**
     * If there is an error, display a toast and move to sign in activity
     * @param e
     */
    private void handleDataInitializationError(Exception e){
        Toast.makeText(this, "Failed to load house data. ", Toast.LENGTH_LONG).show();
        launchSignInActivity();
    }

    /**
     * Return true if the user is logged in and false otherwise
     * @return boolean isLoggedIn
     */
    private boolean isLoggedIn(){
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Check for links and transition to naviagtion
     */
    private void checkForLinks(){
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null && intent.getData().getHost() != null) {
            FirebaseDynamicLinks.getInstance()
                    .getDynamicLink(getIntent())
                    .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                        @Override
                        public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                            // Get deep link from result (may be null if no link is found)
                            Uri deepLink = null;
                            if (pendingDynamicLinkData != null) {
                                deepLink = pendingDynamicLinkData.getLink();
                                handleDeepLinks(deepLink);
                            }
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            handleLinks(intent);
                        }
                    });
        }
        else{
            launchNavigationActivity();
        }
    }

    /**
     * Transition to Sign In Activity
     */
    private void launchSignInActivity(){
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * transition to Navigation Activity
     */
    private void launchNavigationActivity(){
        launchNaviationActivity(false);
    }

    /**
     * transition to Navigation Activity
     * @param pointSubmitted Was a point submitted?
     */
    private void launchNaviationActivity(boolean pointSubmitted){
        Intent intent = new Intent(this, NavigationActivity.class);
        intent.putExtra("PointSubmitted", pointSubmitted);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        finish();
    }

    /**
     * display the house sign up activity
     */
    private void launchHouseSignUpActivity(){
        Intent intent = new Intent(this, HouseSignUpActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        finish();
    }

    /**
     * Post dialog warning about out of date app
     */
    private void alertOutOfDateApp(){
        new AlertDialog.Builder(this)
                .setTitle("App Out Of Date")
                .setMessage("We noticed that your app is out of date. Please update to the latest version to take advantage of all the new features.")
                .setPositiveButton("Ok", (dialog, whichButton) -> {
                    checkForLinks();
                })
                .setCancelable(false)
                .show();
    }

    /**
     * Post dialog warning that the app is loading slowly
     */
    private void alertAppIsTakingALongTimeToLoad(){
        new AlertDialog.Builder(this)
                .setTitle("Purdue HCR Is Taking a While")
                .setMessage("Purdue HCR has been loading for a while. Would you like to continue to wait or go back to Sign In?")
                .setPositiveButton("Sign In", (dialog, whichButton) -> {
                    //They want to join a house
                    launchSignInActivity();
                })
                .setNegativeButton("Wait", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startTimer();

                    }
                }).show();
    }

    /**
     * If there is an account without user data, handle user response
     */
    private void handleMissingUserInformation(){
        new AlertDialog.Builder(this)
                    .setTitle("Welcome Back")
                    .setMessage("Welcome back to Purdue HCR! Please connect with a new house to start earning points.")
                    .setPositiveButton("Let's Go!", (dialog, whichButton) -> {
                        //They want to join a house
                        launchHouseSignUpActivity();
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //They hit cancel :(
                            launchSignInActivity();
                        }
                    }).show();
    }

    private void handleDeepLinks(Uri deepLink) {
        handleLinks(new Intent("", deepLink));
    }

    private void handleLinks(Intent intent) {
        String host = intent.getData().getHost();
        String path = intent.getData().getPath();

        if (host.equals("addpoints")) {
            String[] parts = path.split("/");
            if (parts.length == 2) {
                String linkId = parts[1].replace("/", "");
                cacheManager.getLinkWithLinkId(linkId, new CacheManagementInterface() {
                    @Override
                    public void onError(Exception e, Context context) {
                        couldNotFindLink(new AlertDialogInterface() {
                            @Override
                            public void onPositiveButtonListener() {
                                launchNavigationActivity();
                            }
                        });
                    }

                    @Override
                    public void onGetLinkWithIdSuccess(Link link) {
                        foundQrCode(link, new AlertDialogInterface() {
                            @Override
                            public void onPositiveButtonListener() {
                                cacheManager.submitPointWithLink(link, new CacheManagementInterface() {
                                    @Override
                                    public void onSuccess() {
                                        launchNaviationActivity(true);
                                    }

                                    @Override
                                    public void onError(Exception e, Context context) {
                                        failedToSubmitPoints(e, new AlertDialogInterface() {
                                            @Override
                                            public void onPositiveButtonListener() {
                                                launchNavigationActivity();
                                            }
                                        });
                                    }
                                });
                            }

                            @Override
                            public void onNegativeButtonListener() {
                                launchNavigationActivity();
                            }
                        });
                    }
                });
            }
            else{
                couldNotFindLink(null);
                launchNavigationActivity();
            }
        } else {
            couldNotFindLink(null);
            launchNavigationActivity();
        }
    }

    private void couldNotFindLink(AlertDialogInterface alertDialogInterface){
        AlertDialogHelper.showSingleButtonDialog(this,
                "Invalid QR Code", "The QR code you scanned wasn't found. Please try scanning it again.",
                "OK", alertDialogInterface)
                .show();
    }

    private void foundQrCode(Link link, AlertDialogInterface alertDialogInterface){
        AlertDialogHelper.showQRSubmissionDialog(this, link, alertDialogInterface).show();
    }

    private void failedToSubmitPoints(Exception e, AlertDialogInterface alertDialogInterface){
        AlertDialogHelper.showSingleButtonDialog(this, "Failed To Submit Points", e.getMessage(), "OK", alertDialogInterface)
                .show();
    }
}
