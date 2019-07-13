package com.hcrpurdue.jason.hcrhousepoints.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hcrpurdue.jason.hcrhousepoints.R;
import com.hcrpurdue.jason.hcrhousepoints.Utils.Singleton;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.SingletonInterface;

import java.util.HashMap;
import java.util.Map;

public class HouseSignUpActivity extends AppCompatActivity {

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText houseCodeEditText;
    private Button joinButton;
    private FirebaseFirestore db;

    private Singleton singleton;
    private Map<String, Pair<String, String>> floorCodes;
    private boolean floorCodesLoaded = false;

    private FirebaseAuth auth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_sign_up);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        singleton = Singleton.getInstance(getApplicationContext());
        getFloorCodes();
        initializeViews();
    }

    private void initializeViews(){
        firstNameEditText = findViewById(R.id.first_name_input);
        lastNameEditText = findViewById(R.id.last_name_input);
        houseCodeEditText = findViewById(R.id.house_code_input);
        joinButton = findViewById(R.id.join_button);
    }

    // Fills floorCodes with key:value pairs in the form of {floorCode}:({floorName}:{houseName})
    private void getFloorCodes() {
        singleton.getFloorCodes(new SingletonInterface() {
            @Override
            public void onGetFloorCodesSuccess(Map<String, Pair<String, String>> data) {
                floorCodes = data;
                floorCodesLoaded = true;
            }
        });
    }

    public void join(View view){
        joinButton.setEnabled(false);
        String first = firstNameEditText.getText().toString();
        String last = lastNameEditText.getText().toString();
        String code = houseCodeEditText.getText().toString();
        if(areFieldsValid(first,last,code) && houseCodeMatches(code)){
            createUser();
        }
        else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    joinButton.setEnabled(true);
                }
            }, 2000);

        }
    }

    /**
     * verify that the fields are valid
     * @param first
     * @param last
     * @param code
     * @return
     */
    private boolean areFieldsValid(String first, String last, String code){
        if(first == null || last == null || code == null){
            Toast.makeText(getApplicationContext(), "Please make sure that no fields are empty.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(first.isEmpty() || last.isEmpty() || code.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please make sure that no fields are empty.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            return true;
        }
    }

    private boolean houseCodeMatches(String code){
        while(!floorCodesLoaded){
            try {
                wait(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(floorCodes.containsKey(code)){
            return true;
        }
        else{
            Toast.makeText(getApplicationContext(), "Could not find house code.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * Create the user record
     */
    private void createUser(){

        FirebaseUser user = auth.getCurrentUser();

        // Generates all of the other data for the user in the DB
        Pair pair = floorCodes.get(houseCodeEditText.getText().toString());
        String floor = (String) pair.first;
        String house = (String) pair.second;
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        Map<String, Object> userData = new HashMap<>();
        userData.put("FirstName", firstName);
        userData.put("LastName", lastName );
        userData.put("Name",firstName+" "+lastName);
        userData.put("FloorID", floor);
        userData.put("House", house);
        userData.put("Permission Level", 0);
        userData.put("TotalPoints", 0);
        if (user != null) {
            String id = user.getUid();
            db.collection("Users").document(id).set(userData)
                    .addOnSuccessListener(aVoid -> {
                        singleton.setUserData(floor, house, firstName,lastName, 0, id);
                        launchInitializationActivity();
                    })
                    .addOnFailureListener(e -> {
                            joinButton.setEnabled(true);
                            Toast.makeText(this, "Could not create user. Please try again.", Toast.LENGTH_LONG).show();
                    });
        } else {
            Toast.makeText(this, "Error loading user after authentication, please try logging in", Toast.LENGTH_LONG).show();
            Log.e("Authentication", "User was generated in FirebaseAuth but was not loaded");
        }
    }

    /**
     * Transition to the initialization activity. This ensures that all the other data is cached before moving on.
     */
    private void launchInitializationActivity() {
        Intent intent = new Intent(this, AppInitializationActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Transition to Sign In Activity
     */
    private void launchSignInActivity(){
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_reverse,R.anim.slide_out_reverse);
    }

    @Override
    public void onBackPressed() {
        if(isTaskRoot()){
            launchSignInActivity();
        }
        else{
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_reverse,R.anim.slide_out_reverse);
        }

    }

}
