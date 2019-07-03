package com.hcrpurdue.jason.hcrhousepoints.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.hcrpurdue.jason.hcrhousepoints.R;

public class PasswordResetDialogFragment extends AppCompatDialogFragment {

    private EditText passwordEntryEditText;
    // private PassResetDialogListener listener;
    private String userEmail = "";
    private boolean isValidEmail = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_layout_reset_pass, null);

        builder.setView(view)
                .setTitle("Reset Password")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userEmail = passwordEntryEditText.getText().toString().trim();


                        if(TextUtils.isEmpty(userEmail)) {
                            Toast.makeText(getContext(), "Please enter an email address", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        isValidEmail = true;

                        /* TODO: Implement check to see if email is in Firebase */
                    }

                });

        passwordEntryEditText = view.findViewById(R.id.forgot_password);
        return builder.create();
    }

    public boolean emailValid() {
        return isValidEmail;
    }

    public String getUserEmail() {
        return userEmail;
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//
//        try {
//            listener = (PassResetDialogListener) context;
//        } catch (ClassCastException e) {
//            Toast.makeText(context, "Class Casting Error", Toast.LENGTH_SHORT).show();
//        }
//    }

//    public interface PassResetDialogListener {
//        void applyTexts(String email);
//    }

}
