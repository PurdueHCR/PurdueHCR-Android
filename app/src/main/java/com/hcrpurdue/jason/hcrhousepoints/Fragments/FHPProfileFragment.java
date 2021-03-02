package com.hcrpurdue.jason.hcrhousepoints.Fragments;


import android.view.View;

import com.hcrpurdue.jason.hcrhousepoints.Models.Enums.UserPermissionLevel;
import com.hcrpurdue.jason.hcrhousepoints.R;

import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.ListenerCallbackInterface;
import com.hcrpurdue.jason.hcrhousepoints.Views.HouseCompetitionCard;
import com.hcrpurdue.jason.hcrhousepoints.Views.RecentPointSubmissionCard;
import com.hcrpurdue.jason.hcrhousepoints.Views.ResidentProfileToolbar;
import com.hcrpurdue.jason.hcrhousepoints.Views.RewardCard;


public class FHPProfileFragment extends BaseProfileFragment {

    private ResidentProfileToolbar residentProfileToolbar;
    private HouseCompetitionCard houseCompetitionCard;


    @Override
    protected void createListeners() {
        flu.getUserPointLogListener().addCallback(PROFILE_FRAGMENT, new ListenerCallbackInterface() {
            @Override
            public void onUpdate() {
                handleNotificationsUpdate();
            }
        });
        flu.getUserAccountListener().addCallback(PROFILE_FRAGMENT, new ListenerCallbackInterface() {
            @Override
            public void onUpdate() {
                handleUserUpdate();
            }
        });
        flu.getHouseListener().addCallback(PROFILE_FRAGMENT, new ListenerCallbackInterface() {
            @Override
            public void onUpdate() {
                handleHouseUpdate();
            }
        });

        flu.getSystemPreferenceListener().addCallback(PROFILE_FRAGMENT, new ListenerCallbackInterface() {
            @Override
            public void onUpdate() {
                handleSystemPreferenceUpdate();
            }
        });
    }


    @Override
    protected int getFragmentLayoutId() {
        return R.layout.fragment_fhp_profile;
    }

    @Override
    protected void setupCards(View view) {
        if(cacheManager.getPermissionLevel() != UserPermissionLevel.FHP && cacheManager.getPermissionLevel() != UserPermissionLevel.PROFESSIONAL_STAFF) {
            residentProfileToolbar = new ResidentProfileToolbar(context, view);
            houseCompetitionCard = new HouseCompetitionCard(context, view);
        }
    }


    /**
     * This method will be called when the userPointLogListener gets activated by Firebase. This will
     *  update the notifications Icon to show if there are new messages.
     */
    private void handleNotificationsUpdate(){
        if(notificationsButton != null)
            notificationsButton.handleNotificationUpdate();
    }

    /**
     * This method will be called when the userPointLogListener gets activated by Firebase. This
     *  will update the user rank
     */
    private void handleUserUpdate(){
        if( residentProfileToolbar != null)
            residentProfileToolbar.handleUserUpdate();
    }

    /**
     * This method will be called when the houseListener gets activated by Firebase.
     */
    private void handleHouseUpdate() {
        if(houseCompetitionCard != null)
            houseCompetitionCard.handleHouseUpdate();
        if(residentProfileToolbar != null){
            residentProfileToolbar.handleHousesUpdated();
        }
    }

    private void handleSystemPreferenceUpdate(){
        if(houseCompetitionCard != null){
            houseCompetitionCard.handleSysPrefUpdate();
        }
        if(residentProfileToolbar != null){
            residentProfileToolbar.handleSysPrefUpdate();
        }
    }
}
