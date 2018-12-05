package Models;

import android.graphics.Point;
import android.view.animation.PathInterpolator;

import com.google.firebase.firestore.DocumentReference;

public class PointLog {
    private String pointDescription;
    private PointType type;
    private String resident;
    private DocumentReference residentRef;
    private String floorID;
    private String logID;

    public PointLog(String pointDescription, String resident, PointType type, String floorID){
        this.pointDescription = pointDescription;
        this.type = type;
        this.resident = resident;
        this.floorID = floorID;
        this.residentRef = null;
    }


    public PointType getPointType() {
        return this.type;
    }


    public void setLogID(String id){
        this.logID = id;
    }

    public String getPointDescription() {
        return pointDescription;
    }

    public PointType getType() {
        return type;
    }

    public String getResident() {
        return resident;
    }

    public void setResidentRef(DocumentReference ref){
        residentRef = ref;
    }

    public DocumentReference getResidentRef() {
        return residentRef;
    }

    public String getFloorID() {
        return floorID;
    }

    public String getLogID() {
        return logID;
    }

}