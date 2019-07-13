package com.hcrpurdue.jason.hcrhousepoints.Activities;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;

import com.hcrpurdue.jason.hcrhousepoints.R;
import com.hcrpurdue.jason.hcrhousepoints.Utils.Singleton;

public class LogInActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private Map<String, Pair<String, String>> floorCodes;
    private Singleton singleton;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button logInButton;
    private Button createAccountButton;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_log_in);
        auth = FirebaseAuth.getInstance();
        singleton = Singleton.getInstance(getApplicationContext());
        initializeViews();
        handleLogOutIfLoggedIn();
    }

    private void handleLogOutIfLoggedIn(){
        auth.signOut();
    }

    /**
     * initialize the text fields and buttons
     */
    private void initializeViews(){
        emailEditText = findViewById(R.id.email_input);
        passwordEditText = findViewById(R.id.password_input);
        logInButton = findViewById(R.id.log_in_button);
        createAccountButton = findViewById(R.id.create_account_button);
    }


    /**
     *  Handle the sign in proccess from the button
     * @param view
     */
    public void signIn(View view) {
        logInButton.setEnabled(false);
        if(!signInInvalid(emailEditText,passwordEditText)){

            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            launchInitializationActivity();
                        } else {
                            Toast.makeText(this, "Authentication failed. Please verify your email and password and try again.",
                                    Toast.LENGTH_LONG).show();
                            logInButton.setEnabled(true);
                            //findViewById(R.id.authenticationProgressBar).setVisibility(View.GONE);
                        }
                    });
        }
        else{
            logInButton.setEnabled(true);
        }

    }

    /**
     * Verify that the email and password fields are not blank
     * @param email EditText for the email field
     * @param password EditText for the password field
     * @return  Boolean true if invalid and false if valid
     */
    private boolean signInInvalid(EditText email, EditText password) {
        // Hide the virtual keyboard
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null && getCurrentFocus() != null) // Avoids null pointer exceptions
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

        // Checks the email text, makes sure it's a valid email address, and makes sure its a Purdue address
        String emailText = email.getText().toString().toLowerCase();
        if (TextUtils.isEmpty(emailText)) {
            Toast.makeText(this, "Please enter your email.", Toast.LENGTH_SHORT).show();
            return true;
        }

        // Checks the password text
        String passwordText = password.getText().toString();
        if (TextUtils.isEmpty(passwordText)) {
            Toast.makeText(this, "Please enter your password.", Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }

    /**
     * Transition to the initialization activity. This ensures that all the other data is cached before moving on.
     */
    private void launchInitializationActivity() {
        Intent intent = new Intent(this, AppInitializationActivity.class);
        startActivity(intent);
    }

    /**
     * Transition the app to the account creation activity
     * @param view view passed by button press
     */
    public void launchAccountCreationActivity(View view){
        createAccountButton.setEnabled(false);
        Intent intent = new Intent(this, AccountCreationActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
        createAccountButton.setEnabled(true);
    }

}