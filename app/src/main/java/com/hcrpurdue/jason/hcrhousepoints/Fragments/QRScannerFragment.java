package com.hcrpurdue.jason.hcrhousepoints.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.hcrpurdue.jason.hcrhousepoints.Activities.NavigationActivity;
import com.hcrpurdue.jason.hcrhousepoints.R;

import java.io.IOException;
import java.util.Objects;

import com.hcrpurdue.jason.hcrhousepoints.Models.Link;
import com.hcrpurdue.jason.hcrhousepoints.Utils.AlertDialogHelper;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.AlertDialogInterface;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.CacheManagementInterface;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.ListenerCallbackInterface;

public class QRScannerFragment extends Fragment implements ListenerCallbackInterface {
    private AppCompatActivity activity;
    private Context context;
    private ProgressBar progressBar;
    private CacheManager cacheManager;

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
        return inflater.inflate(R.layout.fragment_qr_scanner, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (AppCompatActivity) getActivity();
        progressBar = Objects.requireNonNull(activity).findViewById(R.id.navigationProgressBar);
        progressBar.setVisibility(View.VISIBLE);
        View view = Objects.requireNonNull(getView());
        Objects.requireNonNull(activity.getSupportActionBar()).setTitle("QR Scanner");

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 10);

        SurfaceView cameraView = view.findViewById(R.id.camera_view);

