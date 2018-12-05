package com.hcrpurdue.jason.hcrhousepoints;

import Models.PointType;
import Utils.Singleton;
import Utils.SingletonInterface;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QRCreation extends Fragment {
    private Context context;
    private Singleton singleton;
    private EditText etInput;
    private Button btnCreateQR;
    private ImageView imageView;
    private AppCompatActivity activity;
    private ArrayList<PointType> totalPtTypes;
    private ArrayList<PointType> scannablePointTypes;
    private ProgressBar progressBar;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        View view = getView();
    }


    public void onActivityCreated(Bundle savedInstanceState) {      //TODO: Check if this is correct
        super.onActivityCreated(savedInstanceState);
        activity = (AppCompatActivity) getActivity();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {        //TODO: Maaaajor check
        super.onCreate(savedInstanceState);
        singleton = Singleton.getInstance(context);
        etInput = activity.findViewById(R.id.linkInput);
        btnCreateQR = activity.findViewById(R.id.btnCreate);
        imageView = activity.findViewById(R.id.QRView);

//
//        totalPtTypes = singleton.getPointTypes(new SingletonInterface() {
//            @Override
//            View view1;
//            public void onPointTypeComplete(List<PointType> data) {
//                List<Map<String, String>> formattedPointTypes = new ArrayList<>();
//                for (PointType type : data) {
//                    if (type.getResidentsCanSubmit()) {
//                        Map<String, String> map = new HashMap<>();
//                        map.put("text", type.getPointDescription());
//                        map.put("subText", String.valueOf(type.getPointValue()) + " points");
//                        formattedPointTypes.add(map);
//                    } else
//                        break;
//                }
//                SimpleAdapter adapter = new SimpleAdapter(context, formattedPointTypes, android.R.layout.simple_list_item_2, new String[]{"text", "subText"}, new int[]{android.R.id.text1, android.R.id.text2});
//                adapter.setDropDownViewResource(android.R.layout.simple_list_item_2);
//                ((Spinner) view1.findViewById(R.id.pointTypeSpinner)).setAdapter(adapter);
//                progressBar.setVisibility(View.GONE);
//            }
//        });
//
//
//        btnCreateQR.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                String text = etInput.getText().toString().trim();
//
//                if(text != null || !text.isEmpty()) {
//                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
//                    try {
//                        BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 500, 500);
//
//                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
//                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
//                        imageView.setImageBitmap(bitmap);
//                    }
//                    catch(Exception e) {
//                        System.out.println("MFW Exception");
//                    }
//                }
//            }
//        });
    }

}
