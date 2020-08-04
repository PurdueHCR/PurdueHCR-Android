package com.hcrpurdue.jason.hcrhousepoints.Views;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hcrpurdue.jason.hcrhousepoints.Models.Reward;
import com.hcrpurdue.jason.hcrhousepoints.R;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;
import com.hcrpurdue.jason.hcrhousepoints.Utils.ImageCacheManager;

import java.util.Collections;
import java.util.Locale;

public class RewardCard {
    private TextView nextRewardNameTextView;
    private TextView nextRewardPPRTextView;
    private ImageView nextRewardImageView;
    private ProgressBar nextRewardProgressBar;
    private View rewardsCardParentView;
    private boolean shouldShow;


    private CacheManager cacheManager;
    private ImageCacheManager imageCacheManager;

    public RewardCard(Context context, View parentView, boolean shouldShow){
        this.rewardsCardParentView = parentView;
        this.shouldShow = shouldShow;
        nextRewardImageView = parentView.findViewById(R.id.next_reward_image_view);
        nextRewardNameTextView = parentView.findViewById(R.id.next_reward_text_view);
        nextRewardPPRTextView = parentView.findViewById(R.id.ppr_requirement_label);
        nextRewardProgressBar = parentView.findViewById(R.id.next_reward_progress_bar);

        imageCacheManager = ImageCacheManager.getInstance();
        this.cacheManager = CacheManager.getInstance(context);
        populateRewardCard();

        if(!shouldShow){
            rewardsCardParentView.setVisibility(View.GONE);
        }
    }

    public void setShouldShow(boolean showRewards){
        this.shouldShow = showRewards;
        if(!shouldShow){
            rewardsCardParentView.setVisibility(View.GONE);
        }
        else{
            rewardsCardParentView.setVisibility(View.VISIBLE);
        }
    }

    private void populateRewardCard(){
        float requiredPPR = 0F;
        float previousPPR = 0F;
        if(cacheManager.getUserHouse() != null){
            float pointsPerResident = cacheManager.getUserHouse().getPointsPerResident();
            int numberOfResidents = cacheManager.getUserHouse().getNumResidents();

            Collections.sort(cacheManager.getRewards());
            for (Reward reward : cacheManager.getRewards()) {
                requiredPPR = reward.getRequiredPointsPerResident();
                if (pointsPerResident < requiredPPR) {
                    nextRewardPPRTextView.setVisibility(View.VISIBLE);
                    nextRewardPPRTextView.setText(String.format(Locale.getDefault(), "%.0f PPR", reward.getRequiredPointsPerResident()));
                    nextRewardNameTextView.setText(reward.getName());
                    nextRewardProgressBar.setMax((int)(requiredPPR * numberOfResidents));
                    nextRewardProgressBar.setProgress((int)((pointsPerResident - previousPPR) * numberOfResidents));

                    if(reward.getDownloadURL() != null){
                        imageCacheManager.setImageViewFromDownloadURL(reward.getDownloadURL(), nextRewardImageView);
                    }
                    break;
                }
            }
            if (pointsPerResident >= requiredPPR) {
                nextRewardProgressBar.setMax(100);
                nextRewardProgressBar.setProgress(100);
                nextRewardImageView.setImageResource(R.drawable.ic_check);
                nextRewardNameTextView.setText("No rewards left to get!");
                nextRewardPPRTextView.setVisibility(View.INVISIBLE);
            }
        }

    }

    /**
     * Call this when the Rewards Listener is updated
     */
    public void handleRewardUpdate(){
        populateRewardCard();
    }


    public void handleSystemPreferencesUpdate(){
        System.out.println("Show new rewards");
        System.out.println("Should show reawrds: "+cacheManager.getSystemPreferences().shouldShowRewards());
        setShouldShow(cacheManager.getSystemPreferences().shouldShowRewards());
    }

}
