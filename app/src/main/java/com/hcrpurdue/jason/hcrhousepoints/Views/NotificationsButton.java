package com.hcrpurdue.jason.hcrhousepoints.Views;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.hcrpurdue.jason.hcrhousepoints.Fragments.NotificationListFragment;
import com.hcrpurdue.jason.hcrhousepoints.Models.Enums.UserPermissionLevel;
import com.hcrpurdue.jason.hcrhousepoints.Models.PointLog;
import com.hcrpurdue.jason.hcrhousepoints.R;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;

public class NotificationsButton {

    private MenuItem notificationBarButton;
    private CacheManager cacheManager;

    public NotificationsButton(Context context, Menu menu){

        cacheManager = CacheManager.getInstance(context);
        notificationBarButton = menu.findItem(R.id.notifications_action_bar_button);
        notificationBarButton.setOnMenuItemClickListener(menuItem -> {
            Fragment fragment = new NotificationListFragment();

            //Create Fragment manager
            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment, Integer.toString(R.id.nav_notification_fragment));
            fragmentTransaction.addToBackStack(Integer.toString(R.id.nav_notification_fragment));
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
            return true;
        });

        notificationBarButton.setIcon(R.drawable.ic_notifications_active);
        setNotificationBarButtonIcon();
    }


    /**
     * Check if there are any notifications on any of the Point Logs
     *  and if so, set the notification icon to ic_notifications_active, else set to ic_notifications_none
     */
    private void setNotificationBarButtonIcon(){
        if(notificationBarButton != null) {
            UserPermissionLevel userPermissionLevel = cacheManager.getPermissionLevel();

            //If the user has any notifications, set icon to active
            for (PointLog log : cacheManager.getPersonalPointLogs()) {
                if (log.getResidentNotifications() > 0) {
                    notificationBarButton.setIcon(R.drawable.ic_notifications_active);
                    return;
                }
            }

            //If the User is an RHP, check the notification count
            if (userPermissionLevel == UserPermissionLevel.RHP) {
                if (cacheManager.getNotificationCount() > 0) {
                    notificationBarButton.setIcon(R.drawable.ic_notifications_active);
                    return;
                }
            }
            //Otherwise reset the notification button to none.
            notificationBarButton.setIcon(R.drawable.ic_notifications_none);
        }
    }

    public void handleNotificationUpdate(){
        setNotificationBarButtonIcon();
    }

}
