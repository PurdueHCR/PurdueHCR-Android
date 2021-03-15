package com.hcrpurdue.jason.hcrhousepoints.Views;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hcrpurdue.jason.hcrhousepoints.Models.AuthRank;
import com.hcrpurdue.jason.hcrhousepoints.Models.Enums.UserPermissionLevel;
import com.hcrpurdue.jason.hcrhousepoints.R;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;
import com.hcrpurdue.jason.hcrhousepoints.Utils.ImageCacheManager;
import com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces.CacheManagementInterface;

public class ResidentProfileToolbar {
    private TextView userPointTotalTextView;
    private TextView userHouseRankTextView;
    private TextView semesterRankTextView;
    private final TextView houseNameTextView;
    private final ImageView houseImageView;

    private final CacheManager cacheManager;
    private final Resources resources;
    private final String packageName;
    private final Context context;
    private AuthRank userRank;

    ImageCacheManager imageCacheManager;


    public ResidentProfileToolbar(Context context, View parentView) {
        System.out.println("INIT TOOLBAR");
        this.cacheManager = CacheManager.getInstance(context);
        houseNameTextView = parentView.findViewById(R.id.house_name_text_view);
        if (cacheManager.getPermissionLevel() != UserPermissionLevel.FHP || cacheManager.getPermissionLevel() != UserPermissionLevel.PROFESSIONAL_STAFF) {

            userPointTotalTextView = parentView.findViewById(R.id.your_points_text_view);
            userHouseRankTextView = parentView.findViewById(R.id.house_rank_text_view);
            semesterRankTextView = parentView.findViewById(R.id.semester_rank_text_view);
        }
        houseImageView = parentView.findViewById(R.id.profile_house_image_view);

        this.context = context;

        this.resources = context.getResources();
        this.packageName = context.getPackageName();
        imageCacheManager = ImageCacheManager.getInstance();
        if (cacheManager.getPermissionLevel() != UserPermissionLevel.FHP || cacheManager.getPermissionLevel() != UserPermissionLevel.PROFESSIONAL_STAFF) {

            refreshRank();
        }
        populateTopBar();

    }

    /**
     * SEt text on top Bar
     */
    private void populateTopBar() {
        System.out.println("POPULATE");
        int drawableID = resources.getIdentifier("hcr_icon_square", "drawable", packageName);

        houseImageView.setImageResource(drawableID);
        houseNameTextView.setText(cacheManager.getHouseAndPermissionName());
        if (cacheManager.getPermissionLevel() != UserPermissionLevel.FHP && cacheManager.getPermissionLevel() != UserPermissionLevel.PROFESSIONAL_STAFF) {
            String userPointsText = "" + cacheManager.getUser().getTotalPoints();
            String houseRankText = (userRank.getHouseRank() != -1) ? "# " + userRank.getHouseRank() : "";
            String semesterRankText = (userRank.getSemesterRank() != -1) ? "# " + userRank.getSemesterRank() : "";
            userPointTotalTextView.setText(userPointsText);
            userHouseRankTextView.setText(houseRankText);
            semesterRankTextView.setText(semesterRankText);
        } else {
            userPointTotalTextView.setVisibility(View.INVISIBLE);
            userHouseRankTextView.setVisibility(View.INVISIBLE);
            semesterRankTextView.setVisibility(View.INVISIBLE);
        }

        if (cacheManager.getUserHouse() != null && cacheManager.getUserHouse().getDownloadURL() != null) {
            imageCacheManager.setImageViewFromDownloadURL(cacheManager.getUserHouse().getDownloadURL(), houseImageView);
        }

    }

    private void refreshRank() {

        cacheManager.getUserRank(context, new CacheManagementInterface() {
            @Override
            public void onError(Exception e, Context context) {
                System.out.println(e.getMessage());
                userRank.setInvalid();
            }

            @Override
            public void onGetRank(AuthRank rank) {
                userRank = rank;
                populateTopBar();
            }
        });
    }


    /**
     * Call this method when the UserListener fires
     */
    public void handleUserUpdate() {
        populateTopBar();
    }

    public void handleSysPrefUpdate() {
        if (cacheManager.getSystemPreferences().isCompetitionVisible()) {
            if (cacheManager.getPermissionLevel() != UserPermissionLevel.FHP && cacheManager.getPermissionLevel() != UserPermissionLevel.PROFESSIONAL_STAFF) {

                userHouseRankTextView.setVisibility(View.VISIBLE);
                semesterRankTextView.setVisibility(View.VISIBLE);
            }
        } else {
            if (cacheManager.getPermissionLevel() != UserPermissionLevel.FHP && cacheManager.getPermissionLevel() != UserPermissionLevel.PROFESSIONAL_STAFF) {

                userHouseRankTextView.setVisibility(View.INVISIBLE);
                semesterRankTextView.setVisibility(View.INVISIBLE);
            }

        }
    }

    public void handleHousesUpdated() {
        populateTopBar();
    }


}
