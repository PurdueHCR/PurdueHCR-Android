package com.hcrpurdue.jason.hcrhousepoints;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import Models.PointLogMessage;
import Models.SystemPreferences;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Models.PointLog;
import Utils.ApprovePointListAdapter;
import Utils.FirebaseUtilInterface;
import Utils.PointLogMessageAdapter;
import Utils.Singleton;
import Utils.SingletonInterface;

public class PointLogDetailsFragment extends Fragment {
    static private Singleton singleton;
    private Context context;
    private ProgressBar progressBar;
    private PointLog log;

    private Button sendMessageButton;
    private Button rejectButton;
    private Button approveButton;
    private Button changeStatusButton;

    private EditText messageTextField;

    private RecyclerView recyclerView;

    private PointLogMessageAdapter adapter;
    private LinearLayoutManager manager;


    @Override
    public void onStart() {
        super.onStart();

        //Handle Updating
        singleton.handlePointLogUpdates(log, new SingletonInterface() {
            @Override
            public void onError(Exception e, Context context) {
                Toast.makeText(context,"Failed to update messages.",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onGetPointLogMessageUpdates(List<PointLogMessage> messages) {
                log.setMessages(messages);
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messages.size());
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        singleton = Singleton.getInstance(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        singleton = Singleton.getInstance(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.point_log_details_fragment, container, false);
//        singleton.getCachedData();
//        detailsList = view.findViewById(R.id.log_details_list);
//        retrieveBundleData();
//        ArrayList<String> items = log.getDetailedMessageList();
//        detailsList.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, items));
        //SwipeRefreshLayout swipeRefresh = view.findViewById(R.id.approve_list_swipe_refresh);
        //swipeRefresh.setOnRefreshListener(() -> getUnconfirmedPoints(swipeRefresh));
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity) Objects.requireNonNull(getActivity());
        progressBar = activity.findViewById(R.id.navigationProgressBar);
        retrieveBundleData();
        initializeFields(activity);

        //progressBar.setVisibility(View.VISIBLE);
        Objects.requireNonNull(activity.getSupportActionBar()).setTitle("Point Details");

        // If user is an RHP
        if(Singleton.getInstance(context).getPermissionLevel() == 1){
            if(log.wasHandled()){
                changeStatusButton.setVisibility(View.VISIBLE);
            }
            else{
                approveButton.setVisibility(View.VISIBLE);
                rejectButton.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Attempts to retrieve a Link model from the Bundle. Call this when initializing the view.
     * To put values in the correct place, when you build a fragmentTransatction, put a Bundle into the fragment with fragment.setArguments(Bundle);
     */
    private void retrieveBundleData(){
        Bundle bundle = getArguments();
        if(bundle != null){
            log = (PointLog) bundle.getSerializable("POINTLOG");
        }
    }

    /**
     * Initialize the fields on the fragment with the links to the xml
     * @param activity
     */
    private void initializeFields(AppCompatActivity activity){
        progressBar = activity.findViewById(R.id.navigationProgressBar);

        messageTextField = activity.findViewById(R.id.log_detail_message_edit_field);

        sendMessageButton = activity.findViewById(R.id.log_detail_send_button);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messageTextField.getText().toString();
                if(message == null || message.equals("")|| message.matches("\\s*")){
                    return;
                }
                messageTextField.setText("");
                singleton.postMessageToPointLog(log, message, new SingletonInterface() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Exception e, Context context) {
                        Toast.makeText(context,"Failed with error: "+e.getMessage(), Toast.LENGTH_LONG ).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
        rejectButton = activity.findViewById(R.id.log_detail_reject_button);
        rejectButton.setOnClickListener(new View.OnClickListener() {
            //TODO Change this to the new method
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                singleton.handlePointLog(log, false,false, new SingletonInterface() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(context,"Success",Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        rejectButton.setVisibility(View.GONE);
                        approveButton.setVisibility(View.GONE);
                        changeStatusButton.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onError(Exception e, Context context){
                        Toast.makeText(context,"Failed with error: "+e.getMessage(), Toast.LENGTH_LONG ).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
        approveButton = activity.findViewById(R.id.log_detail_approve_button);
        approveButton.setOnClickListener(new View.OnClickListener() {
            //TODO Change this to the new method
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                singleton.handlePointLog(log, true,false, new SingletonInterface() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(context,"Success",Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        rejectButton.setVisibility(View.GONE);
                        approveButton.setVisibility(View.GONE);
                        changeStatusButton.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onError(Exception e, Context context){
                        Toast.makeText(context,"Failed with error: "+e.getMessage(), Toast.LENGTH_LONG ).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
        changeStatusButton = activity.findViewById(R.id.log_detail_change_status_button);
        changeStatusButton.setOnClickListener(new View.OnClickListener() {
            //TODO Change this to the new method
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                singleton.handlePointLog(log, log.wasRejected(),true, new SingletonInterface() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(context,"Success",Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    @Override
                    public void onError(Exception e, Context context){
                        Toast.makeText(context,"Failed with error: "+e.getMessage(), Toast.LENGTH_LONG ).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });

        recyclerView = activity.findViewById(R.id.log_detail_history_list);
        adapter = new PointLogMessageAdapter(log,context);
        manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

    }


}
