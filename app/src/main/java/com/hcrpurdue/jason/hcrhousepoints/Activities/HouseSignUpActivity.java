/**
 * House Sign Up Activity - Input first and last name and house code. If code checks out
 *    Add the user to the house and floor and permission level that matches that code
 */

package com.hcrpurdue.jason.hcrhousepoints.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.LinkMovementMethod;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hcrpurdue.jason.hcrhousepoints.Models.Enums.UserPermissionLevel;
import com.hcrpurdue.jason.hcrhousepoints.Models.HouseCode;
import com.hcrpurdue.jason.hcrhousepoints.Models.Link;
import com.hcrpurdue.jason.hcrhousepoints.Models.ResponseCodeMessage;
import com.hcrpurdue.jason.hcrhousepoints.Models.User;
import com.hcrpurdue.jason.hcrhousepoints.R;
import com.hcrpurdue.jason.hcrhousepoints.Utils.AlertDialogHelper;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.AlertDialogInterface;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.CacheManagementInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HouseSignUpActivity extends AppCompatActivity {

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText houseCodeEditText;
    private CheckBox privacyCheckBox;
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
        handleJoinHouseLink(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        handleJoinHouseLink(this);
    }

    private void initializeViews(){
        firstNameEditText = findViewById(R.id.first_name_input);
        lastNameEditText = findViewById(R.id.last_name_input);
        houseCodeEditText = findViewById(R.id.house_code_input);
        joinButton = findViewById(R.id.join_button);
        loadingBar = findViewById(R.id.initialization_progress_bar);
        loadingBar.setVisibility(View.INVISIBLE);
        privacyCheckBox = findViewById(R.id.privacy_and_terms_checkbox);

        privacyCheckBox.setMovementMethod(LinkMovementMethod.getInstance());
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
        if(areFieldsValid(first,last,code, privacyCheckBox.isChecked())){
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
    private boolean areFieldsValid(String first, String last, String code, Boolean checkedBox){
        if(first == null || last == null || code == null){
            Toast.makeText(getApplicationContext(), "Please make sure that no fields are empty.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(first.isEmpty() || last.isEmpty() || code.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please make sure that no fields are empty.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!checkedBox){
            Toast.makeText(getApplicationContext(), "Please agree to the privacy and the terms and conditions.", Toast.LENGTH_SHORT).show();
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

        FirebaseUser firebaseUser = auth.getCurrentUser();
        String floorId = houseCode.getFloorId();
        String houseName = houseCode.getHouseName();
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();

        if (firebaseUser != null) {
            String id = firebaseUser.getUid();
            cacheManager.createUser(firstName, lastName, houseCode.getCode(), new CacheManagementInterface() {
                @Override
                public void onHttpCreateUserSuccess(User user) {
                    cacheManager.setUserAndCache(user, id);
                    launchInitializationActivity();
                }

                @Override
                public void onError(Exception e, Context context) {
                    stopLoading();
                    Toast.makeText(context, "Could not create user. Please try again.", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onHttpError(ResponseCodeMessage responseCodeMessage) {
                    stopLoading();
                    System.out.println("HTTP ERROR: "+responseCodeMessage.getMessage());
                }
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

    private void handleJoinHouseLink(AppCompatActivity activity) {

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        System.out.println("Should handle link in house signup");
                        Uri deepLink = null;
                        if(pendingDynamicLinkData != null && pendingDynamicLinkData.getLink() != null){
                            System.out.println("Got deepLink: "+pendingDynamicLinkData.getLink());
                            deepLink = pendingDynamicLinkData.getLink();
                            String url = deepLink.toString();
                            url = url.replace("https://purdue-hcr-test.web.app/#/", "");
                            url = url.replace("https://purduehcr.web.app/#/", "");
                            if(url.split("/").length == 2){
                                String command = url.split("/")[0];
                                String data = url.split("/")[1];
                                if (command.contains("createaccount")) {
                                    String houseCode = data.replace("/", "");
                                    houseCodeEditText.setText(houseCode);
                                    houseCodeEditText.setEnabled(false);
                                } else {
                                    System.out.println("We don't handle the path: "+command+" in the initialization activity.");
                                }
                            }
                            else {
                                AlertDialogHelper.showSingleButtonDialog(activity, "Could Not Find House Code",
                                        "Sorry. We were unable to find that house code. Please let whoever gave you this link know that it is out of date.",
                                        "Ok",null);
                            }

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                AlertDialogHelper.showSingleButtonDialog(activity, "Could Not Find House Code",
                        "Sorry. We were unable to find that house code. Please let whoever gave you this link know that it is out of date.",
                        "Ok",null);
            }
        });
    }

}
