package com.hcrpurdue.jason.hcrhousepoints.Models;

import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.NonNull;

import java.util.Map;

public class Reward implements Comparable<Reward>{

    public static final String FILE_NAME_KEY = "FileName";
    public static final String REQUIRED_PPR_KEY = "RequiredPPR";
    public static final String REQUIRED_VALUE_KEY = "RequiredValue";

    private String name;
    private int requiredPoints;
    private float pointsPerResident;
    private String fileName;

//    public Reward(String n, int points, float pointsPerResident, int resource){
//        name = n;
//        requiredPoints = points;
//        imageResource = resource;
//        this.pointsPerResident = pointsPerResident;
//    }
//

    public Reward(String name, Map<String, Object> data) {
        this.name = name;
        this.fileName = (String) data.get("FileName");
        this.requiredPoints = ((Long) data.get("RequiredValue")).intValue();
        this.pointsPerResident = ((Long) data.get(Reward.REQUIRED_PPR_KEY)).floatValue();
    }

    public String getName() {
        return name;
    }


    public int getIconResource(Context context){
        String rewardIcon = fileName.replace(".png", "").toLowerCase();
        return context.getResources().getIdentifier(rewardIcon, "drawable", context.getPackageName());
    }

    public int getRequiredPoints() {
        return requiredPoints;
    }

    public float getRequiredPointsPerResident() {
        return pointsPerResident;
    }

    @Override
    public int compareTo(@NonNull Reward reward) {
        return requiredPoints - reward.requiredPoints;
    }
}
