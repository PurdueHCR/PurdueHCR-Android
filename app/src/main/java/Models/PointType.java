package Models;

import android.support.annotation.NonNull;

public class PointType implements Comparable<PointType> {

    private int pointValue;
    private String pointDescription;
    private Boolean residentsCanSubmit;
    private int pointID;

    public PointType(int pointValue, String pointDescription, Boolean residentsCanSubmit, int pointID) {
        this.pointValue = pointValue;
        this.pointDescription = pointDescription;
        this.residentsCanSubmit = residentsCanSubmit;
        this.pointID = pointID;
    }

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

    @Override
    public int compareTo(@NonNull PointType other) {
        return pointID - other.pointID;
    }
}
