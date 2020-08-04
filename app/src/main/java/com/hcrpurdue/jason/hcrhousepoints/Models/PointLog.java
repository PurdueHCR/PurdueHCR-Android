package com.hcrpurdue.jason.hcrhousepoints.Models;

import android.content.Context;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.Timestamp;
import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;

public class PointLog implements Comparable<PointLog>, Serializable {

    //TODO: 1: /  2: (2 *)  3: Enter

    public final String REJECTED_STRING = "DENIED: ";
    public final String SHREVE_RESIDENT = "(Shreve) ";

    //Firestore Keys
    public static final String FLOOR_ID_KEY = "FloorID";
    public static final String DESCRIPTION_KEY = "Description";
    public static final String FIRST_NAME_KEY = "ResidentFirstName";
    public static final String LAST_NAME_KEY = "ResidentLastName";
    public static final String APPROVED_BY_KEY = "ApprovedBy";
    public static final String DATE_SUBMITTED_KEY = "DateSubmitted";
    public static final String DATE_OCCURRED_KEY = "DateOccurred";
    public static final String APPROVED_ON_KEY = "ApprovedOn";
    public static final String RESIDENT_ID_KEY = "ResidentId";
    public static final String RESIDENT_NOTIF_KEY = "ResidentNotifications";
    public static final String RHP_NOTIF_KEY = "RHPNotifications";
    public static final String POINT_TYPE_ID_KEY = "PointTypeID";


    //Variable names from Firestore

    private String approvedBy;
    private Timestamp approvedOn;
    private String pointDescription;
    private String floorID;
    private PointType type;
    private String residentFirstName;
    private String residentLastName;
    private String residentId;
    private Timestamp dateSubmitted;
    private Timestamp dateOccurred;
    private String logID;
    private int residentNotifications;
    private int rhpNotifications;
    private List<PointLogMessage> messages;


    private boolean wasHandled;

    /**
     * Initialization for newly created points before they are created in Firebase. If these points are being pulled
     * from Firebase database, use the other init method.
     *
     * @param pointDescription - Description of the point
     * @param last             - Name of the resident
     * @param first            - Name of the resident
     * @param type             - Type of point for log
     * @param floorID          - ID of the floor on which the user lives (e.g. 2N)
     * @param residentId      - Firebase id for the user
     */
    public PointLog(String pointDescription, String first, String last, PointType type, String floorID, String residentId) {
        this(pointDescription,first,last,type,floorID,residentId, Timestamp.now());
    }

    public PointLog(String pointDescription, PointType type, User user){
        this(pointDescription, user.getFirstName(), user.getLastName(), type, user.getFloorId(), user.getUserId());
    }

    /**
     * Initialization for newly created points before they are created in Firebase. If these points are being pulled
     * from Firebase database, use the other init method.
     *
     * @param pointDescription - Description of the point
     * @param last             - Name of the resident
     * @param first            - Name of the resident
     * @param type             - Type of point for log
     * @param floorID          - ID of the floor on which the user lives (e.g. 2N)
     * @param residentId      - Firebase id for the user
     * @param dateOccurred    - Timestamp which the user reported that the thing for the point log occurred
     */
    public PointLog(String pointDescription, String first, String last, PointType type, String floorID, String residentId, Timestamp dateOccurred) {
        this.pointDescription = pointDescription;
        this.type = type;
        this.residentLastName = last;
        this.residentFirstName = first;
        this.floorID = floorID;
        this.dateSubmitted = Timestamp.now();
        this.wasHandled = false;
        this.messages = new ArrayList<>();
        this.residentId = residentId;
        this.dateOccurred = dateOccurred;
        this.residentNotifications = 0;
        this.rhpNotifications = 0;
    }

    public PointLog(String pointDescription, PointType type, Timestamp dateOccurred, User user){
        this(pointDescription, user.getFirstName(), user.getLastName(), type, user.getFloorId(), user.getUserId(), dateOccurred);
    }

    /**
     * Initializes point from Firebase
     *
     * @param id       - ID of the point in Firebase
     * @param document - Dictionary returned from Firebase request
     * @param context  - Context of current activity. Used to get the Point Types from the cacheManager
     */
    public PointLog(String id, Map<String, Object> document, Context context) {

        this.logID = id;

        this.floorID = (String) document.get(FLOOR_ID_KEY);
        this.pointDescription = (String) document.get(DESCRIPTION_KEY);
        this.residentFirstName = (String) document.get(FIRST_NAME_KEY);
        this.residentLastName = (String) document.get(LAST_NAME_KEY);
        this.approvedBy = (String) document.get(APPROVED_BY_KEY);
        this.dateSubmitted = (Timestamp) document.get(DATE_SUBMITTED_KEY);
        this.approvedOn = (Timestamp) document.get(APPROVED_ON_KEY);
        this.residentId = (String) document.get(RESIDENT_ID_KEY);
        this.dateOccurred = (Timestamp) document.get(DATE_OCCURRED_KEY);
        Long resNotifs = ((Long) document.get(RESIDENT_NOTIF_KEY));
        Long rhpNotifs = ((Long) document.get(RHP_NOTIF_KEY));
        if(resNotifs != null){
            this.residentNotifications = resNotifs.intValue();
        }
        if(rhpNotifs != null) {
            this.rhpNotifications = rhpNotifs.intValue();
        }

        int idValue = ((Long) document.get(POINT_TYPE_ID_KEY)).intValue();


        if (idValue < 1) {

            this.wasHandled = false;
        } else {

            this.wasHandled = true;
        }

        this.type = CacheManager.getInstance(context).getPointTypeWithID(Math.abs(idValue));

        if (floorID.equals("Shreve")) {
            residentFirstName = SHREVE_RESIDENT + residentFirstName;
        }

        this.messages = new ArrayList<>();

    }

