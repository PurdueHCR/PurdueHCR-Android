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

    private String firstName;
    private String floorId;
    private String houseName;
    private String lastName;
    private UserPermissionLevel permissionLevel;
    private int totalPoints;
    private String id;
    private String firebaseToken;

    public User(String firstName, String lastName, String floorId, String houseName,  UserPermissionLevel permissionLevel, int totalPoints){
        this.firstName = firstName;
        this.floorId = floorId;
        this.houseName = houseName;
        this.lastName = lastName;
        this.permissionLevel = permissionLevel;
        this.totalPoints = totalPoints;
    }
    public User(String userId, String firstName, String lastName, String floorId, String houseName,  UserPermissionLevel permissionLevel, int totalPoints){
        this.id = userId;
        this.firstName = firstName;
        this.floorId = floorId;
        this.houseName = houseName;
        this.lastName = lastName;
        this.permissionLevel = permissionLevel;
        this.totalPoints = totalPoints;
    }

    public User(String id, Map<String,Object> data){
        this.id = id;
        this.firstName = (String) data.get(FIRST_NAME_KEY);
        this.floorId = (String) data.get(FLOOR_ID_KEY);
        this.houseName = (String) data.get(HOUSE_KEY);
        this.lastName = (String) data.get(LAST_NAME_KEY);
        this.permissionLevel = UserPermissionLevel.fromServerValue(((Long) data.get(PERMISSION_LEVEL_KEY)).intValue());
        this.totalPoints = ((Long) data.get(TOTAL_POINTS_KEY)).intValue();
    }

    public Map<String, Object> convertToDict(){
        HashMap<String, Object> dict = new HashMap<String, Object>();
        dict.put(FIRST_NAME_KEY, this.firstName);
        dict.put(LAST_NAME_KEY, this.lastName);
        dict.put(FLOOR_ID_KEY, this.floorId);
        dict.put(HOUSE_KEY, this.houseName);
        dict.put(PERMISSION_LEVEL_KEY, this.permissionLevel.getServerValue());
        dict.put(TOTAL_POINTS_KEY, this.totalPoints);
        return dict;
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
        return houseName;
    }

    public String getLastName() {
        return lastName;
    }

    public UserPermissionLevel getPermissionLevel() {
        return permissionLevel;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public void setFirebaseToken(String token){
        this.firebaseToken = token;
    }

    public String getFirebaseToken(){
        return this.firebaseToken;
    }

}
