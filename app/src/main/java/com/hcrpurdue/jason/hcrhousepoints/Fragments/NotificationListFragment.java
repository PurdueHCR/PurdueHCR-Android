package com.hcrpurdue.jason.hcrhousepoints.Fragments;

import android.content.Context;

import com.hcrpurdue.jason.hcrhousepoints.ListAdapters.PointLogAdapter;
import com.hcrpurdue.jason.hcrhousepoints.Models.Enums.UserPermissionLevel;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointLog;
import com.hcrpurdue.jason.hcrhousepoints.R;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.ListenerCallbackInterface;

import java.util.List;

public class NotificationListFragment extends PersonalPointLogListFragment {

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(cacheManager.getPermissionLevel() == UserPermissionLevel.RHP){
            flu.getRHPNotificationListener().addCallback(CALLBACK_KEY, new ListenerCallbackInterface() {
                @Override
                public void onUpdate() {
                    handleUpdate();
                }
            });
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(cacheManager.getPermissionLevel() == UserPermissionLevel.RHP){
            flu.getRHPNotificationListener().removeCallback(CALLBACK_KEY);
        }
    }

    @Override
    protected void createAdapter(List<PointLog> logs){
        if(logs.size() == 0){
            emptyMessageTextView.setText("You do not have any notifications to respond to.");
        }

        adapter = new PointLogAdapter(logs,getContext(), R.id.nav_personal_point_log_list);
        setListAdapter(adapter);
    }

    @Override
    public void handleUpdate() {
        setLogs();
        if(!isSearching){
            createAdapter(logs);
        }
    }

    private void setLogs(){
        logs.clear();
        List<PointLog> personalLogs = cacheManager.getPersonalPointLogs();
        for(PointLog log: personalLogs){
            if(log.getResidentNotifications() != 0){
                logs.add(log);
            }
        }
        if(cacheManager.getPermissionLevel() == UserPermissionLevel.RHP){
            logs.addAll(cacheManager.getRHPNotificationLogs());
        }
    }
}
