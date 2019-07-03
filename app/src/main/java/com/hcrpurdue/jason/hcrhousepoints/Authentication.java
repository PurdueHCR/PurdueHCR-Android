package com.hcrpurdue.jason.hcrhousepoints;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import Models.Link;
import Utils.Singleton;
import Utils.SingletonInterface;

public class Authentication extends AppCompatActivity {
    private FirebaseAuth auth;
    private Map<String, Pair<String, String>> floorCodes;
    private FirebaseFirestore db;
    static private Singleton singleton;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_authentication);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        singleton = Singleton.getInstance(getApplicationContext());

        handleLinks();

        // Check if user is signed in (non-null) and update UI accordingly. Authentication should be cached automatically
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            findViewById(R.id.authenticationProgressBar).setVisibility(View.VISIBLE);
            if (singleton.cacheFileExists()) {
                singleton.getUserData(new SingletonInterface() {
                });
                launchNextActivity();
            } else {
                singleton.getUserDataNoCache(new SingletonInterface() {
                    public void onSuccess() {
                        launchNextActivity();
                    }
                });
            }
        } else {
            EditText passwordInput = findViewById(R.id.passwordInput);
            passwordInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
            passwordInput.setOnEditorActionListener((textView, i, keyEvent) -> {
                if (i == EditorInfo.IME_ACTION_DONE || (i == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN))
                    signIn(null);
                return true;
            });
            ((EditText) findViewById(R.id.floorCodeInput)).setOnEditorActionListener((textView, i, keyEvent) -> {
                if (i == EditorInfo.IME_ACTION_DONE || (i == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN))
                    signUp(null);
                return true;
            });
            getFloorCodes();
        }
    }


    //TODO: FORGOT PASS

    public void forgotPassword(View view) {
        /* Code for Forgot Password Dialog */

        TextView fgtPassView = findViewById(R.id.forgot_password);

        fgtPassView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openResetPasswordDialog();
            }
        });


        /* Code for Forgot Password Dialog */

    }

    //TODO: END FORGOT PASS


    /* Opens the reset password dialog box invoked when user clicks Forgot Password TextView */
    public void openResetPasswordDialog() {

        PasswordResetDialog resetDialog = new PasswordResetDialog();

        resetDialog.show(getSupportFragmentManager(), "Password Reset");


        /* If the email verification in PasswordResetDialog onCreateDialog() method checks out, it returns a boolean that allows the
        * password reset process to continue in this function */

        if (resetDialog.emailValid()) {
            String email = resetDialog.getUserEmail();

            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(Authentication.this, "Password Reset Email has been sent!", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(Authentication.this,
                                        "Email failed to send. Please verify your email is correct and try again.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }


                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Authentication.this, "Please verify your email", Toast.LENGTH_SHORT).show();
                        }
                    });




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
                        if (singleton.cacheFileExists()) {
                            singleton.getUserData(new SingletonInterface() {
                            });
                            launchNextActivity();
                        } else {
                            singleton.getUserDataNoCache(new SingletonInterface() {
                                public void onSuccess() {
                                    launchNextActivity();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(this, "Authentication failed. Please verify your email and password and try again.",
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
                                        singleton.setUserData(floor, house, nameText, 0, id);
                                        launchNextActivity();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, "User DB binds failed, please tell your RHP to tell Jason", Toast.LENGTH_LONG).show());
                        } else {
                            Toast.makeText(this, "Error loading user after authentication, please try logging in", Toast.LENGTH_LONG).show();
                            Log.e("Authentication", "User was generated in FirebaseAuth but was not loaded");
                        }
                    } else {
                        Toast.makeText(this, "Authentication failed, try again later before contacting your RHP",
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
            Toast.makeText(this, "Email is not a valid Purdue email address", Toast.LENGTH_SHORT).show();
            return true;
        }

        // Checks the password text
        String passwordText = password.getText().toString();
        if (TextUtils.isEmpty(passwordText)) {
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (passwordText.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Passwords must match", Toast.LENGTH_SHORT).show();
            return true;
        }

        // Checks to make sure that there is at least one space in the name for first and last name
        if (!nameText.contains(" ")) {
            Toast.makeText(this, "Name must include your first and last name", Toast.LENGTH_SHORT).show();
            return true;
        }

        // Checks that the floor code is valid
        if (!floorCodes.containsKey(floorCodeText)) {
            Toast.makeText(this, "Floor code invalid", Toast.LENGTH_SHORT).show();
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
        intent.putExtra("PointSubmitted", false);
        startActivity(intent);
        finish();
    }

    private void handleLinks() {
        //Toast.makeText(getApplicationContext(), "Handling link",
        //Toast.LENGTH_SHORT).show();
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null && intent.getData().getHost() != null) {
            findViewById(R.id.authenticationProgressBar).setVisibility(View.VISIBLE);
            //Toast.makeText(getApplicationContext(), intent.getData().toString(),
            //Toast.LENGTH_SHORT).show();
            String host = intent.getData().getHost();
            String path = intent.getData().getPath();
            singleton.getCachedData();

            //Toast.makeText(getApplicationContext(), "Host: "+host,
            //Toast.LENGTH_SHORT).show();
            //Toast.makeText(getApplicationContext(), "path: "+path,
            //    Toast.LENGTH_SHORT).show();
            if (auth.getCurrentUser() != null) {
                if (host.equals("addpoints")) {
                    String[] parts = path.split("/");
                    if (parts.length == 2) {
                        String linkId = parts[1].replace("/", "");
                        singleton.getLinkWithLinkId(linkId, new SingletonInterface() {
                            @Override
                            public void onError(Exception e, Context context) {
                                Toast.makeText(Authentication.this, "Failed to count link with issue: " + e.getLocalizedMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onGetLinkWithIdSuccess(Link link) {
                                singleton.submitPointWithLink(link, new SingletonInterface() {
                                    @Override
                                    public void onSuccess() {
                                        Intent intent = new Intent(Authentication.this, NavigationDrawer.class);
                                        intent.putExtra("PointSubmitted", true);
                                        startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void onError(Exception e, Context context) {
                                        Toast.makeText(Authentication.this, "Failed to count link with issue: " + e.getLocalizedMessage(),
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
            } else
                Toast.makeText(this, "You must log in before submitting points", Toast.LENGTH_LONG).show();
        }
    }
}