package com.hcrpurdue.jason.hcrhousepoints.Models;

import androidx.annotation.NonNull;

public class Reward implements Comparable<Reward>{

    public static final String FILE_NAME_KEY = "FileName";
    public static final String REQUIRED_PPR_KEY = "RequiredPPR";
    public static final String REQUIRED_VALUE_KEY = "RequiredValue";

    private String name;
    private int requiredPoints;
    private float pointsPerResident;
    private int imageResource;

    public Reward(String n, int points, float pointsPerResident, int resource){
        name = n;
        requiredPoints = points;
        imageResource = resource;
        this.pointsPerResident = pointsPerResident;
    }

    public String getName() {
        return name;
    }

    public int getImageResource() {
        return imageResource;
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
