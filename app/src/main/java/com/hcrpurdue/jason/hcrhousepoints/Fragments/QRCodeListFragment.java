package com.hcrpurdue.jason.hcrhousepoints.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hcrpurdue.jason.hcrhousepoints.R;

import java.util.ArrayList;
import java.util.Objects;

import com.hcrpurdue.jason.hcrhousepoints.Models.Link;
import com.hcrpurdue.jason.hcrhousepoints.ListAdapters.QrCodeListAdapter;
import com.hcrpurdue.jason.hcrhousepoints.Utils.Singleton;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.SingletonInterface;

public class QRCodeListFragment extends Fragment {
    static private Singleton singleton;
    private Context context;
    private ProgressBar progressBar;
    private ListView qrCodeListView;
    private FloatingActionButton qrCodeCreateFab;
    private TextView houseDisabledTextView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        singleton = Singleton.getInstance(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_qr_code_list, container, false);
        singleton.getCachedData();
        qrCodeListView = view.findViewById(R.id.qr_code_list);
        houseDisabledTextView = view.findViewById(R.id.qr_code_list_house_disabled_message);
        qrCodeCreateFab = (FloatingActionButton) view.findViewById(R.id.qr_code_create_fab);
        SwipeRefreshLayout swipeRefresh = view.findViewById(R.id.qr_code_list_swipe_refresh);
        swipeRefresh.setOnRefreshListener(() -> getQRCodesFromServer(swipeRefresh));

        qrCodeCreateFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Create destination fragment
                Fragment fragment = new QRCreationFragment();

                //Create Fragment manager
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, fragment, Integer.toString(R.id.generateQRCode));
                fragmentTransaction.addToBackStack(Integer.toString(R.id.nav_qr_code_display));
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity) Objects.requireNonNull(getActivity());
        progressBar = activity.findViewById(R.id.navigationProgressBar);

        boolean isHouseEnabled = singleton.getCachedSystemPreferences().isHouseEnabled();

        if(!isHouseEnabled) {
            qrCodeListView.setVisibility(View.GONE);
            houseDisabledTextView.setText(singleton.getCachedSystemPreferences().getHouseIsEnabledMsg());
            houseDisabledTextView.setVisibility(View.VISIBLE);

        }

        else {
            qrCodeListView.setVisibility(View.VISIBLE);
            houseDisabledTextView.setVisibility(View.GONE);
        }

        getQRCodesFromServer(null);
        progressBar.setVisibility(View.VISIBLE);
        Objects.requireNonNull(activity.getSupportActionBar()).setTitle("QR Codes");


    }

    private void getQRCodesFromServer(SwipeRefreshLayout swipeRefresh) {

        singleton.getUserCreatedQRCodes(true, new SingletonInterface() {
            @Override
            public void onError(Exception e, Context context) {
                Toast.makeText(context, "Failed to retrieve QR Codes.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onGetQRCodesForUserSuccess(ArrayList<Link> qrCodes) {
                QrCodeListAdapter adapter = new QrCodeListAdapter(qrCodes,context);
                qrCodeListView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
                if(swipeRefresh != null){
                    swipeRefresh.setRefreshing(false);
                }

            }
        });
    }

}
