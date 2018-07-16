package com.hcrpurdue.jason.hcrhousepoints;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Authentication extends AppCompatActivity {
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null)
            return; // TODO: Go to the logged in page
        // TODO:  Pull house info and floor codes
    }

    public void signIn(View view) {
        EditText email = findViewById(R.id.emailInput);
        EditText password = findViewById(R.id.passwordInput);
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();

        if (signInInvalid(email, password))
            return;

        auth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            // TODO: Go to next page
                        } else {
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    public void signUp(View view) {
        EditText email = findViewById(R.id.emailInput);
        EditText password = findViewById(R.id.passwordInput);
        EditText passwordConfirm = findViewById(R.id.confirmPasswordInput);
        EditText name = findViewById(R.id.nameInput);
        EditText floorCode = findViewById(R.id.floorCodeInput);
        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();
        String passwordConfirmText = passwordConfirm.getText().toString();
        String nameText = name.getText().toString();
        String floorCodeText = floorCode.getText().toString();


        if (signInInvalid(email, password) || signUpInvalid(password, passwordConfirm, name, floorCode))
            return;

        auth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            // TODO: Show rest of stuff after signed in
                        } else {
                            Toast.makeText(getApplicationContext(), "Authentication failed, try again later before contacting your RHP",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean signInInvalid(EditText email, EditText password) {
        // Hide the virtual keyboard
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputManager != null && getCurrentFocus() != null) // Avoids null pointer exceptions
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

        // Checks the email text, makes sure it's a valid email address, and makes sure its a Purdue address
        String emailText = email.getText().toString();
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
        if(passwordText.length() < 6){
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
        if(!nameText.contains(" ")){
            Toast.makeText(getApplicationContext(), "Name must include your first and last name", Toast.LENGTH_SHORT).show();
            return true;
        }

        //TODO: Check that FloorCode matches a valid code

        return false;
    }
}