    public HashMap<String, Object> convertToDict() {

        int pointTypeIDValue = this.type.getId();

        if(!wasHandled) {
            pointTypeIDValue = pointTypeIDValue * -1;
        }

        String residentName = this.residentFirstName;

        if(floorID.equals("Shreve")) {
            residentName = residentName.replace(SHREVE_RESIDENT, "");
        }

        HashMap<String, Object> dict = new HashMap<String, Object>();

        dict.put(DESCRIPTION_KEY, this.pointDescription);
        dict.put(FLOOR_ID_KEY, this.floorID);
        dict.put(POINT_TYPE_ID_KEY, pointTypeIDValue);
        dict.put(FIRST_NAME_KEY, residentName);
        dict.put(LAST_NAME_KEY, residentLastName);
        dict.put(DATE_SUBMITTED_KEY, this.dateSubmitted);
        dict.put(RESIDENT_ID_KEY, this.residentId);
        dict.put(RESIDENT_NOTIF_KEY, this.residentNotifications);
        dict.put(RHP_NOTIF_KEY, this.rhpNotifications);
        dict.put(DATE_OCCURRED_KEY, this.dateOccurred);

        if(this.approvedBy != null) {
            dict.put(APPROVED_BY_KEY, this.approvedBy);
        }

        if(this.approvedOn != null) {
            dict.put(APPROVED_ON_KEY, this.approvedOn);
        }

        return dict;

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
                this.approvedBy = CacheManager.getInstance(context).getName();
            }

            this.approvedOn = Timestamp.now();
        }

        else {
            //Rejecting the point
            if(!wasRejected()) {
                this.pointDescription = REJECTED_STRING + pointDescription;
                this.approvedBy = CacheManager.getInstance(context).getName();
                this.approvedOn = Timestamp.now();
            }
        }
    }


    public void updateApprovalStatus(boolean approved, Context context) {
        updateApprovalStatus(approved, false, context);
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

    public String getResidentFirstName() {
        return residentFirstName;
    }

    public String getResidentLastName() {
        return residentLastName;
    }

    public int getResidentNotifications() {
        return residentNotifications;
    }

    public int getRhpNotifications() {
        return rhpNotifications;
    }

    public String getResidentId() {
        return residentId;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public Timestamp getApprovedOn() {
        return approvedOn;
    }

    public Timestamp getDateOccurred(){
        return dateOccurred;
    }

    public Timestamp getDateSubmitted() { return dateSubmitted;}

    /**
     * This static method is used to create the map to update the number of notifications.
     * Local number of notifications on the Point Log will not update until Firestore Listener fires
     * @param notificationForResident    True: notification for resident is updated. False: update for RHP
     * @param shouldReset True: Reset the count to 0. False: dont
     * @return
     */
    public Map<String,Object> createNotificationsMap(boolean notificationForResident, boolean shouldReset){
        Map<String,Object> data = new HashMap<>();
        if(notificationForResident){
            //We don't update the local number of notifications because our Firestore Listener will update it once it has been saved in the database
            residentNotifications = (shouldReset?0:residentNotifications+1);
            data.put(RESIDENT_NOTIF_KEY,residentNotifications);

        }
        else{
            //We don't update the local number of notifications because our Firestore Listener will update it once it has been saved in the database
            rhpNotifications = (shouldReset?0:rhpNotifications+1);
            data.put(RHP_NOTIF_KEY,rhpNotifications);
        }
        return data;
    }

    /**
     * From an updated PointLog record, update this instance
     * @param toUpdate  PointLog which contains the updates.
     */
    public void updateValues(PointLog toUpdate){
        if(toUpdate.getLogID().equals(logID)){
            this.approvedBy = toUpdate.getApprovedBy();
            this.approvedOn = toUpdate.getApprovedOn();
            this.pointDescription = toUpdate.getPointDescription();
            this.residentNotifications = toUpdate.getResidentNotifications();
            this.rhpNotifications = toUpdate.getRhpNotifications();
        }
    }

    @Override
    public int compareTo(PointLog pointLog) {
        return pointLog.getDateSubmitted().compareTo(dateSubmitted);
    }
}