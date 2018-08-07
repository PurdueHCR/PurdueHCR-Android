package Models;

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