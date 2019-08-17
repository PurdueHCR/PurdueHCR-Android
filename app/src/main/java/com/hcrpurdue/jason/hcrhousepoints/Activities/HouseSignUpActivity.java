/**
 * House Sign Up Activity - Input first and last name and house code. If code checks out
 *    Add the user to the house and floor and permission level that matches that code
 */

package com.hcrpurdue.jason.hcrhousepoints.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hcrpurdue.jason.hcrhousepoints.Models.HouseCode;
import com.hcrpurdue.jason.hcrhousepoints.R;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.CacheManagementInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HouseSignUpActivity extends AppCompatActivity {

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText houseCodeEditText;
    private Button joinButton;
    private ProgressBar loadingBar;

    private FirebaseFirestore db;

    private CacheManager cacheManager;
    private List<HouseCode> floorCodes;
    private boolean floorCodesLoaded = false;

    private FirebaseAuth auth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_sign_up);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        cacheManager = CacheManager.getInstance(getApplicationContext());
        getFloorCodes();
        initializeViews();
    }

    private void initializeViews(){
        firstNameEditText = findViewById(R.id.first_name_input);
        lastNameEditText = findViewById(R.id.last_name_input);
        houseCodeEditText = findViewById(R.id.house_code_input);
        joinButton = findViewById(R.id.join_button);
        loadingBar = findViewById(R.id.initialization_progress_bar);
        loadingBar.setVisibility(View.INVISIBLE);
    }

    // Fills floorCodes with key:value pairs in the form of {floorCode}:({floorName}:{houseName})
    private void getFloorCodes() {
        cacheManager.getHouseCodes(new CacheManagementInterface() {
            @Override
            public void onGetHouseCodes(List<HouseCode> codes){
                floorCodes = codes;
                floorCodesLoaded = true;
            }
        });
    }

    /**
     * When the join button is tapped, create the user
     * @param view
     */
    public void join(View view){
        startLoading();
        String first = firstNameEditText.getText().toString();
        String last = lastNameEditText.getText().toString();
        String code = houseCodeEditText.getText().toString();
        if(areFieldsValid(first,last,code)){
            HouseCode houseCode = getHouseCode(code);
            if(houseCode != null) {
                createUser(houseCode);
            }
            else{
                Toast.makeText(getApplicationContext(),"Could not find that code.",Toast.LENGTH_LONG).show();
                stopLoading();
            }
        }
        else{
            stopLoading();

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

    private HouseCode getHouseCode(String code){
        while(!floorCodesLoaded){
            try {
                wait(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for(HouseCode hc: floorCodes){
            if(hc.getCode().equals(code)){
                return hc;
            }
        }
        return null;
    }

    /**
     * Create the user record
     */
    private void createUser(HouseCode houseCode){

        FirebaseUser user = auth.getCurrentUser();

        String floor = houseCode.getFloorId();
        String house = houseCode.getHouseName();
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        int permissionLevel = houseCode.getPermissionLevel().getFirestoreValue();
        Map<String, Object> userData = new HashMap<>();
        userData.put("FirstName", firstName);
        userData.put("LastName", lastName );
        userData.put("FloorID", floor);
        userData.put("House", house);
        userData.put("Permission Level", permissionLevel);
        userData.put("TotalPoints", 0);
        if (user != null) {
            String id = user.getUid();
            db.collection("Users").document(id).set(userData)
                    .addOnSuccessListener(aVoid -> {
                        cacheManager.setUserData(floor, house, firstName,lastName, permissionLevel, id);
                        launchInitializationActivity();
                    })
                    .addOnFailureListener(e -> {
                            stopLoading();
                            Toast.makeText(this, "Could not create user. Please try again.", Toast.LENGTH_LONG).show();
                    });
        } else {
            Toast.makeText(this, "Error loading user after authentication, please try logging in", Toast.LENGTH_LONG).show();
            launchSignInActivity();
        }
    }

    /**
     * Transition to the initialization activity. This ensures that all the other data is cached before moving on.
     */
    private void launchInitializationActivity() {
        stopLoading();
        Intent intent = new Intent(this, AppInitializationActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Transition to Sign In Activity
     */
    private void launchSignInActivity(){
        stopLoading();
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_reverse,R.anim.slide_out_reverse);
    }

    /**
     * Handle someone hitting the back button on the device
     */
    @Override
    public void onBackPressed() {
        if(isTaskRoot()){
            //If this is the first activity displayed after the loading page, transition to the sign in page
            launchSignInActivity();
        }
        else{
            //If there were activities before this, go back
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_reverse,R.anim.slide_out_reverse);
        }

    }

    private void startLoading(){
        loadingBar.setVisibility(View.VISIBLE);
        joinButton.setEnabled(false);
    }

    private void stopLoading(){
        loadingBar.setVisibility(View.INVISIBLE);
        joinButton.setEnabled(true);
    }

}
