/**
 * AccountCreationActivity - This class holds the controller for Account Creation.
 *
 *  On sign-up button press, check the validity of the fields and create the account with Firestore.
 *  After the account is created, transition to the activity to sign up for a house.
 *
 */

package com.hcrpurdue.jason.hcrhousepoints.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.hcrpurdue.jason.hcrhousepoints.R;

public class AccountCreationActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button signUpButton;
    private ProgressBar loadingBar;

    /**
     * Called when the Activity is created.
     * @param savedInstanceState When any state information is passed in through a bundle, it will be found here
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_create_account);
        initializeViews();

    }

    /**
     * Connect the views to the local class variables
     */
    private void initializeViews(){
        emailEditText = findViewById(R.id.email_input);
        passwordEditText = findViewById(R.id.password_input);
        confirmPasswordEditText = findViewById(R.id.confirm_password_input);
        signUpButton = findViewById(R.id.sign_up_button);
        loadingBar = findViewById(R.id.initialization_progress_bar);
        loadingBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Method that the sign up button calls to crete the account in Firestore
     * @param view
     */
    public void signUp(View view) {
        //Disable the button to prevent spamming
        startLoading();

        //Get the text values
        final String emailText = emailEditText.getText().toString();
        final String passwordText = passwordEditText.getText().toString();
        final String confirmPasswordText = confirmPasswordEditText.getText().toString();

        if (isAccountCreationDataValid(emailText,passwordText,confirmPasswordText)){
            //Use Firestore Auth to create the user
            auth.createUserWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            //Account successfully created. Transition to house sign up.
                            launchHouseSignUpActivity();
                        } else {
                            //If there is a problem display a toast.
                            Toast.makeText(this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            //Reenable the sign up button
                            stopLoading();
                        }
                    });
        }
        else{
            stopLoading();
        }
    }

    /**
     *  Verifies that the provided information is correct. Returns true if all is valid
     * @param emailAddress  Email address for which to create an account
     * @param password      Password for which to create an account
     * @param confirmPassword   Password retyped to ensure correct password
     * @return  true if all is valid
     */
    private boolean isAccountCreationDataValid(String emailAddress, String password, String confirmPassword){
        // Hide the virtual keyboard
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null && getCurrentFocus() != null) // Avoids null pointer exceptions
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

        // Checks the email text, makes sure it's a valid email address, and makes sure its a Purdue address
        String emailText = emailAddress.toLowerCase();
        if (TextUtils.isEmpty(emailText) || !Patterns.EMAIL_ADDRESS.matcher(emailText).matches() || !emailText.matches("[A-Z0-9a-z._%+-]+@purdue\\.edu")) {
            Toast.makeText(this, "The email must be a valid Purdue email address.", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Checks the password text
        else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();
            return false;
        }
        //ensure the password has appropriate length
        else if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long.", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Checks that the passwords match
        else if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "The passwords do not match.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            //Everything checks out. Create the account
            return true;
        }
    }

    /**
     * Transition to House Sign Up activity
     */
    private void launchHouseSignUpActivity(){
        stopLoading();
        Intent intent = new Intent(this, HouseSignUpActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
    }

    /**
     * If the back button on the bottom bar is pressed, transition the app back to the previous page
     */
    @Override
    public void onBackPressed() {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_reverse,R.anim.slide_out_reverse);

    }

    private void startLoading(){
        loadingBar.setVisibility(View.VISIBLE);
        signUpButton.setEnabled(false);
    }

    private void stopLoading(){
        loadingBar.setVisibility(View.INVISIBLE);
        signUpButton.setEnabled(true);
    }
}
