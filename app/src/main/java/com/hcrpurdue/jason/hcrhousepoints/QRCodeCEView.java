package com.hcrpurdue.jason.hcrhousepoints;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Point;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.hcrpurdue.jason.hcrhousepoints.Models.Link;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointType;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class QRCodeCEView {
    private Link code;
    private Context context;
    private ArrayList<PointType> enabledTypes = new ArrayList<PointType>();
    CacheManager cacheManager;

    Spinner pointTypeSpinner;
    Switch multipleUseSwitch;
    EditText descriptionEditText;
    Button createButton;

    TextView invalidPointType;
    TextView invalidDescription;

    public QRCodeCEView(Context context, View parentView, Link link){
        this.context = context;
        this.cacheManager = CacheManager.getInstance(context);
        this.code = link;

        pointTypeSpinner = parentView.findViewById(R.id.point_type_spinner);
        multipleUseSwitch = parentView.findViewById(R.id.multi_use_switch);
        descriptionEditText = parentView.findViewById(R.id.qr_code_description_edit_text);
        createButton = parentView.findViewById(R.id.create_button);
        invalidPointType =  parentView.findViewById(R.id.invalid_point_type_label);
        invalidDescription = parentView.findViewById(R.id.invalid_description);

        invalidPointType.setVisibility(View.INVISIBLE);
        invalidDescription.setVisibility(View.INVISIBLE);

        multipleUseSwitch.setOnClickListener(v -> flipSwitch(parentView));
        createButton.setOnClickListener(v -> generateQRCode(parentView));

        loadSpinner(cacheManager.getPointTypeList());

        if(code != null){
            descriptionEditText.setText(code.getDescription());
            createButton.setText("Update");
            multipleUseSwitch.setChecked(!code.isSingleUse());
        }
    }

    public QRCodeCEView(Context context, View parentView){
        this(context, parentView, null);
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
        placeholder.put("text", "Select a Point Type");
        placeholder.put("subText", " Tap to select");
        formattedPointTypes.add(placeholder);
        for (PointType type : types) {
            if (type.getUserCanGenerateQRCodes(cacheManager.getPermissionLevel()) && type.isEnabled()) {
                enabledTypes.add(type);
                Map<String, String> map = new HashMap<>();
                map.put("text", type.getName());
                map.put("subText", type.getValue() + " point" + ((type.getValue() == 1)? "":"s"));
                formattedPointTypes.add(map);
                if(code != null && code.getPointTypeId() == type.getId()){
                    selectedPointType = type;
                }
            }
        }
        SimpleAdapter adapter = new SimpleAdapter(context, formattedPointTypes, android.R.layout.simple_list_item_2, new String[]{"text", "subText"}, new int[]{android.R.id.text1, android.R.id.text2});
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_2);
        pointTypeSpinner.setAdapter(adapter);
        if(selectedPointType != null){
            pointTypeSpinner.setSelection(enabledTypes.indexOf(selectedPointType));
        }
    }

    /**
     * Handles the logic for flipping the Multi-Use Switch
     * @param view View where button lies
     */
    private void flipSwitch(View view){
        if(multipleUseSwitch.isChecked()){
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Warning");
            alertDialog.setMessage("If you allow this code to be used multiple times, points submitted will have to be approved by an RHP.");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                    (dialog, which) -> dialog.dismiss());
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                    (dialog, which) -> {
                        multipleUseSwitch.setChecked(false);
                        dialog.dismiss();
                    });
            alertDialog.show();
        }
    }

    /**
     * Handles the logic when the generate QRcode button is pressed
     * @param view view where button is
     */
    private void generateQRCode(View view) {

        //check conditions
        if(TextUtils.isEmpty(descriptionEditText.getText())){
            invalidDescription.setVisibility(View.VISIBLE);
            invalidDescription.setText(R.string.invalid_empty_description);
            Toast.makeText(context,"Please enter a description",Toast.LENGTH_LONG).show();
        }
        else if (descriptionEditText.getText().length() > 64){
            invalidDescription.setText(R.string.invalid_length_description);
            invalidDescription.setVisibility(View.VISIBLE);
        }
        else if(pointTypeSpinner.getSelectedItemPosition() == 0){
            invalidPointType.setVisibility(View.VISIBLE);
        }
        else{
            invalidDescription.setVisibility(View.INVISIBLE);
            invalidPointType.setVisibility(View.VISIBLE);
            //Create new Link object
            /*
            Link link = new Link(codeDescriptionLabel.getText().toString(),
                    (!multipleUseSwitch.isChecked()),
                    enabledTypes.get(pointTypeSpinner.getSelectedItemPosition()).getId());
            //Pass to CacheManager then Firebase to handle generation of Links in database
            cacheManager.createQRCode(link, new CacheManagementInterface() {
                @Override
                public void onSuccess() {
                    //Put the link into the Bundle
                    Bundle args = new Bundle();
                    args.putSerializable("QRCODE", link);

                    //Create destination fragment
                    Fragment fragment = new QrCodeDetailsFragment();
                    fragment.setArguments(args);

                    //Create Fragment manager
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame, fragment, Integer.toString(R.id.nav_qr_code_display));
                    fragmentTransaction.addToBackStack(Integer.toString(R.id.generateQRCode));
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentTransaction.commit();
                }
                @Override
                public void onError(Exception e, Context context) {
                    Toast.makeText(context,"Could Not Create QR Code. Try Again",Toast.LENGTH_LONG).show();
                }
            });
             */
        }
    }
}
