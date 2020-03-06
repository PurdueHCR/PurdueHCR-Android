package com.hcrpurdue.jason.hcrhousepoints.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.ListFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hcrpurdue.jason.hcrhousepoints.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.hcrpurdue.jason.hcrhousepoints.Models.Link;
import com.hcrpurdue.jason.hcrhousepoints.ListAdapters.QrCodeListAdapter;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.CacheManagementInterface;

public class QRCodeListFragment extends ListFragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {
    static private CacheManager cacheManager;
    private Context context;
    private ProgressBar progressBar;
    private ListView qrCodeListView;
    private FloatingActionButton qrCodeCreateFab;
    private SwipeRefreshLayout swipeRefresh;
    private TextView messageTextView;
    private List<Link> links;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cacheManager = CacheManager.getInstance(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        getQRCodesFromServer(swipeRefresh);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_qr_code_list, container, false);
        cacheManager.getCachedData();
        qrCodeListView = view.findViewById(android.R.id.list);
        messageTextView = view.findViewById(android.R.id.empty);
        qrCodeCreateFab = view.findViewById(R.id.qr_code_create_fab);
        swipeRefresh = view.findViewById(R.id.qr_code_list_swipe_refresh);
        swipeRefresh.setOnRefreshListener(() -> getQRCodesFromServer(swipeRefresh));

        qrCodeCreateFab.setOnClickListener(view1 -> {

            QrCodeCreateBottomDialogFragment createFragment = new QrCodeCreateBottomDialogFragment(context);
            createFragment.show(getFragmentManager(), "Create QR Code");
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity) Objects.requireNonNull(getActivity());
        progressBar = activity.findViewById(R.id.navigationProgressBar);

        progressBar.setVisibility(View.VISIBLE);
        Objects.requireNonNull(activity.getSupportActionBar()).setTitle("QR Codes");


    }

    private void getQRCodesFromServer(SwipeRefreshLayout swipeRefresh) {

        cacheManager.getUserCreatedQRCodes(true, new CacheManagementInterface() {
            @Override
            public void onError(Exception e, Context context) {
                Toast.makeText(context, "Failed to retrieve QR Codes.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onGetQRCodesForUserSuccess(ArrayList<Link> qrCodes) {
                progressBar.setVisibility(View.GONE);
                if(swipeRefresh != null){
                    swipeRefresh.setRefreshing(false);
                }
                links = qrCodes;
                redrawQRCodes();

            }
        });
    }

    public void redrawQRCodes(){
        if(links != null && links.size() != 0){
            QrCodeListAdapter adapter = new QrCodeListAdapter(links,context);
            qrCodeListView.setVisibility(View.VISIBLE);
            qrCodeListView.setAdapter(adapter);
            messageTextView.setVisibility(View.GONE);
        }
        else{
            qrCodeListView.setVisibility(View.INVISIBLE);
            messageTextView.setVisibility(View.VISIBLE);
            messageTextView.setText("You haven't made any QR Codes.");
        }
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem menuItem) {
        return false;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }


}
