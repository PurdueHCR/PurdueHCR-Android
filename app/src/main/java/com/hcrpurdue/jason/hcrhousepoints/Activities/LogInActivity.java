/**
 *  LogInActivity- will display the log in screen. Here there are options for creating an account
 *      And launching the forgot password process
 */

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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;

import com.hcrpurdue.jason.hcrhousepoints.R;
import com.hcrpurdue.jason.hcrhousepoints.Utils.ForgotPasswordDialog;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;

public class LogInActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button logInButton;
    private Button createAccountButton;
    private ProgressBar loadingBar;

    /**
     * When the activity is created, setup the view and initialize authentication
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_log_in);
        auth = FirebaseAuth.getInstance();
        initializeViews();
        handleLogOutIfLoggedIn();
    }

    /**
     * make sure the user is loggedout
     */
    private void handleLogOutIfLoggedIn(){
        //this method will sign the user out if logged in and do nothing if already logged out
        auth.signOut();
    }

    /**
     * initialize the text fields and buttons
     */
    private void initializeViews(){
        emailEditText = findViewById(R.id.email_input);
        passwordEditText = findViewById(R.id.password_input);
        logInButton = findViewById(R.id.log_in_button);
        logInButton.setEnabled(true);
        createAccountButton = findViewById(R.id.create_account_button);
        loadingBar = findViewById(R.id.initialization_progress_bar);
        loadingBar.setVisibility(View.INVISIBLE);
    }


    /**
     *  Handle the sign in proccess from the button
     * @param view
     */
    public void signIn(View view) {
        startLoading();
        //Make sure the info is valid
        if(!signInInvalid(emailEditText,passwordEditText)){

            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            //If the sign in is successful, return to the initialization activity
                            launchInitializationActivity();
                        } else {
                            //If it fails, toast error and reenable login button
                            Toast.makeText(this, "Authentication failed. Please verify your email and password and try again.",
                                    Toast.LENGTH_LONG).show();
                            stopLoading();
                        }
                    });
        }
        else{
            stopLoading();
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
        stopLoading();
        Intent intent = new Intent(this, AppInitializationActivity.class);
        startActivity(intent);
    }

    /**
     * Transition the app to the account creation activity
     * @param view view passed by button press
     */
    public void launchAccountCreationActivity(View view){
        startLoading();
        Intent intent = new Intent(this, AccountCreationActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
        stopLoading();
    }

    /**
     * display the password reset dialog
     * @param view
     */
    public void openPasswordResetDialog(View view) {
        ForgotPasswordDialog forgotPasswordDialog = new ForgotPasswordDialog(this);
        forgotPasswordDialog.show();
    }

    private void startLoading(){
        loadingBar.setVisibility(View.VISIBLE);
        logInButton.setEnabled(false);
        createAccountButton.setEnabled(false);
    }

    private void stopLoading(){
        loadingBar.setVisibility(View.INVISIBLE);
        logInButton.setEnabled(true);
        createAccountButton.setEnabled(true);
    }

}