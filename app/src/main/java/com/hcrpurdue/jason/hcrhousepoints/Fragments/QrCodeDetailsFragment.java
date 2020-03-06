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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.hcrpurdue.jason.hcrhousepoints.Models.Link;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointType;
import com.hcrpurdue.jason.hcrhousepoints.Models.ResponseCodeMessage;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CapturePhotoUtils;
import com.hcrpurdue.jason.hcrhousepoints.Utils.QRCodeUtil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.hcrpurdue.jason.hcrhousepoints.R;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.CacheManagementInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QrCodeDetailsFragment extends AppCompatActivity{
    private CacheManager cacheManager;
    private BottomSheetBehavior mBottomSheetBehaviour;
    private Dialog shareDialog;
    private Bitmap qrCodeMap;
    private Link qrCodeModel;


    //Main QR Code View
    private ImageView qrCodeImageView;

    //Bottom Bar Views
    private Spinner pointTypeSpinner;
    private EditText pointDescriptionEditText;
    private ImageButton shareButton;
    private Switch isEnabledSwitch;

    private Switch isMultiUseSwitch;
    private Switch isArchivedSwitch;
    private Button updateButton;
    private ProgressBar loadingSymbol;

    //Important Variables
    private ArrayList<PointType> enabledTypes = new ArrayList<PointType>();
    private boolean dialogIsVisible = false;






    /**
     * When the activity is created, run this code
     * @param savedInstanceState    - Any saved information from previous instances
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cacheManager = CacheManager.getInstance(this);
        setContentView(R.layout.fragment_qr_code_expanded);

        qrCodeModel = (Link) getIntent().getExtras().getBundle("QRCODE").getSerializable("QRCODE");
        if(qrCodeModel == null){
            qrCodeModel = new Link("B8hlX08pQyJFk2mdqAYI", "Test Code", true, 1, true, true);
        }



        initializeUIElements();

        View editView = findViewById(R.id.info_edit_bottom_sheet);
        mBottomSheetBehaviour = BottomSheetBehavior.from(editView);
        mBottomSheetBehaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                if(newState == BottomSheetBehavior.STATE_EXPANDED){
                    pointTypeSpinner.setEnabled(true);
                    pointDescriptionEditText.setEnabled(true);
                    pointTypeSpinner.setBackgroundTintList(getResources().getColorStateList(R.color.black));
                }
                else if(newState == BottomSheetBehavior.STATE_COLLAPSED){
                    pointTypeSpinner.setEnabled(false);
                    pointDescriptionEditText.setEnabled(false);
                    pointTypeSpinner.setBackgroundTintList(getResources().getColorStateList(R.color.white));
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
        shareDialog = createShareDialog();

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
        return dialog;
    }



    /**
     * Initializes UIElements in the View
     *
     */
    private void initializeUIElements(){
        qrCodeImageView = findViewById(R.id.qr_code_image_view);


        pointDescriptionEditText = findViewById(R.id.qr_code_description_edit_text);
        pointTypeSpinner = findViewById(R.id.qr_code_edit_point_type_spinner);
        isEnabledSwitch = findViewById(R.id.qr_code_collapsed_enabled_switch);

        isMultiUseSwitch = findViewById(R.id.qr_code_edit_multi_use_switch);
        isArchivedSwitch = findViewById(R.id.qr_code_edit_archive_switch);
        updateButton = findViewById(R.id.qr_code_edit_update_button);
        shareButton = findViewById(R.id.qr_code_collapsed_share_button);
        loadingSymbol = findViewById(R.id.qr_code_edit_loading_symbol);

        pointTypeSpinner.setEnabled(false);
        pointDescriptionEditText.setEnabled(false);

        View gestureView = findViewById(R.id.gesture_recognizer_view);
        gestureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogIsVisible = true;
                shareDialog.show();
            }
        });

        loadSpinner(cacheManager.getPointTypeList());


        pointDescriptionEditText.setText(qrCodeModel.getDescription());
        isEnabledSwitch.setChecked(qrCodeModel.isEnabled());
        isMultiUseSwitch.setChecked(!qrCodeModel.isSingleUse());
        isArchivedSwitch.setChecked(qrCodeModel.isArchived());


        qrCodeMap = QRCodeUtil.generateQRCodeWithActivityFromString(this,qrCodeModel.getAddress());
        if(qrCodeMap != null){
            qrCodeImageView.setImageBitmap(qrCodeMap);
        }
        else{
            Toast.makeText(getApplicationContext(),"Could not create QR Code",Toast.LENGTH_LONG).show();
        }

        isEnabledSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEnabledStatus();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateButton.setEnabled(false);
                loadingSymbol.setVisibility(View.VISIBLE);
                Map<String, Object> data = getUpdatedData();
                if(data.keySet().size() > 0){
                    cacheManager.updateQRCode(qrCodeModel.getLinkId(), data, new CacheManagementInterface() {
                        @Override
                        public void onHttpSuccess(ResponseCodeMessage responseCodeMessage) {
                            updateButton.setEnabled(true);
                            loadingSymbol.setVisibility(View.INVISIBLE);
                            qrCodeModel.setHttpUpdates(data);
                            mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }

                        @Override
                        public void onError(Exception e, Context context) {
                            System.out.println("ERROR IN CREATING: "+e.getLocalizedMessage());
                            Toast.makeText(context,"Could not create QR code",Toast.LENGTH_LONG).show();
                            updateButton.setEnabled(true);
                            loadingSymbol.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onHttpError(ResponseCodeMessage responseCodeMessage) {
                            System.out.println("HTTP ERROR: "+responseCodeMessage.getMessage());
                            Toast.makeText(getApplicationContext(),"Could not create QR code",Toast.LENGTH_LONG).show();
                            updateButton.setEnabled(true);
                            loadingSymbol.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                else{
                    updateButton.setEnabled(true);
                    loadingSymbol.setVisibility(View.INVISIBLE);
                }
            }
        });
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

    private void setEnabledStatus(){
        if(!dialogIsVisible){
            HashMap<String, Object> data = new HashMap<>();
            data.put("is_enabled", isEnabledSwitch.isChecked());
            cacheManager.updateQRCode(qrCodeModel.getLinkId(), data, new CacheManagementInterface() {
                @Override
                public void onHttpSuccess(ResponseCodeMessage responseCodeMessage) {
                    qrCodeModel.setHttpUpdates(data);
                }

                @Override
                public void onError(Exception e, Context context) {
                    System.out.println("ERROR IN CREATING: "+e.getLocalizedMessage());
                    Toast.makeText(context,"Could not create QR code",Toast.LENGTH_LONG).show();
                    isEnabledSwitch.setChecked(!isEnabledSwitch.isChecked());
                }

                @Override
                public void onHttpError(ResponseCodeMessage responseCodeMessage) {
                    System.out.println("HTTP ERROR: "+responseCodeMessage.getMessage());
                    Toast.makeText(getApplicationContext(),"Could not create QR code",Toast.LENGTH_LONG).show();
                    isEnabledSwitch.setChecked(!isEnabledSwitch.isChecked());
                }
            });
        }
    }


    /**
     * Puts the PointType list into the Spinner
     * @param types List of PointType objects to put into the spinner
     */
    private void loadSpinner(List<PointType> types){
        enabledTypes = new ArrayList<>();
        PointType selectedPointType = null;


        List<Map<String, String>> formattedPointTypes = new ArrayList<>();
        Map<String, String> placeholder = new HashMap<>();
        for (PointType type : types) {
            if (type.getUserCanGenerateQRCodes(cacheManager.getPermissionLevel()) && type.isEnabled()) {
                enabledTypes.add(type);
                Map<String, String> map = new HashMap<>();
                map.put("text", type.getName());
                map.put("subText", type.getValue() + " point" + ((type.getValue() == 1)? "":"s"));
                formattedPointTypes.add(map);
                if(qrCodeModel != null && qrCodeModel.getPointTypeId() == type.getId()){
                    selectedPointType = type;
                }
            }
        }
        SimpleAdapter adapter = new SimpleAdapter(this, formattedPointTypes, android.R.layout.simple_list_item_2, new String[]{"text", "subText"}, new int[]{android.R.id.text1, android.R.id.text2});
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_2);
        pointTypeSpinner.setAdapter(adapter);
        if(selectedPointType != null){
            pointTypeSpinner.setSelection(enabledTypes.indexOf(selectedPointType));
        }
    }

    private Map<String, Object> getUpdatedData(){
        Map<String, Object> data = new HashMap<>();
        if(enabledTypes.get(pointTypeSpinner.getSelectedItemPosition()).getId() != qrCodeModel.getPointTypeId()){
            System.out.println("New Point Id");
            data.put("point_id", enabledTypes.get(pointTypeSpinner.getSelectedItemPosition()).getId());
        }
        if(!pointDescriptionEditText.getText().toString().equals(qrCodeModel.getDescription())){
            System.out.println("New description");
            data.put("description", pointDescriptionEditText.getText().toString());
        }
        if(isEnabledSwitch.isChecked() != qrCodeModel.isEnabled()){
            System.out.println("New eanabled");
            data.put("is_enabled", isEnabledSwitch.isChecked());
        }
        if(isArchivedSwitch.isChecked() != qrCodeModel.isArchived()){
            System.out.println("New archived");
            data.put("is_archived" , isArchivedSwitch.isChecked() );
        }
        if(isMultiUseSwitch.isChecked() == qrCodeModel.isSingleUse()){
            System.out.println("New single use");
            data.put("single_use", !isMultiUseSwitch.isChecked());
        }
        return data;
    }

}
