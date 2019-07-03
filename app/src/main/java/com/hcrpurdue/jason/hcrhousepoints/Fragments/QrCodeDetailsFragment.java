package com.hcrpurdue.jason.hcrhousepoints.Fragments;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Objects;

import com.hcrpurdue.jason.hcrhousepoints.Models.Link;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CapturePhotoUtils;
import com.hcrpurdue.jason.hcrhousepoints.Utils.QRCodeUtil;
import com.hcrpurdue.jason.hcrhousepoints.Utils.Singleton;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.SingletonInterface;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.hcrpurdue.jason.hcrhousepoints.R;

public class QrCodeDetailsFragment extends Fragment {
    AppCompatActivity activity;
    Context context;
    TextView pointTypeLabel;
    ImageView qrCodeImageView;
    TextView qrCodeIOSURLLabel;
    TextView qrCodeAndroidURLLabel;
    TextView pointDescriptionLabel;
    Button saveToPhotosButton;
    Switch isEnabledSwitch;
    Switch isArchivedSwitch;

    Link qrCodeModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_qr_code_details, container, false);

        retrieveBundleData();
        initializeUIElements(view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (AppCompatActivity) getActivity();
        Objects.requireNonNull(activity.getSupportActionBar()).setTitle("QR Code");
    }


    /**
     * Attempts to retrieve a Link model from the Bundle. Call this when initializing the view.
     * To put values in the correct place, when you build a fragmentTransatction, put a Bundle into the fragment with fragment.setArguments(Bundle);
     */
    private void retrieveBundleData(){
        Bundle bundle = getArguments();
        if(bundle != null){
            qrCodeModel = (Link) bundle.getSerializable("QRCODE");
            if(qrCodeModel == null){
                qrCodeModel = new Link("B8hlX08pQyJFk2mdqAYI", "Test Code", true, 1, true, true);
            }
        }
    }

    /**
     * Initializes UIElements in the View
     *
     * @param view View where elements are to be initialized
     */
    private void initializeUIElements(View view){
        pointTypeLabel = view.findViewById(R.id.pointTypeDescriptionLabel);
        qrCodeImageView = view.findViewById(R.id.qrCodeImageView);
        qrCodeIOSURLLabel = view.findViewById(R.id.ios_url);
        qrCodeAndroidURLLabel = view.findViewById(R.id.android_url);
        pointDescriptionLabel = view.findViewById(R.id.pointLogDescriptionLabel);
        saveToPhotosButton = view.findViewById(R.id.saveToPhotosButton);
        isEnabledSwitch = view.findViewById(R.id.isEnabledSwitch);
        isArchivedSwitch = view.findViewById(R.id.isArchivedSwitch);

        saveToPhotosButton.setOnClickListener(view1 -> {
            view1.setEnabled(false);
            BitmapDrawable drawable = (BitmapDrawable) qrCodeImageView.getDrawable();
            Bitmap map = Bitmap.createBitmap(drawable.getBitmap());
            CapturePhotoUtils capturePhotoUtils = new CapturePhotoUtils();
            if(capturePhotoUtils.insertImage(getActivity(), map, qrCodeModel.getDescription(), qrCodeModel.getDescription())){
                Toast.makeText(context, "Photo Saved", Toast.LENGTH_LONG).show();
                view1.setEnabled(true);
            }
            else{
                Toast.makeText(context, "Could not save. Please check permissions.", Toast.LENGTH_LONG).show();
                view1.setEnabled(true);
            }

        });

        isEnabledSwitch.setOnClickListener(view1 -> {
            changeEnabledStatus(((Switch) view1).isChecked());
        });
        isArchivedSwitch.setOnClickListener(view1 -> {
            changeArchivedStatus(((Switch) view1).isChecked());
        });


        pointTypeLabel.setText(qrCodeModel.getPointType(context).getPointDescription());

        qrCodeIOSURLLabel.setOnClickListener(view12 -> {
            ClipboardManager cm = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData cData = ClipData.newPlainText("iOS Link",qrCodeModel.getAddress());
            cm.setPrimaryClip(cData);
            Toast.makeText(context, "iOS Link Copied", Toast.LENGTH_SHORT).show();
        });

        qrCodeAndroidURLLabel.setOnClickListener(view13 -> {
            ClipboardManager cm = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData cData = ClipData.newPlainText("Android Link",qrCodeModel.getAndroidDeepLinkAddress());
            cm.setPrimaryClip(cData);
            Toast.makeText(context, "Android Link Copied", Toast.LENGTH_SHORT).show();
        });

        pointDescriptionLabel.setText(qrCodeModel.getDescription());
        isEnabledSwitch.setChecked(qrCodeModel.isEnabled());
        isArchivedSwitch.setChecked(qrCodeModel.isArchived());

        Bitmap qrCode = QRCodeUtil.generateQRCodeFromString(getActivity(),qrCodeModel.getAddress());
        if(qrCode != null){
            qrCodeImageView.setImageBitmap(qrCode);
        }
        else{
            Toast.makeText(context,"Could not create QR Code",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Update the server and model with the new changes
     * @param isEnabled
     */
    private void changeEnabledStatus(Boolean isEnabled){

        Singleton.getInstance(context).setQRCodeEnabledStatus(qrCodeModel, isEnabled, new SingletonInterface() {
            @Override
            public void onSuccess() {
                if(isEnabled){
                    Toast.makeText(context, "Code has been activated", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(context, "Code has been deactivated", Toast.LENGTH_LONG).show();
                }
                qrCodeModel.setEnabled(isEnabled);
            }

            @Override
            public void onError(Exception e, Context context) {
                isEnabledSwitch.setChecked(!isEnabled);
                Toast.makeText(context, "Code could not be updated.", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Update the server and model with the new changes
     * @param isArchived
     */
    private void changeArchivedStatus(Boolean isArchived){
        Singleton.getInstance(context).setQRCodeArchivedStatus(qrCodeModel, isArchived, new SingletonInterface() {
            @Override
            public void onSuccess() {
                if(isArchived){
                    Toast.makeText(context, "Code has been archived", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(context, "Code has been unarchived", Toast.LENGTH_LONG).show();
                }
                qrCodeModel.setArchived(isArchived);
            }

            @Override
            public void onError(Exception e, Context context) {
                isArchivedSwitch.setChecked(!isArchived);
                Toast.makeText(context, "Code could not be updated.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
