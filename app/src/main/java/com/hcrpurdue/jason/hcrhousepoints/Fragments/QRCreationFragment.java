package com.hcrpurdue.jason.hcrhousepoints.Fragments;

import com.hcrpurdue.jason.hcrhousepoints.Models.Link;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointType;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.CacheManagementInterface;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.ListenerCallbackInterface;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.hcrpurdue.jason.hcrhousepoints.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class QRCreationFragment extends Fragment implements ListenerCallbackInterface {
    private Context context;
    private ArrayList<PointType> enabledTypes = new ArrayList<PointType>();
    CacheManager cacheManager;

    Spinner pointTypeSpinner;
    Switch multipleUseSwitch;
    EditText codeDescriptionLabel;
    Button generateQRButton;

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
        View view = inflater.inflate(R.layout.fragment_qr_creation, container, false);
        cacheManager = CacheManager.getInstance(view.getContext());

        initializeUIElements(view);

        return view;
    }

    /**
     * Initializes UIElements in the View
     *
     * @param view View where elements are to be initialized
     */
    private void initializeUIElements(View view) {
        pointTypeSpinner = view.findViewById(R.id.point_type_spinner);
        multipleUseSwitch = (Switch) view.findViewById(R.id.multi_use_switch);
        multipleUseSwitch.setOnClickListener(v -> flipSwitch(view));
        codeDescriptionLabel = (EditText) view.findViewById(R.id.generate_qrcode_description);
        generateQRButton = (Button) view.findViewById(R.id.generate_button);
        generateQRButton.setOnClickListener(v -> generateQRCode(view));
        loadSpinner(cacheManager.getPointTypeList());
        refreshData();

    }

    /**
     * Refresh the point types in the Spinner
     */
    private void refreshData() {
        try {
            cacheManager.getUpdatedPointTypes(new CacheManagementInterface() {
                public void onPointTypeComplete(List<PointType> data) {
                    loadSpinner(data);
                }
            });

        } catch (Exception e) {
            Toast.makeText(context, "Failed to load point types", Toast.LENGTH_LONG).show();
            Log.e("PointSubmissionFragment", "Error loading point types", e);
        }
    }

    /**
     * Puts the PointType list into the Spinner
     * @param types List of PointType objects to put into the spinner
     */
    private void loadSpinner(List<PointType> types){
        enabledTypes = new ArrayList<>();
        List<Map<String, String>> formattedPointTypes = new ArrayList<>();
        for (PointType type : types) {
            if (type.getRHPsCanGenerateQRCodes() && type.isEnabled()) {
                enabledTypes.add(type);
                Map<String, String> map = new HashMap<>();
                map.put("text", type.getName());
                map.put("subText", String.valueOf(type.getValue()) + " points");
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
        if(TextUtils.isEmpty(codeDescriptionLabel.getText())){
            Toast.makeText(context,"Please enter a description",Toast.LENGTH_LONG).show();
        }
        else{
            //Create new Link object
            Link link = new Link(codeDescriptionLabel.getText().toString(),
                    (!multipleUseSwitch.isChecked()),
                    enabledTypes.get(pointTypeSpinner.getSelectedItemPosition()).getId());
            Toast.makeText(context,"ID: "+enabledTypes.get(pointTypeSpinner.getSelectedItemPosition()).getId(),Toast.LENGTH_LONG).show();
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
        }
    }

}
