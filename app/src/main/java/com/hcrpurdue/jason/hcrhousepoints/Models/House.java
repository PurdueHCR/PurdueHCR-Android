package com.hcrpurdue.jason.hcrhousepoints.Models;

import androidx.annotation.NonNull;

import java.util.Map;

public class House implements Comparable<House> {

    public static String COLOR_KEY = "Color";
    public static String NUMBER_OF_RESIDENTS_KEY = "NumberOfResidents";
    public static String TOTAL_POINTS_KEY = "TotalPoints";
    public static String DOWNLOAD_URL_KEY = "DownloadURL";


    private int numResidents;
    private int totalPoints;
    private String name;
    private String downloadURL;

    public House(String n, int residents, int points) {
        name = n;
        numResidents = residents;
        totalPoints = points;
    }

    public House(String houseName, Map<String,Object> firestoreData){
        this.numResidents = ((Long) firestoreData.get(NUMBER_OF_RESIDENTS_KEY)).intValue();
        this.name = houseName;
        this.totalPoints = ((Long) firestoreData.get(TOTAL_POINTS_KEY)).intValue();
        this.downloadURL = (String) firestoreData.get(DOWNLOAD_URL_KEY);
    }

    public String getName() {
        return name;
    }

    public int getNumResidents() {
        return numResidents;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public String getDownloadURL(){
        return downloadURL;
    }

    public float getPointsPerResident(){
        return (float) ((totalPoints * 1.0) / numResidents);
    }

    @Override
    public int compareTo(@NonNull House house) {
        return Float.compare(house.getPointsPerResident(), getPointsPerResident());
    }
}