        BarcodeDetector detector = new BarcodeDetector.Builder(context).setBarcodeFormats(Barcode.QR_CODE).build();
        CameraSource cameraSource = new CameraSource.Builder(context, detector).setAutoFocusEnabled(true).build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    cameraSource.start(cameraView.getHolder());
                } catch (SecurityException e) {
                    cameraPermissionDenied();
                } catch (Exception e) {
                    errorStartingCamera();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        cacheManager = CacheManager.getInstance(getContext());
        cacheManager.getCachedData();
        setProcessor(detector, cameraSource, cameraView);
        progressBar.setVisibility(View.GONE);
    }


    private void setProcessor(BarcodeDetector detector, CameraSource cameraSource, SurfaceView cameraView){
        detector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> progressBar.setVisibility(View.VISIBLE));
                    handler.post(() -> detector.release());
                    handler.post(() -> cameraSource.stop());
                    Barcode barcode = barcodes.valueAt(0);
                    if(barcode.displayValue.length() > 11 && barcode.displayValue.substring(0, 11).equals("hcrpoint://")) {
                        String path = barcode.displayValue.substring(11);
                        String[] parts = path.split("/");
                        if (parts.length == 2) {
                            String linkId = parts[1].replace("/", "");
                            if(cacheManager.getCachedSystemPreferences().isHouseEnabled()) {
                                cacheManager.getLinkWithLinkId(linkId, new CacheManagementInterface() {
                                    @Override
                                    public void onError(Exception e, Context context) {
                                        handler.post(() -> progressBar.setVisibility(View.GONE));
                                        handler.post(() -> couldNotFindLink(new AlertDialogInterface() {
                                            @Override
                                            public void onPositiveButtonListener() {
                                                try {
                                                    handler.post(() -> {
                                                        try {
                                                            cameraSource.start(cameraView.getHolder());
                                                        } catch (IOException ex) {
                                                            handler.post(() -> errorStartingCamera());
                                                            Log.e("QRScanner", "Error in starting camera", ex);
                                                        }
                                                    });
                                                } catch (SecurityException ex) {
                                                    handler.post(() -> cameraPermissionDenied());
                                                    Log.e("QRScanner", "Camera access not granted", ex);
                                                }
                                            }
                                        }));
                                    }

                                    @Override
                                    public void onGetLinkWithIdSuccess(Link link) {
                                        foundQrCode(link, new AlertDialogInterface() {
                                            @Override
                                            public void onPositiveButtonListener() {
                                                cacheManager.submitPointWithLink(link, new CacheManagementInterface() {
                                                    @Override
                                                    public void onSuccess() {
                                                        // onFullSuccess for Ctrl+F
                                                        try {
                                                            ((NavigationView) activity.findViewById(R.id.nav_view)).getMenu().getItem(0).setChecked(true);
                                                            handler.post(() -> progressBar.setVisibility(View.GONE));
                                                            FragmentManager fragmentManager = activity.getSupportFragmentManager();
                                                            Fragment fragment = fragmentManager.findFragmentByTag(Integer.toString(R.id.nav_new_profile));
                                                            if (fragment == null)
                                                                fragment = ProfileFragment.class.newInstance();
                                                            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, Integer.toString(R.id.nav_new_profile)).addToBackStack(Integer.toString(R.id.nav_scan_code)).commit();
                                                            fragmentManager.executePendingTransactions();
                                                            ((NavigationActivity) activity).animateSuccess();
                                                        } catch (Exception e) {
                                                            handler.post(() -> Toast.makeText(context, "Point submitted successfully, please return to another page", Toast.LENGTH_SHORT).show());
                                                        }
                                                    }

                                                    @Override
                                                    public void onError(Exception e, Context context) {
                                                        handler.post(() -> progressBar.setVisibility(View.GONE));
                                                        handler.post(()-> setProcessor(detector,cameraSource,cameraView));
                                                        failedToSubmitPoints(e, new AlertDialogInterface() {
                                                            @Override
                                                            public void onPositiveButtonListener() {
                                                                restartCamera(handler, cameraSource, cameraView);
                                                            }
                                                        });
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onNegativeButtonListener() {
                                                handler.post(() -> progressBar.setVisibility(View.GONE));
                                                handler.post(()-> setProcessor(detector,cameraSource,cameraView));
                                                restartCamera(handler, cameraSource, cameraView);
                                            }
                                        });

                                    }
                                });
                            }///
                            else {
                                houseSystemIsDisabled();

                            }
                        } else {
                            handler.post(() -> progressBar.setVisibility(View.GONE));
                            handler.post(()-> setProcessor(detector,cameraSource,cameraView));
                            handler.post(() -> couldNotFindLink(new AlertDialogInterface() {
                                @Override
                                public void onPositiveButtonListener() {
                                    restartCamera(handler,cameraSource, cameraView);
                                }
                            }));

                        }
                    } else if (barcode.valueFormat == Barcode.URL) {
                        handler.post(() -> progressBar.setVisibility(View.GONE));
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(barcode.displayValue));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        handler.post(() -> progressBar.setVisibility(View.GONE));
                        handler.post(() -> couldNotFindLink(new AlertDialogInterface() {
                            @Override
                            public void onPositiveButtonListener() {
                                restartCamera(handler,cameraSource, cameraView);
                                setProcessor(detector,cameraSource,cameraView);
                            }
                        }));
                    }
                }
            }
        });
    }

    private void cameraPermissionDenied(){
        AlertDialogHelper.showSingleButtonDialog(getActivity(), "Could Not Launch Camera", "Camera permissions denied, please accept them in your settings.", "OK", null)
                .show();
    }

    private void errorStartingCamera(){
        AlertDialogHelper.showSingleButtonDialog(getActivity(), "Could Not Launch Camera", "There was a problem starting the camera.", "OK", new AlertDialogInterface() {
            @Override
            public void onPositiveButtonListener() {
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                fragmentManager.popBackStackImmediate();
            }
        })
                .show();
    }

    private void couldNotFindLink(AlertDialogInterface alertDialogInterface){
        AlertDialogHelper.showSingleButtonDialog(getActivity(), "Invalid QR Code", "The QR code you scanned wasn't found. Please try scanning it again.", "OK", alertDialogInterface)
                .show();
    }

    private void failedToSubmitPoints(Exception e, AlertDialogInterface alertDialogInterface){
        AlertDialogHelper.showSingleButtonDialog(getActivity(), "Failed To Submit Points", e.getMessage(), "OK", alertDialogInterface)
                .show();
    }

    private void restartCamera(Handler handler, CameraSource cameraSource, SurfaceView cameraView){
        try {
            handler.post(() -> {
                try {
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException ex) {
                    handler.post(() -> errorStartingCamera());
                    Log.e("QRScanner", "Error in starting camera", ex);
                }
            });
        } catch (SecurityException ex) {
            handler.post(() -> cameraPermissionDenied());
            Log.e("QRScanner", "Camera access not granted", ex);
        }
    }

    private void houseSystemIsDisabled(){
        AlertDialogHelper.showSingleButtonDialog(getActivity(), "House System Is Disabled", "Points can not be submitted when the house system is disabled.", "Drats", null)
                .show();
    }

    private void foundQrCode(Link link, AlertDialogInterface alertDialogInterface){
        AlertDialogHelper.showQRSubmissionDialog(getActivity(), link, alertDialogInterface).show();
    }
}
