package com.hcrpurdue.jason.hcrhousepoints.Views;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hcrpurdue.jason.hcrhousepoints.R;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.CacheManagementInterface;

public class ResidentProfileToolbar {
    private TextView userPointTotalTextView;
    private TextView userHouseRankTextView;
    private TextView houseNameTextView;
    private ImageView houseImageView;

    private CacheManager cacheManager;
    private Resources resources;
    private String packageName;
    private Context context;
    private int userRank;


    public ResidentProfileToolbar(Context context, View parentView){
        houseNameTextView = parentView.findViewById(R.id.house_name_text_view);
        userPointTotalTextView = parentView.findViewById(R.id.your_points_text_view);
        userHouseRankTextView = parentView.findViewById(R.id.house_rank_text_view);
        houseImageView = parentView.findViewById(R.id.profile_house_image_view);
        this.context = context;
        this.cacheManager = CacheManager.getInstance(context);
        this.resources = context.getResources();
        this.packageName = context.getPackageName();
        refreshRank();
        populateTopBar();
    }

    /**
     * SEt text on top Bar
     */
    private void populateTopBar(){
        int drawableID = resources.getIdentifier(cacheManager.getHouseName().toLowerCase(), "drawable", packageName);

        houseImageView.setImageResource(drawableID);
        houseNameTextView.setText(cacheManager.getHouseAndPermissionName());
        String userPointsText = "" + cacheManager.getUser().getTotalPoints();
        String houseRankText = (userRank != -1)? "# "+userRank: "";
        userPointTotalTextView.setText(userPointsText);
        userHouseRankTextView.setText(houseRankText);

    }

    private void refreshRank(){

        cacheManager.getUserRank(context, new CacheManagementInterface() {
            @Override
            public void onError(Exception e, Context context) {
                System.out.println(e.getMessage());
                userRank = -1;
            }

            @Override
            public void onGetRank(Integer rank) {
                userRank = rank;
                populateTopBar();
            }
        });
    }


    /**
     * Call this method when the UserListener fires
     */
    public void handleUserUpdate(){
        populateTopBar();
    }

}
