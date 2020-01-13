package com.hcrpurdue.jason.hcrhousepoints.Models;

import androidx.annotation.NonNull;

import com.hcrpurdue.jason.hcrhousepoints.Models.Enums.UserPermissionLevel;

import java.io.Serializable;
import java.util.Map;

public class PointType implements Comparable<PointType>, Serializable {

    private int value;
    private String name;
    private String description;
    private boolean residentsCanSubmit;
    private int id;
    private boolean isEnabled;
    private int permissionLevel;

    public PointType(int value, String name, String description, boolean residentsCanSubmit, int id, boolean isEnabled, int permissionLevel) {
        this.value = value;
        this.name = name;
        this.description = description;
        this.residentsCanSubmit = residentsCanSubmit;
        this.id = id;
        this.isEnabled = isEnabled;
        this.permissionLevel = permissionLevel;
    }

    /**
     * Initialization method to create PointType from Firebase Document map
     * @param id    Firebase Id of the document
     * @param data  Map of document from firebase
     */
    public PointType(Integer id, Map<String,Object> data){
        this.value = ((Long) data.get("Value")).intValue();
        this.name = (String) data.get("Name");
        this.description = (String) data.get("Description");
        this.residentsCanSubmit = (boolean) data.get("ResidentsCanSubmit");
        this.id = id;
        this.isEnabled = (boolean) data.get("Enabled");
        this.permissionLevel = ((Long) data.get("PermissionLevel")).intValue();
    }


    public int getValue() {
        return value;
    }

    public String getName() { return name; }

    public String getPointDescription() {
        return description;
    }

    public Boolean getResidentsCanSubmit() {
        return residentsCanSubmit;
    }

    public int getId() {
        return id;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public boolean getUserCanGenerateQRCodes(UserPermissionLevel userPermissionLevel){
        if(userPermissionLevel == UserPermissionLevel.RHP){
            return permissionLevel > 1;
        } else if(userPermissionLevel == UserPermissionLevel.PROFESSIONAL_STAFF){
            return true;
        } else if(userPermissionLevel == UserPermissionLevel.FHP){
            return permissionLevel > 2;
        } else if(userPermissionLevel == UserPermissionLevel.PRIVILEGED_RESIDENT){
            return permissionLevel > 2;
        }
        return false;
    }


    @Override
    public int compareTo(@NonNull PointType other) {
        if(value == other.value)
            return id - other.id;
        return value - other.value;
    }

}
