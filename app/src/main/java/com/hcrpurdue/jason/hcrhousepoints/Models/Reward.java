package com.hcrpurdue.jason.hcrhousepoints.Models;

import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.NonNull;

import java.util.Map;

public class Reward implements Comparable<Reward>{

    public static final String FILE_NAME_KEY = "FileName";
    public static final String REQUIRED_PPR_KEY = "RequiredPPR";
    public static final String NAME_KEY = "Name";
    public static final String DOWNLOAD_URL_KEY = "DownloadURL";



    private String id;
    private String name;
    private float pointsPerResident;
    private String fileName;
    private String downloadURL;


    public Reward(String id, Map<String, Object> data) {
        this.id = id;
        this.name = (String) data.get(Reward.NAME_KEY);
        this.fileName = (String) data.get(Reward.FILE_NAME_KEY);
        this.pointsPerResident = ((Long) data.get(Reward.REQUIRED_PPR_KEY)).floatValue();
        this.downloadURL = (String) data.get(Reward.DOWNLOAD_URL_KEY);
    }

    public String getName() {
        return name;
    }

    //TODO Delete this and replace with usage of downloadURL
    public int getIconResource(Context context){
        String rewardIcon = fileName.replace(".png", "").toLowerCase();
        return context.getResources().getIdentifier(rewardIcon, "drawable", context.getPackageName());
    }

    public String getDownloadURL(){
        return this.downloadURL;
    }


    public float getRequiredPointsPerResident() {
        return pointsPerResident;
    }

    @Override
    public int compareTo(@NonNull Reward reward) {
        return (int) (pointsPerResident - reward.getRequiredPointsPerResident());
    }
}
