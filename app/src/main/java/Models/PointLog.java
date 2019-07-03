package Models;

import android.content.Context;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.Singleton;

public class PointLog implements Serializable {

    //TODO: 1: /  2: (2 *)  3: Enter

    public final String REJECTED_STRING = "DENIED: ";
    public final String SHREVE_RESIDENT = "(Shreve) ";

    //Variable names from Firebase

    private String approvedBy;
    private Date approvedOn;
    private String pointDescription;
    private String floorID;
    private PointType type;
    private String resident;
    private DocumentReference residentRef;
    private Date residentReportTime;
    private String logID;
    private List<PointLogMessage> messages;


    private boolean wasHandled;

    /**
     * Initialization for newly created points before they are created in Firebase. If these points are being pulled
     * from Firebase database, use the other init method.
     *
     * @param pointDescription - Description of the point
     * @param resident         - Name of the resident
     * @param type             - Type of point for log
     * @param floorID          - ID of the floor on which the user lives (e.g. 2N)
     * @param residentRef      - Firebase reference for the user
     */
    public PointLog(String pointDescription, String resident, PointType type, String floorID,
                    DocumentReference residentRef) {
        this.pointDescription = pointDescription;
        this.type = type;
        this.resident = resident;
        this.floorID = floorID;
        this.residentRef = residentRef;
        this.residentReportTime = new Date();
        this.wasHandled = false;
        this.messages = new ArrayList<>();
    }

    /**
     * Initializes point from Firebase
     *
     * @param id       - ID of the point in Firebase
     * @param document - Dictionary returned from Firebase request
     * @param context  - Context of current activity
     */
    public PointLog(String id, Map<String, Object> document, Context context) {

        this.logID = id;

        this.floorID = (String) document.get("FloorID");
        this.pointDescription = (String) document.get("Description");
        this.resident = (String) document.get("Resident");
        this.residentRef = (DocumentReference) document.get("ResidentRef");
        this.approvedBy = (String) document.get("ApprovedBy");
//        this.approvedOn = (Date) document.get("ApprovedOn");
//        this.residentReportTime = (Timestamp) document.get("ResidentReportTime");

        this.residentReportTime = (Date) document.get("ResidentReportTime");
        this.approvedOn = (Date) document.get("ApprovedOn");

        int idValue = ((Long) document.get("PointTypeID")).intValue();

        if (idValue < 1) {

            this.wasHandled = false;
        } else {

            this.wasHandled = true;
        }

        this.type = Singleton.getInstance(context).getPointTypeWithID(Math.abs(idValue));

        if (floorID.equals("Shreve")) {
            resident = SHREVE_RESIDENT + resident;
        }

        this.messages = new ArrayList<>();

    }


    public boolean wasRejected() {

        return this.pointDescription.contains(REJECTED_STRING);
    }


    /**
     * Changes the value of the point log when it is being rejected or approved
     * Notes: It doesn't matter if the point was already handled. Works properly either way.
     *
     *
     *
     * @param approved - Bool point is approved
     * @param preapproved - Bool point is preapproved
     * @param context - Context of current activity
     */
    public void updateApprovalStatus(boolean approved, boolean preapproved, Context context) {
        wasHandled = true;


        if (approved) {

            //Approves the point
            if (wasRejected()) {
                //Point was previously rejected
                this.pointDescription = pointDescription.replace(REJECTED_STRING, "");
            }


            if (preapproved) {
                this.approvedBy = "Preapproved";
            } else {
                this.approvedBy = Singleton.getInstance(context).getName();
            }

            this.approvedOn = new Date();

        }

        else {
            //Rejecting the point

            if(!wasRejected()) {
                this.pointDescription = REJECTED_STRING + pointDescription;
                this.approvedBy = Singleton.getInstance(context).getName();
                this.approvedOn = new Date();
            }

        }
    }


    public void updateApprovalStatus(boolean approved, Context context) {
        updateApprovalStatus(approved, false, context);
    }

    //TODO: Comment
    public HashMap<String, Object> convertToDict() {

        int pointTypeIDValue = this.type.getPointID();

        if(!wasHandled) {
            pointTypeIDValue = pointTypeIDValue * -1;
        }

        String residentName = this.resident;

        if(floorID.equals("Shreve")) {
            residentName = residentName.replace(SHREVE_RESIDENT, "");
        }

        HashMap<String, Object> dict = new HashMap<String, Object>();

        dict.put("Description", this.pointDescription);
        dict.put("FloorID", this.floorID);
        dict.put("PointTypeID", pointTypeIDValue);
        dict.put("Resident", residentName);
        dict.put("ResidentRef", this.residentRef);
        dict.put("ResidentReportTime", this.residentReportTime);

        if(this.approvedBy != null) {
            dict.put("ApprovedBy", this.approvedBy);
        }

        if(this.approvedOn != null) {
            dict.put("ApprovedOn", this.approvedOn);
        }

        return dict;

    }



    /**
     * @return
     */
    public PointType getPointType() {
        return this.type;
    }


    public void setLogID(String id) {
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

    public void setResidentRef(DocumentReference ref) {
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

    public Boolean wasHandled() { return  wasHandled; }

    public List<PointLogMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<PointLogMessage> msg){
        this.messages = msg;
    }
}