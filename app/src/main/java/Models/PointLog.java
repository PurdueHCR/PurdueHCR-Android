package Models;

import com.google.firebase.firestore.DocumentReference;

public class PointLog {
    String pointDescription;
    PointType type;
    String resident;
    DocumentReference residentRef;
    String floorID;
    String logID;

    public PointLog(String pointDescription, String resident, PointType type, String floorID, DocumentReference residentRef){
        this.pointDescription = pointDescription;
        this.type = type;
        this.resident = resident;
        this.floorID = floorID;
        this.residentRef = residentRef;
    }

    public void setLogID(String logID){
        this.logID = logID;
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