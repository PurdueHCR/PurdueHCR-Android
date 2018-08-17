package com.hcrpurdue.jason.hcrhousepoints;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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

import java.io.IOException;
import java.util.Objects;

import Models.Link;
import Utils.Singleton;
import Utils.SingletonInterface;

public class QRScan extends Fragment {
    AppCompatActivity activity;
    Context context;
    ProgressBar progressBar;
    
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        this.context = context;
        activity = (AppCompatActivity) getActivity();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressBar = activity.findViewById(R.id.navigationProgressBar);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.qr_reader, container, false);
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
                    Toast.makeText(context, "Camera permissions denied, please accept them", Toast.LENGTH_SHORT).show();
                    Log.e("QRScanner", "Camera access not granted", e);
                } catch (Exception e) {
                    Toast.makeText(context, "Error in starting camera", Toast.LENGTH_SHORT).show();
                    Log.e("QRScanner", "Error in starting camera", e);
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

        detector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    AsyncTask.execute(cameraSource::stop);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> progressBar.setVisibility(View.VISIBLE));
                    Barcode barcode = barcodes.valueAt(0);
                    if (barcode.displayValue.length() > 11 && barcode.displayValue.substring(0, 11).equals("hcrpoint://")) {
                        String path = barcode.displayValue.substring(11);
                        String[] parts = path.split("/");
                        if (parts.length == 2) {
                            Singleton singleton = Singleton.getInstance();
                            String linkId = parts[1].replace("/", "");
                            singleton.getLinkWithLinkId(linkId, new SingletonInterface() {
                                @Override
                                public void onError(Exception e, Context context) {
                                    handler.post(() -> progressBar.setVisibility(View.GONE));
                                    handler.post(() -> Toast.makeText(context, "Failed to count link with issue: " + e.getLocalizedMessage(),
                                            Toast.LENGTH_SHORT).show());
                                    try {
                                        handler.post(() -> {
                                            try {
                                                cameraSource.start(cameraView.getHolder());
                                            } catch (IOException ex) {
                                                handler.post(() -> Toast.makeText(context, "Error in starting camera", Toast.LENGTH_SHORT).show());
                                                Log.e("QRScanner", "Error in starting camera", ex);
                                            }
                                        });
                                    } catch (SecurityException ex) {
                                        handler.post(() -> Toast.makeText(context, "Camera permissions denied, please accept them", Toast.LENGTH_SHORT).show());
                                        Log.e("QRScanner", "Camera access not granted", ex);
                                    }
                                }

                                @Override
                                public void onGetLinkWithIdSuccess(Link link) {
                                    singleton.submitPointWithLink(link, new SingletonInterface() {
                                        @Override
                                        public void onSuccess() {
                                            // onFullSuccess for Ctrl+F
                                            try {
                                                ((NavigationView)activity.findViewById(R.id.nav_view)).getMenu().getItem(0).setChecked(true);
                                                handler.post(() -> progressBar.setVisibility(View.GONE));
                                                Bundle bundle = new Bundle();
                                                bundle.putBoolean("showSuccess", true);
                                                Fragment fragment = SubmitPoints.class.newInstance();
                                                fragment.setArguments(bundle);
                                                activity.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
                                            } catch (Exception e) {
                                                handler.post(() -> Toast.makeText(context, "Point submitted successfully, please return to another page", Toast.LENGTH_SHORT).show());
                                            }
                                        }

                                        @Override
                                        public void onError(Exception e, Context context) {
                                            handler.post(() -> progressBar.setVisibility(View.GONE));
                                            handler.post(() -> Toast.makeText(context, "Failed to count link with issue: " + e.getLocalizedMessage(),
                                                    Toast.LENGTH_SHORT).show());
                                            try {
                                                handler.post(() -> {
                                                    try {
                                                        cameraSource.start(cameraView.getHolder());
                                                    } catch (IOException ex) {
                                                        handler.post(() -> Toast.makeText(context, "Error in starting camera", Toast.LENGTH_SHORT).show());
                                                        Log.e("QRScanner", "Error in starting camera", ex);
                                                    }
                                                });
                                            } catch (SecurityException ex) {
                                                handler.post(() -> Toast.makeText(context, "Camera permissions denied, please accept them", Toast.LENGTH_SHORT).show());
                                                Log.e("QRScanner", "Camera access not granted", ex);
                                            }
                                        }
                                    });
                                }
                            });
                        } else {
                            handler.post(() -> progressBar.setVisibility(View.GONE));
                            handler.post(() -> Toast.makeText(context, "Invalid QR Code",
                                    Toast.LENGTH_SHORT).show());
                            try {
                                handler.post(() -> {
                                    try {
                                        cameraSource.start(cameraView.getHolder());
                                    } catch (IOException ex) {
                                        handler.post(() -> Toast.makeText(context, "Error in starting camera", Toast.LENGTH_SHORT).show());
                                        Log.e("QRScanner", "Error in starting camera", ex);
                                    }
                                });
                            } catch (SecurityException ex) {
                                handler.post(() -> Toast.makeText(context, "Camera permissions denied, please accept them", Toast.LENGTH_SHORT).show());
                                Log.e("QRScanner", "Camera access not granted", ex);
                            }
                        }
                    } else if (barcode.valueFormat == Barcode.URL) {
                        handler.post(() -> progressBar.setVisibility(View.GONE));
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(barcode.displayValue));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        handler.post(() -> progressBar.setVisibility(View.GONE));
                        handler.post(() -> Toast.makeText(context, "Invalid QR Code", Toast.LENGTH_SHORT).show());
                        try {
                            handler.post(() -> {
                                try {
                                    cameraSource.start(cameraView.getHolder());
                                } catch (IOException ex) {
                                        handler.post(() -> Toast.makeText(context, "Error in starting camera", Toast.LENGTH_SHORT).show());
                                        Log.e("QRScanner", "Error in starting camera", ex);
                                }
                            });
                        } catch (SecurityException ex) {
                            handler.post(() -> Toast.makeText(context, "Camera permissions denied, please accept them", Toast.LENGTH_SHORT).show());
                            Log.e("QRScanner", "Camera access not granted", ex);
                        }
                    }
                }
            }
        });
        return view;
    }
}
