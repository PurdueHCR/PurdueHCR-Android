package Models;

import androidx.annotation.NonNull;

public class PointType implements Comparable<PointType> {

    private int pointValue;
    private String pointDescription;
    private boolean residentsCanSubmit;
    private int pointID;
    private boolean isEnabled;
    private int permissionLevel;

    public PointType(int pointValue, String pointDescription, boolean residentsCanSubmit, int pointID, boolean isEnabled, int permissionLevel) {
        this.pointValue = pointValue;
        this.pointDescription = pointDescription;
        this.residentsCanSubmit = residentsCanSubmit;
        this.pointID = pointID;
        this.isEnabled = isEnabled;
        this.permissionLevel = permissionLevel;
    }

    //TODO: add getter for permLev


    public int getPointValue() {
        return pointValue;
    }

    public String getPointDescription() {
        return pointDescription;
    }

    public Boolean getResidentsCanSubmit() {
        return residentsCanSubmit;
    }

    public int getPointID() {
        return pointID;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public int getPermissionLevel() {
        return permissionLevel;
    }

    @Override
    public int compareTo(@NonNull PointType other) {
        if (residentsCanSubmit == other.residentsCanSubmit) {
            if(pointValue == other.pointValue)
                return pointID - other.pointID;
            return pointValue - other.pointValue;
        }
        return Boolean.compare(other.residentsCanSubmit, residentsCanSubmit);
    }
}
