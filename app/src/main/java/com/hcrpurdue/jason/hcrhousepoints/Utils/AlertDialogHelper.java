package com.hcrpurdue.jason.hcrhousepoints.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.hcrpurdue.jason.hcrhousepoints.Models.Link;
import com.hcrpurdue.jason.hcrhousepoints.R;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.AlertDialogInterface;

import java.util.Date;

public class AlertDialogHelper {

    /**
     * Create a Material Design Alert Dialog with a single button
     *
     * @param activity
     * @param title
     * @param message
     * @param buttonText
     */
    public static Dialog showSingleButtonDialog(Activity activity, String title, String message, String buttonText, AlertDialogInterface alertDialogInterface){

        return new SingleButtonDialog(activity, title, message, buttonText, alertDialogInterface);
    }

    public static Dialog showDoubleButtonDialog(Activity activity, String title, String message, String positiveText, String negativeText, AlertDialogInterface alertDialogInterface){

        return new DoubleButtonDialog(activity, title, message, positiveText, negativeText, alertDialogInterface);
    }

    public static Dialog showQRSubmissionDialog(Activity activity, Link link, AlertDialogInterface alertDialogInterface){

        return new QRSubmissionDialog(activity, link, alertDialogInterface);
    }
}


class SingleButtonDialog extends AlertDialog{
    SingleButtonDialog(Activity activity, String title, String message, String buttonText, AlertDialogInterface alertDialogInterface){
        super(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_single_button, null);

        TextView titleTextView = view.findViewById(R.id.title);
        TextView messageTextView = view.findViewById(R.id.description_text_view);
        Button completeButton = view.findViewById(R.id.complete_button);

        titleTextView.setText(title);
        messageTextView.setText(message);
        completeButton.setText(buttonText);


        completeButton.setOnClickListener(view1 -> {
            if(alertDialogInterface != null)
                alertDialogInterface.onPositiveButtonListener();
            dismiss();
        });
        setCancelable(false);
        setView(view);
    }
}

class DoubleButtonDialog extends AlertDialog{
    DoubleButtonDialog(Activity activity, String title, String message, String positiveButtonText, String negativeButtonText, AlertDialogInterface alertDialogInterface){
        super(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_double_button, null);

        TextView titleTextView = view.findViewById(R.id.title);
        TextView messageTextView = view.findViewById(R.id.description_text_view);
        Button positiveButton = view.findViewById(R.id.positive_button);
        Button negativeButton = view.findViewById(R.id.negative_button);

        titleTextView.setText(title);
        messageTextView.setText(message);
        positiveButton.setText(positiveButtonText);
        negativeButton.setText(negativeButtonText);


        positiveButton.setOnClickListener(view1 -> {
            if(alertDialogInterface != null)
                alertDialogInterface.onPositiveButtonListener();
            dismiss();
        });

        negativeButton.setOnClickListener(view1 -> {
            if(alertDialogInterface != null)
                alertDialogInterface.onNegativeButtonListener();
            dismiss();
        });

        setCancelable(false);
        setView(view);
    }
}

class QRSubmissionDialog extends AlertDialog{
    QRSubmissionDialog(Activity activity, Link link, AlertDialogInterface alertDialogInterface){
        super(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_qr_submission, null);

        TextView nameTextView = view.findViewById(R.id.qr_code_cell_point_type_name_text_view);
        TextView descriptionTextView = view.findViewById(R.id.description_text_view);
        TextView monthView = view.findViewById(R.id.month_text_view);
        TextView dateView = view.findViewById(R.id.month_date_view);

        Button positiveButton = view.findViewById(R.id.positive_button);
        Button negativeButton = view.findViewById(R.id.negative_button);


        nameTextView.setText(link.getPointType(getContext()).getName());
        descriptionTextView.setText(link.getDescription());
        monthView.setText(DateFormat.format("MMM",new Date()));
        dateView.setText(DateFormat.format("dd",new Date()));


        positiveButton.setOnClickListener(view1 -> {
            if(alertDialogInterface != null)
                alertDialogInterface.onPositiveButtonListener();
            dismiss();
        });

        negativeButton.setOnClickListener(view1 -> {
            if(alertDialogInterface != null)
                alertDialogInterface.onNegativeButtonListener();
            dismiss();
        });

        setCancelable(false);
        setView(view);
    }
}