package com.hcrpurdue.jason.hcrhousepoints.Utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

import static androidx.constraintlayout.motion.widget.MotionScene.TAG;

public class QRCodeUtil {

    public static Bitmap generateSmallQRCodeFromString(String url){


        Bitmap bitmap = null;

        int height = 50;
        int width = 50;

        int smallerDimension = Math.min(width, height);
        smallerDimension = smallerDimension * 3 / 4;

        QRGEncoder qrgEncoder = new QRGEncoder(url, null, QRGContents.Type.TEXT, smallerDimension);

        try {
            bitmap = qrgEncoder.encodeAsBitmap();
        } catch (WriterException e) {
            Log.v(TAG, e.toString());
        }
        return bitmap;
    }

    public static Bitmap generateQRCodeWithActivityFromString(Activity activity, String url){


        Bitmap bitmap = null;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        int smallerDimension = Math.min(width, height);
        smallerDimension = smallerDimension * 3 / 4;

        QRGEncoder qrgEncoder = new QRGEncoder(url, null, QRGContents.Type.TEXT, smallerDimension);

        try {
            bitmap = qrgEncoder.encodeAsBitmap();
        } catch (WriterException e) {
            Log.v(TAG, e.toString());
        }
        return bitmap;
    }
}
