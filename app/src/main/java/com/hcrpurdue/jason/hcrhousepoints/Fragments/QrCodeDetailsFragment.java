package com.hcrpurdue.jason.hcrhousepoints.Fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.hcrpurdue.jason.hcrhousepoints.Models.Link;
import com.hcrpurdue.jason.hcrhousepoints.QRCodeCEView;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CapturePhotoUtils;
import com.hcrpurdue.jason.hcrhousepoints.Utils.QRCodeUtil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.hcrpurdue.jason.hcrhousepoints.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class QrCodeDetailsFragment extends AppCompatActivity{
    private TextView pointTypeLabel;
    private ImageView qrCodeImageView;
    private TextView pointDescriptionLabel;
    private ImageButton editButton;
    private ImageButton shareButton;
    private Switch isEnabledSwitch;
    boolean dialogIsVisible = false;
    private Dialog shareDialog;
    private Bitmap qrCodeMap;
    private BottomSheetBehavior mBottomSheetBehaviour;
    private Link qrCodeModel;

    private QRCodeCEView qrCodeCEView;

    /**
     * When the activity is created, run this code
     * @param savedInstanceState    - Any saved information from previous instances
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_qr_code_expanded);

        qrCodeModel = (Link) getIntent().getExtras().getBundle("QRCODE").getSerializable("QRCODE");
        if(qrCodeModel == null){
            qrCodeModel = new Link("B8hlX08pQyJFk2mdqAYI", "Test Code", true, 1, true, true);
        }

        View gestureView = findViewById(R.id.gesture_recognizer_view);
        gestureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideBottomStatusBar(view);
                InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                if(inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0)) {

                }
                else if(dialogIsVisible){
                    shareDialog.dismiss();
                    dialogIsVisible = false;
                }
                else if(mBottomSheetBehaviour.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
                else {
                    mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        initializeUIElements();
        hideBottomStatusBar(getWindow().getDecorView());

        View editView = findViewById(R.id.info_edit_bottom_sheet);
        mBottomSheetBehaviour = BottomSheetBehavior.from(editView);
        shareDialog = createShareDialog();
        qrCodeCEView = new QRCodeCEView(this, editView, qrCodeModel);

    }

    public Dialog createShareDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Share Options")
            .setItems(R.array.share_options, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // The 'which' argument contains the index position
                    // of the selected item
                    switch (which){
                        case 0:
                            copyAndroidLink();
                            break;
                        case 1:
                            copyIOSLink();
                            break;
                        case 2:
                            saveImage();
                            break;
                        default:
                            showSystemShareOptions();
                    }
                }
            });
        Dialog dialog = builder.create();
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        dialog.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        return dialog;
    }



    /**
     * Initializes UIElements in the View
     *
     */
    private void initializeUIElements(){
        qrCodeImageView = findViewById(R.id.qr_code_image_view);


        pointDescriptionLabel = findViewById(R.id.qr_code_collapsed_point_description_label);
        pointTypeLabel = findViewById(R.id.qr_code_collapsed_point_type_label);
        isEnabledSwitch = findViewById(R.id.qr_code_collapsed_enabled_switch);
        editButton = findViewById(R.id.qr_code_collapsed_edit_button);
        shareButton = findViewById(R.id.qr_code_collapsed_share_button);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogIsVisible = true;
                shareDialog.show();
            }
        });

        pointTypeLabel.setText(qrCodeModel.getPointType(getApplicationContext()).getName());

        pointDescriptionLabel.setText(qrCodeModel.getDescription());
        isEnabledSwitch.setChecked(qrCodeModel.isEnabled());

        qrCodeMap = QRCodeUtil.generateQRCodeFromString(this,qrCodeModel.getAddress());
        if(qrCodeMap != null){
            qrCodeImageView.setImageBitmap(qrCodeMap);
        }
        else{
            Toast.makeText(getApplicationContext(),"Could not create QR Code",Toast.LENGTH_LONG).show();
        }
    }

    private void copyIOSLink(){
        ClipboardManager cm = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData cData = ClipData.newPlainText("iOS Link",qrCodeModel.getAddress());
        cm.setPrimaryClip(cData);
        Toast.makeText(this, "iOS Link Copied", Toast.LENGTH_SHORT).show();
    }

    private void copyAndroidLink(){
        ClipboardManager cm = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData cData = ClipData.newPlainText("Android Link",qrCodeModel.getAndroidDeepLinkAddress());
        cm.setPrimaryClip(cData);
        Toast.makeText(this, "Android Link Copied", Toast.LENGTH_SHORT).show();
    }

    private void saveImage(){
        CapturePhotoUtils capturePhotoUtils = new CapturePhotoUtils();
        if(capturePhotoUtils.insertImage(this, qrCodeMap, qrCodeModel.getDescription(), qrCodeModel.getDescription())){
            Toast.makeText(this, "Photo Saved", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, "Could not save. Please check permissions.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Saves the image as PNG to the app's cache directory.
     * @return Uri of the saved file or null
     */
    private Uri saveImageToCache() {
        //TODO - Should be processed in another thread
        File imagesFolder = new File(getCacheDir(), "images");
        Uri uri = null;
        try {
            imagesFolder.mkdirs();
            File file = new File(imagesFolder, "shared_image.png");

            FileOutputStream stream = new FileOutputStream(file);
            qrCodeMap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(this, "com.hcrpurdue.jason.hcrhousepoints", file);

        } catch (IOException e) {
            System.out.println( "IOException while trying to write file for sharing: " + e.getMessage());
        }
        return uri;
    }

    private void showSystemShareOptions(){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, saveImageToCache());
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, "Share Image"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideBottomStatusBar(getWindow().getDecorView());
    }

    private void hideBottomStatusBar(View decorView){
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

}
