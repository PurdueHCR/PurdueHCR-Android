package com.hcrpurdue.jason.hcrhousepoints.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hcrpurdue.jason.hcrhousepoints.R;
import com.hcrpurdue.jason.hcrhousepoints.Utils.Singleton;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.SingletonInterface;

import java.util.Map;

public class AppInitializationActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private Singleton singleton;
    private ProgressBar loadingBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_app_initialization);
        auth = FirebaseAuth.getInstance();
        singleton = Singleton.getInstance(getApplicationContext());
        initializeViews();
        initializeCachedData();

    }

    private void initializeViews(){
        loadingBar = findViewById(R.id.initialization_progress_bar);
        loadingBar.setVisibility(View.VISIBLE);
    }

    /**
     * Method will ensure data is cached before loading is complete
     */
    private void initializeCachedData(){
        if(isLoggedIn()){
            if (singleton.cacheFileExists()) {
                singleton.getUserData(new SingletonInterface() {
                    public void onSuccess() {
                        launchNavigationActivity();
                    }
                    public void onError(Exception e, Context context){
                        if(e.getMessage().equals("User does not exist.")){
                            handleMissingUserInformation();
                        }
                        else{
                            Toast.makeText(AppInitializationActivity.this,
                                    e.getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                singleton.getUserDataNoCache(new SingletonInterface() {
                    public void onSuccess() {
                        launchNavigationActivity();
                    }
                    public void onError(Exception e, Context context){
                        if(e.getMessage().equals("User does not exist.")){
                            handleMissingUserInformation();
                        }
                        else{
                            Toast.makeText(AppInitializationActivity.this,
                                    e.getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        else{
            launchSignInActivity();
        }
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
        Intent intent = new Intent(this, NavigationDrawer.class);
        intent.putExtra("PointSubmitted", false);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        finish();
    }

    private void launchHouseSignUpActivity(){
        Intent intent = new Intent(this, HouseSignUpActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        finish();
    }

    private void handleMissingUserInformation(){
        new AlertDialog.Builder(this)
                    .setTitle("Welcome Back")
                    .setMessage("Welcome back to Purdue HCR! Please connect with a new house to start earning points.")
                    .setPositiveButton("Let's Go!", (dialog, whichButton) -> {
                        launchHouseSignUpActivity();
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            launchSignInActivity();
                        }
                    }).show();



    }
}
