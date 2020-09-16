package com.hcrpurdue.jason.hcrhousepoints.Models;

import com.hcrpurdue.jason.hcrhousepoints.Models.Enums.UserPermissionLevel;

import java.util.HashMap;
import java.util.Map;

public class User {

    public static final String FIRST_NAME_KEY = "FirstName";
    public static final String LAST_NAME_KEY = "LastName";
    public static final String FLOOR_ID_KEY = "FloorID";
    public static final String HOUSE_KEY = "House";
    public static final String PERMISSION_LEVEL_KEY = "Permission Level";
    public static final String TOTAL_POINTS_KEY = "TotalPoints";
    public static final String SEMESTER_POINTS = "SemesterPoints";
    public static final String ENABLED = "Enabled";

    private String firstName;
    private String floorId;
    private String house;
    private String lastName;
    private int semesterPoints;
    private boolean enabled;
    private int permissionLevel;
    private int totalPoints;
    private String id;

    public User(String userId, String firstName, String lastName, String floorId, String house,  int permissionLevel, int totalPoints, int semesterPoints, boolean isEnabled){
        this.id = userId;
        this.firstName = firstName;
        this.floorId = floorId;
        this.house = house;
        this.lastName = lastName;
        this.permissionLevel = permissionLevel;
        this.totalPoints = totalPoints;
        this.semesterPoints = semesterPoints;
        this.enabled = isEnabled;
    }

    public User(String id, Map<String,Object> data){
        this.id = id;
        this.firstName = (String) data.get(FIRST_NAME_KEY);
        this.floorId = (String) data.get(FLOOR_ID_KEY);
        this.house = (String) data.get(HOUSE_KEY);
        this.lastName = (String) data.get(LAST_NAME_KEY);
        this.permissionLevel = ((Long) data.get(PERMISSION_LEVEL_KEY)).intValue();
        this.totalPoints = ((Long) data.get(TOTAL_POINTS_KEY)).intValue();
        this.semesterPoints = ((Long) data.get(SEMESTER_POINTS)).intValue();
        this.enabled = (boolean) data.get(ENABLED);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getUserId(){
        return id;
    }

    public String getFloorId() {
        return floorId;
    }

    public String getHouseName() {
        return house;
    }

    public String getLastName() {
        return lastName;
    }

    public int getSemesterPoints(){ return this.semesterPoints; }

    public boolean isEnabled() { return enabled; }

    public UserPermissionLevel getPermissionLevel() {
        return UserPermissionLevel.fromServerValue(this.permissionLevel);
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }


}
