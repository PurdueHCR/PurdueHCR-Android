package com.hcrpurdue.jason.hcrhousepoints.Fragments;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;

import com.hcrpurdue.jason.hcrhousepoints.Models.PointLogMessage;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hcrpurdue.jason.hcrhousepoints.R;

import java.util.List;
import java.util.Objects;

import com.hcrpurdue.jason.hcrhousepoints.Models.PointLog;

import com.hcrpurdue.jason.hcrhousepoints.ListAdapters.PointLogMessageAdapter;
import com.hcrpurdue.jason.hcrhousepoints.Utils.FirebaseListenerUtil;
import com.hcrpurdue.jason.hcrhousepoints.Utils.Singleton;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.ListenerCallbackInterface;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.SingletonInterface;

public class PointLogDetailsFragment extends Fragment implements ListenerCallbackInterface {
    static private Singleton singleton;
    private Context context;
    private ProgressBar progressBar;
    private PointLog log;
    private List<PointLogMessage> logMessages;

    private Button sendMessageButton;
    private Button rejectButton;
    private Button approveButton;
    private Button changeStatusButton;

    private EditText messageTextField;

    private RecyclerView recyclerView;

    private PointLogMessageAdapter adapter;
    private LinearLayoutManager manager;

    private FirebaseListenerUtil flu;
    private final String CALLBACK_KEY = "POINT_LOG_DETAILS";


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
                logMessages = messages;
                log.setMessages(logMessages);
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
        View view = inflater.inflate(R.layout.fragment_point_log_details, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity) Objects.requireNonNull(getActivity());
        progressBar = activity.findViewById(R.id.navigationProgressBar);
        retrieveBundleData();
        logMessages = log.getMessages();
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

        flu = FirebaseListenerUtil.getInstance(getContext());
        //If Log belongs to this user, update it with UserPointLogListener
        if(log.getResidentId().equals(singleton.getUserId())){
            flu.getUserPointLogListener().addCallback(CALLBACK_KEY, new ListenerCallbackInterface() {
                @Override
                public void onUpdate() {
                    handleUserLogUpdate();
                }
            });
        }
        else{
            flu.createPointLogListener(log);
            flu.getPointLogListener().addCallback(CALLBACK_KEY, new ListenerCallbackInterface() {
                @Override
                public void onUpdate() {
                    handleNotOwnedLogUpdate();
                }
            });
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
                hideKeyboard();
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
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
        rejectButton = activity.findViewById(R.id.log_detail_reject_button);
        rejectButton.setOnClickListener(new View.OnClickListener() {
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

    /**
     * When the fragment is done, remove the callbacks
     */
    @Override
    public void onDetach() {
        super.onDetach();
        //Remove listeners
        flu.getUserPointLogListener().removeCallback(CALLBACK_KEY);
        if(flu.getPointLogListener() != null){
            flu.getPointLogListener().killListener();
        }
        //Reset the notification count when the user leaves.
        singleton.resetPointLogNotificationCount(log,(log.getResidentId().equals(singleton.getUserId())));
    }

    /**
     * If the displayed point log belongs to this user, handle updates here
     */
    public void handleUserLogUpdate() {
        List<PointLog> logs = singleton.getPersonalPointLogs();
        for(PointLog pointLog: logs){
            //If the log equals to the displayed logs ID, then update the displayed Log
            if(pointLog.getLogID().equals(log.getLogID())){
                //Make sure the point messages are transfered over
                log.updateValues(pointLog);
                //Will handle updates for approval status as notification count
                adapter.notifyDataSetChanged();
                break;
            }
        }
    }

    /**
     * If this log is not owned by this user, use this callback handler.
     */
    public void handleNotOwnedLogUpdate(){
        /*
        When we created the listener, we passed in the reference to this log, so when we get an
        update it will update our instance.
         */
        adapter.notifyDataSetChanged();
    }

    private void hideKeyboard(){
        InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null && getActivity().getCurrentFocus() != null) { // Avoids null pointer exceptions
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


}
