package com.hcrpurdue.jason.hcrhousepoints.Fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.hcrpurdue.jason.hcrhousepoints.Models.Link;
import com.hcrpurdue.jason.hcrhousepoints.Utils.QRCodeUtil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.hcrpurdue.jason.hcrhousepoints.R;

public class QrCodeDetailsFragment extends AppCompatActivity{
    TextView pointTypeLabel;
    ImageView qrCodeImageView;
//    TextView qrCodeIOSURLLabel;
//    TextView qrCodeAndroidURLLabel;
    TextView pointDescriptionLabel;
    ImageButton editButton;
    Switch isEnabledSwitch;
//    Switch isArchivedSwitch;

//    View editThingy;

    private BottomSheetBehavior mBottomSheetBehaviour;
    Link qrCodeModel;

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
                if(mBottomSheetBehaviour.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
                else {
                    mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        initializeUIElements();
        View decorView = getWindow().getDecorView();
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

        View editView = findViewById(R.id.info_edit_bottom_sheet);
        mBottomSheetBehaviour = BottomSheetBehavior.from(editView);


    }



    //    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        super.onCreateView(inflater, container, savedInstanceState);
//        View view = inflater.inflate(R.layout.fragment_qr_code_expanded, container, false);
//
//        retrieveBundleData();
//        initializeUIElements(view);
//        view.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_IMMERSIVE
//                        // Set the content to appear under the system bars so that the
//                        // content doesn't resize when the system bars hide and show.
//                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        // Hide the nav bar and status bar
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
//
//        return view;
//    }
//
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        activity = (AppCompatActivity) getActivity();
//        Objects.requireNonNull(activity.getSupportActionBar()).setTitle("QR Code");
//    }


    /**
     * Attempts to retrieve a Link model from the Bundle. Call this when initializing the view.
     * To put values in the correct place, when you build a fragmentTransatction, put a Bundle into the fragment with fragment.setArguments(Bundle);
     */
    private void retrieveBundleData(){

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
//        qrCodeIOSURLLabel = view.findViewById(R.id.ios_url);
//        qrCodeAndroidURLLabel = view.findViewById(R.id.android_url);
//        pointDescriptionLabel = view.findViewById(R.id.pointLogDescriptionLabel);
//        saveToPhotosButton = view.findViewById(R.id.saveToPhotosButton);
//        isEnabledSwitch = view.findViewById(R.id.isEnabledSwitch);
//        isArchivedSwitch = view.findViewById(R.id.isArchivedSwitch);
//
//        saveToPhotosButton.setOnClickListener(view1 -> {
//            view1.setEnabled(false);
//            BitmapDrawable drawable = (BitmapDrawable) qrCodeImageView.getDrawable();
//            Bitmap map = Bitmap.createBitmap(drawable.getBitmap());
//            CapturePhotoUtils capturePhotoUtils = new CapturePhotoUtils();
//            if(capturePhotoUtils.insertImage(getActivity(), map, qrCodeModel.getDescription(), qrCodeModel.getDescription())){
//                Toast.makeText(context, "Photo Saved", Toast.LENGTH_LONG).show();
//                view1.setEnabled(true);
//            }
//            else{
//                Toast.makeText(context, "Could not save. Please check permissions.", Toast.LENGTH_LONG).show();
//                view1.setEnabled(true);
//            }
//
//        });
//
//        isEnabledSwitch.setOnClickListener(view1 -> {
//            changeEnabledStatus(((Switch) view1).isChecked());
//        });
//        isArchivedSwitch.setOnClickListener(view1 -> {
//            changeArchivedStatus(((Switch) view1).isChecked());
//        });
//
//

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                editThingy.setVisibility(View.VISIBLE);
                mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        pointTypeLabel.setText(qrCodeModel.getPointType(getApplicationContext()).getName());
//
//        qrCodeIOSURLLabel.setOnClickListener(view12 -> {
//            ClipboardManager cm = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
//            ClipData cData = ClipData.newPlainText("iOS Link",qrCodeModel.getAddress());
//            cm.setPrimaryClip(cData);
//            Toast.makeText(context, "iOS Link Copied", Toast.LENGTH_SHORT).show();
//        });
//
//        qrCodeAndroidURLLabel.setOnClickListener(view13 -> {
//            ClipboardManager cm = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
//            ClipData cData = ClipData.newPlainText("Android Link",qrCodeModel.getAndroidDeepLinkAddress());
//            cm.setPrimaryClip(cData);
//            Toast.makeText(context, "Android Link Copied", Toast.LENGTH_SHORT).show();
//        });
//
        pointDescriptionLabel.setText(qrCodeModel.getDescription());
        isEnabledSwitch.setChecked(qrCodeModel.isEnabled());
//        isArchivedSwitch.setChecked(qrCodeModel.isArchived());
//
        Bitmap qrCode = QRCodeUtil.generateQRCodeFromString(this,qrCodeModel.getAddress());
        if(qrCode != null){
            qrCodeImageView.setImageBitmap(qrCode);
        }
        else{
            Toast.makeText(getApplicationContext(),"Could not create QR Code",Toast.LENGTH_LONG).show();
        }
    }

}
