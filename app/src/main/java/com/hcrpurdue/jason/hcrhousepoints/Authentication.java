package com.hcrpurdue.jason.hcrhousepoints;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import Models.Link;
import Utils.FirebaseUtil;
import Utils.Singleton;
import Utils.SingletonInterface;

public class Authentication extends AppCompatActivity {
    private FirebaseAuth auth;
    private Map<String, Pair<String, String>> floorCodes;
    private FirebaseFirestore db;
    private Singleton singleton;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handleLinks();

        setContentView(R.layout.activity_authentication);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        singleton = Singleton.getInstance();
        singleton.setApplicationContext(getApplicationContext());

        // Check if user is signed in (non-null) and update UI accordingly. Authentication should be cached automatically
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            findViewById(R.id.authenticationProgressBar).setVisibility(View.VISIBLE);
            singleton.getUserData(currentUser.getUid(), new SingletonInterface() {

                public void onSuccess() {
                    launchNextActivity();
                }

            });
        } else {
            getFloorCodes();
        }
    }

    public void signIn(View view) {
        EditText email = findViewById(R.id.emailInput);
        EditText password = findViewById(R.id.passwordInput);
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();

        if (signInInvalid(email, password))
            return;

        findViewById(R.id.authenticationProgressBar).setVisibility(View.VISIBLE);
        auth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        singleton.getUserData(Objects.requireNonNull(auth.getCurrentUser()).getUid(), new SingletonInterface() {

                            public void onSuccess() {
                                launchNextActivity();
                            }

                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "Authentication failed. Please verify your email and password and try again.",
                                Toast.LENGTH_LONG).show();
                        findViewById(R.id.authenticationProgressBar).setVisibility(View.GONE);
                    }
                });
    }

    public void signUp(View view) {
        EditText email = findViewById(R.id.emailInput);
        EditText password = findViewById(R.id.passwordInput);
        EditText passwordConfirm = findViewById(R.id.confirmPasswordInput);
        EditText name = findViewById(R.id.nameInput);
        EditText floorCode = findViewById(R.id.floorCodeInput);
        final String emailText = email.getText().toString();
        final String passwordText = password.getText().toString();
        final String nameText = name.getText().toString();
        final String floorCodeText = floorCode.getText().toString();


        if (signInInvalid(email, password) || signUpInvalid(password, passwordConfirm, name, floorCode))
            return;

        findViewById(R.id.authenticationProgressBar).setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();

                        // Generates all of the other data for the user in the DB
                        Pair pair = floorCodes.get(floorCodeText);
                        String floor = (String) pair.first;
                        String house = (String) pair.second;
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("Name", nameText);
                        userData.put("FloorID", floor);
                        userData.put("House", house);
                        userData.put("Permission Level", 0);
                        userData.put("TotalPoints", 0);
                        if (user != null) {
                            String id = user.getUid();
                            db.collection("Users").document(id).set(userData)
                                    .addOnSuccessListener(aVoid -> {
                                        singleton.setUserData(floor, house, nameText, 0, 0, id);
                                        launchNextActivity();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "User DB binds failed, please tell your RHP to tell Jason", Toast.LENGTH_LONG).show());
                        } else {
                            Toast.makeText(getApplicationContext(), "Error loading user after authentication, please try logging in", Toast.LENGTH_LONG).show();
                            Log.e("Authentication", "User was generated in FirebaseAuth but was not loaded");
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Authentication failed, try again later before contacting your RHP",
                                Toast.LENGTH_SHORT).show();
                        findViewById(R.id.authenticationProgressBar).setVisibility(View.GONE);
                    }
                });
    }

    private boolean signInInvalid(EditText email, EditText password) {
        // Hide the virtual keyboard
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null && getCurrentFocus() != null) // Avoids null pointer exceptions
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

        // Checks the email text, makes sure it's a valid email address, and makes sure its a Purdue address
        String emailText = email.getText().toString().toLowerCase();
        if (TextUtils.isEmpty(emailText) || !Patterns.EMAIL_ADDRESS.matcher(emailText).matches() || !emailText.matches("[A-Z0-9a-z._%+-]+@purdue\\.edu")) {
            Toast.makeText(getApplicationContext(), "Email is not a valid Purdue email address", Toast.LENGTH_SHORT).show();
            return true;
        }

        // Checks the password text
        String passwordText = password.getText().toString();
        if (TextUtils.isEmpty(passwordText)) {
            Toast.makeText(getApplicationContext(), "Password cannot be empty", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (passwordText.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }

    private boolean signUpInvalid(EditText password, EditText passwordConfirm, EditText name, EditText floorCode) {
        String passwordText = password.getText().toString();
        String passwordConfirmText = passwordConfirm.getText().toString();
        String nameText = name.getText().toString();
        String floorCodeText = floorCode.getText().toString();

        // Checks that the passwords match
        if (!passwordText.equals(passwordConfirmText)) {
            Toast.makeText(getApplicationContext(), "Passwords must match", Toast.LENGTH_SHORT).show();
            return true;
        }

        // Checks to make sure that there is at least one space in the name for first and last name
        if (!nameText.contains(" ")) {
            Toast.makeText(getApplicationContext(), "Name must include your first and last name", Toast.LENGTH_SHORT).show();
            return true;
        }

        // Checks that the floor code is valid
        if (floorCodes.get(floorCodeText) == null) {
            Toast.makeText(getApplicationContext(), "Floor code invalid", Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }

    // Fills floorCodes with key:value pairs in the form of {floorCode}:({floorName}:{houseName})
    private void getFloorCodes() {
        singleton.getFloorCodes(new SingletonInterface() {
            @Override
            public void onGetFloorCodesSuccess(Map<String, Pair<String, String>> data) {
                floorCodes = data;
            }
        });
    }

    private void launchNextActivity() {
        Intent intent = new Intent(this, NavigationDrawer.class);
        intent.putExtra("HouseName", singleton.getHouse());
        intent.putExtra("PermissionLevel", singleton.getPermissionLevel());
        startActivity(intent);
        finish();
    }

    private void handleLinks() {
        //Toast.makeText(getApplicationContext(), "Handling link",
        //Toast.LENGTH_SHORT).show();
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            //Toast.makeText(getApplicationContext(), intent.getData().toString(),
            //Toast.LENGTH_SHORT).show();
            String host = intent.getData().getHost();
            String path = intent.getData().getPath();

            //Toast.makeText(getApplicationContext(), "Host: "+host,
            //Toast.LENGTH_SHORT).show();
            //Toast.makeText(getApplicationContext(), "path: "+path,
            //    Toast.LENGTH_SHORT).show();

            if (host.equals("addpoints")) {
                String[] parts = path.split("/");
                if (parts.length == 2) {
                    System.out.println("YO: inside");
                    String linkId = parts[1].replace("/", "");
                    Singleton.getInstance().getLinkWithLinkId(linkId, new SingletonInterface() {
                        @Override
                        public void onError(Exception e, Context context) {
                            Toast.makeText(getApplicationContext(), "Failed to count link with issue: " + e.getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onGetLinkWithIdSuccess(Link link) {
                            System.out.println("YO:Ongetlinksuckess");
                            Singleton.getInstance().submitPointWithLink(link, new SingletonInterface() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(getApplicationContext(), "Points submitted",
                                            Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(Exception e, Context context) {
                                    Toast.makeText(getApplicationContext(), "Failed to count link with issue: " + e.getLocalizedMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            } else {
                Toast.makeText(getApplicationContext(), "Invalid QR Code",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}