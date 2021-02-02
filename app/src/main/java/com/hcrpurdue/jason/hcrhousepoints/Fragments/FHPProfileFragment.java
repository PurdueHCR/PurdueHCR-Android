package com.hcrpurdue.jason.hcrhousepoints.Fragments;


import android.view.View;

import com.hcrpurdue.jason.hcrhousepoints.R;

import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.ListenerCallbackInterface;
import com.hcrpurdue.jason.hcrhousepoints.Views.HouseCompetitionCard;
import com.hcrpurdue.jason.hcrhousepoints.Views.RecentPointSubmissionCard;
import com.hcrpurdue.jason.hcrhousepoints.Views.ResidentProfileToolbar;
import com.hcrpurdue.jason.hcrhousepoints.Views.RewardCard;


public class FHPProfileFragment extends BaseProfileFragment {

    // TODO: What do these objects do?
    private ResidentProfileToolbar residentProfileToolbar;
    private HouseCompetitionCard houseCompetitionCard;
    private RewardCard rewardCard;
    private RecentPointSubmissionCard recentPointSubmissionCard;


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

        flu.getRewardsListener().addCallback(PROFILE_FRAGMENT, new ListenerCallbackInterface() {
            @Override
            public void onUpdate() {
                handleRewardUpdate();
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
        residentProfileToolbar = new ResidentProfileToolbar(context, view);
        houseCompetitionCard = new HouseCompetitionCard(context, view);
        rewardCard = new RewardCard(context, view.findViewById(R.id.profile_reward_card), cacheManager.getSystemPreferences().shouldShowRewards());
        recentPointSubmissionCard = new RecentPointSubmissionCard(context, view);
    }


    /**
     * This method will be called when the userPointLogListener gets activated by Firebase. This will
     *  update the notifications Icon to show if there are new messages.
     */
    private void handleNotificationsUpdate(){
        if(notificationsButton != null)
            notificationsButton.handleNotificationUpdate();
        if(recentPointSubmissionCard != null)
            recentPointSubmissionCard.handleNotificationsUpdate();
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

    private void handleRewardUpdate() {
        if(rewardCard != null)
            rewardCard.handleRewardUpdate();
    }

    private void handleSystemPreferenceUpdate(){
        if(houseCompetitionCard != null){
            houseCompetitionCard.handleSysPrefUpdate();
        }
        if(residentProfileToolbar != null){
            residentProfileToolbar.handleSysPrefUpdate();
        }
        if(rewardCard != null){
            rewardCard.handleSystemPreferencesUpdate();
        }
    }
}
