package com.hcrpurdue.jason.hcrhousepoints.ListAdapters;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.hcrpurdue.jason.hcrhousepoints.Activities.NavigationActivity;
import com.hcrpurdue.jason.hcrhousepoints.Fragments.QrCodeDetailsFragment;
import com.hcrpurdue.jason.hcrhousepoints.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.hcrpurdue.jason.hcrhousepoints.Models.Link;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CapturePhotoUtils;
import com.hcrpurdue.jason.hcrhousepoints.Utils.QRCodeUtil;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.CacheManagementInterface;

public class QrCodeListAdapter extends BaseAdapter implements ListAdapter {

    private List<Link> qrCodeList;
    private Context context;

    public QrCodeListAdapter(List<Link> qrCodeList, Context context){
        this.qrCodeList = qrCodeList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return qrCodeList.size();
    }

    @Override
    public Object getItem(int i) {
        return qrCodeList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Link qrCode = qrCodeList.get(position);
        View view = convertView;
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Objects.requireNonNull(inflater);
            view = inflater.inflate(R.layout.list_item_qr_code, parent, false);
        }

        TextView titleTextView = view.findViewById(R.id.qr_code_cell_description_text_view);
        titleTextView.setText(qrCode.getDescription());

        TextView typeTextView = view.findViewById(R.id.qr_code_cell_point_type_name_text_view);
        typeTextView.setText(qrCode.getPointType(context).getName());
        Switch codeActiveSwitch = view.findViewById(R.id.qr_code_cell_enabled_switch);
        codeActiveSwitch.setChecked(qrCode.isEnabled());

        ImageView qrImageView = view.findViewById(R.id.qr_code_cell_image_view);
        Bitmap qrCodemap = QRCodeUtil.generateSmallQRCodeFromString(qrCode.getAddress());
        if(qrCodemap != null){
            qrImageView.setImageBitmap(qrCodemap);
        }
        else{
            Toast.makeText(context,"Could not create QR Code",Toast.LENGTH_LONG).show();
        }

        codeActiveSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CacheManager.getInstance(context).setQRCodeEnabledStatus(qrCodeList.get(position), codeActiveSwitch.isChecked(), new CacheManagementInterface() {
                    @Override
                    public void onSuccess() {
                        if (codeActiveSwitch.isChecked()) {
                            Toast.makeText(context, "Code has been activated", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "Code has been deactivated", Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onError(Exception e, Context context) {
                        codeActiveSwitch.setChecked(!codeActiveSwitch.isChecked());
                        qrCodeList.get(position).setEnabled(!codeActiveSwitch.isChecked());
                        Toast.makeText(context, "Code could not be updated.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        ImageButton shareButton = view.findViewById(R.id.qr_code_cell_share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createShareDialog(context, qrCode).show();
            }
        });




        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(context, QrCodeDetailsFragment.class );
                Bundle args = new Bundle();
                args.putSerializable("QRCODE", qrCode);
                intent.putExtra("QRCODE",args);
                context.startActivity(intent);
                ((AppCompatActivity) context).overridePendingTransition(R.anim.fade_in,R.anim.fade_out);

            }
        });

        return view;
    }

    private void copyIOSLink(Context context, Link qrCodeModel){
        ClipboardManager cm = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData cData = ClipData.newPlainText("iOS Link",qrCodeModel.getAddress());
        cm.setPrimaryClip(cData);
        Toast.makeText(context, "iOS Link Copied", Toast.LENGTH_SHORT).show();
    }

    private void copyAndroidLink(Context context, Link qrCodeModel){
        ClipboardManager cm = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData cData = ClipData.newPlainText("Android Link",qrCodeModel.getAndroidDeepLinkAddress());
        cm.setPrimaryClip(cData);
        Toast.makeText(context, "Android Link Copied", Toast.LENGTH_SHORT).show();
    }

    private void saveImage(Context context, Link qrCodeModel, Bitmap qrCodeMap){
        CapturePhotoUtils capturePhotoUtils = new CapturePhotoUtils();
        if(capturePhotoUtils.insertImage((Activity) context, qrCodeMap, qrCodeModel.getDescription(), qrCodeModel.getDescription())){
            Toast.makeText(context, "Photo Saved", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(context, "Could not save. Please check permissions.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Saves the image as PNG to the app's cache directory.
     * @return Uri of the saved file or null
     */
    private Uri saveImageToCache(Context context, Bitmap qrCodeMap) {
        File imagesFolder = new File(((Activity) context).getCacheDir(), "images");
        Uri uri = null;
        try {
            imagesFolder.mkdirs();
            File file = new File(imagesFolder, "shared_image.png");

            FileOutputStream stream = new FileOutputStream(file);
            qrCodeMap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(context, "com.hcrpurdue.jason.hcrhousepoints", file);

        } catch (IOException e) {
            System.out.println( "IOException while trying to write file for sharing: " + e.getMessage());
        }
        return uri;
    }

    private void showSystemShareOptions(Context context, Link qrCodeModel){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        Bitmap bitmap = QRCodeUtil.generateQRCodeWithActivityFromString((Activity) context, qrCodeModel.getAddress());
        shareIntent.putExtra(Intent.EXTRA_STREAM, saveImageToCache(context, bitmap));
        shareIntent.setType("image/jpeg");
        context.startActivity(Intent.createChooser(shareIntent, "Share Image"));
    }

    public Dialog createShareDialog(Context context, Link qrCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        Bitmap bitmap = QRCodeUtil.generateQRCodeWithActivityFromString((Activity) context, qrCode.getAddress());
        builder.setTitle("Share Options")
                .setItems(R.array.share_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which){
                            case 0:
                                copyAndroidLink(context,qrCode);
                                break;
                            case 1:
                                copyIOSLink(context, qrCode);
                                break;
                            case 2:
                                saveImage(context, qrCode, bitmap);
                                break;
                            default:
                                showSystemShareOptions(context, qrCode);
                        }
                    }
                });
        Dialog dialog = builder.create();
        return dialog;
    }


}
