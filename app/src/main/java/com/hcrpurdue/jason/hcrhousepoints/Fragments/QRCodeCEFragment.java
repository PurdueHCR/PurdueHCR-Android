package com.hcrpurdue.jason.hcrhousepoints.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hcrpurdue.jason.hcrhousepoints.Models.Link;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointType;
import com.hcrpurdue.jason.hcrhousepoints.R;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.CacheManagementInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * The QR Code Create and Edit Fragment is the bottom sheet modal that will pop up and appear when
 * a QR code needs to be created or edited
 */
public class QRCodeCEFragment extends BottomSheetDialogFragment {

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

    public QRCodeCEFragment(Context context) {
        this.context = context;
        this.cacheManager = CacheManager.getInstance(context);
    }

    public QRCodeCEFragment(Context context, Link code) {
        this(context);
        this.code = code;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.modal_qr_create_and_edit, container,
                false);
        pointTypeSpinner = view.findViewById(R.id.point_type_spinner);
        multipleUseSwitch = view.findViewById(R.id.multi_use_switch);
        descriptionEditText = view.findViewById(R.id.qr_code_description_edit_text);
        createButton = view.findViewById(R.id.create_button);
        invalidPointType =  view.findViewById(R.id.invalid_point_type_label);
        invalidDescription = view.findViewById(R.id.invalid_description);

        invalidPointType.setVisibility(View.GONE);
        invalidDescription.setVisibility(View.GONE);

        multipleUseSwitch.setOnClickListener(v -> flipSwitch(view));
        createButton.setOnClickListener(v -> generateQRCode(view));

        loadSpinner(cacheManager.getPointTypeList());

        return view;

    }


    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(requireContext(), getTheme());
    }

    /**
     * Puts the PointType list into the Spinner
     * @param types List of PointType objects to put into the spinner
     */
    private void loadSpinner(List<PointType> types){
        enabledTypes = new ArrayList<>();

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
            }
        }
        SimpleAdapter adapter = new SimpleAdapter(context, formattedPointTypes, android.R.layout.simple_list_item_2, new String[]{"text", "subText"}, new int[]{android.R.id.text1, android.R.id.text2});
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_2);
        pointTypeSpinner.setAdapter(adapter);
    }

    /**
     * Handles the logic for flipping the Multi-Use Switch
     * @param view View where button lies
     */
    private void flipSwitch(View view){
        if(multipleUseSwitch.isChecked()){
            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
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
        //Dismiss keyboard when generating
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);

        if(imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0); // Hide keyboard
        }

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
