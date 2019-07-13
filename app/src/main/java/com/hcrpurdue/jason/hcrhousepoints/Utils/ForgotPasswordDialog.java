package com.hcrpurdue.jason.hcrhousepoints.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.hcrpurdue.jason.hcrhousepoints.R;

public class ForgotPasswordDialog{

    private EditText userEmailEditText;
    private boolean isValidEmail = false;
    private String userEmail = "";
    private FirebaseAuth auth;
    private Dialog dialog;

    public ForgotPasswordDialog(Activity callingActivity){

        auth = FirebaseAuth.getInstance();

        AlertDialog.Builder builder = new AlertDialog.Builder(callingActivity);

        LayoutInflater inflater = callingActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_layout_reset_pass, null);
        userEmailEditText = view.findViewById(R.id.forgot_email_input);

        builder.setView(view)
                .setTitle("Reset Password")
                .setNegativeButton("Cancel", (dialog, which) -> {
                })
                .setPositiveButton("Ok", null);
        dialog = builder.create();
        createOkButton();
    }

    public void show(){
        dialog.show();
    }

    private void createOkButton(){
        dialog.setOnShowListener(dialogInterface -> {

            Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                userEmail = userEmailEditText.getText().toString().trim();
                if(TextUtils.isEmpty(userEmail)) {
                    Toast.makeText(dialog.getContext(), "Please enter an email address", Toast.LENGTH_SHORT).show();
                }
                else {
                    auth.sendPasswordResetEmail(userEmail).addOnCompleteListener(task -> {
                        if(!task.isSuccessful()){
                            Toast.makeText(dialog.getContext(), "We were unable to find your email address.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(dialog.getContext(), "Check your inbox for a link to reset your password.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                }
            });
        });
    }
}
