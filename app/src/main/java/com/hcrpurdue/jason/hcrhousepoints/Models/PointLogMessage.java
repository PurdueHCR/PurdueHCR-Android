package com.hcrpurdue.jason.hcrhousepoints.Models;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PointLogMessage {

    private String message;
    private String senderFirstName;
    private String senderLastName;
    private int senderPermissionLevel;
    private Date messageCreationDate;

    public static final String MESSAGE_KEY = "Message";
    public static final String SENDER_FIRST_NAME_KEY = "SenderFirstName";
    public static final String SENDER_LAST_NAME_KEY = "SenderLastName";
    public static final String SENDER_PERMISSION_LEVEL_KEY = "SenderPermissionLevel";
    public static final String MESSAGE_CREATION_DATE_KEY = "CreationDate";


    public PointLogMessage(String message, String senderFirstName, String senderLastName, int senderPermissionLevel) {
        this.message = message;
        this.senderFirstName = senderFirstName;
        this.senderLastName = senderLastName;
        this.senderPermissionLevel = senderPermissionLevel;
        messageCreationDate = new Date();
    }

    public PointLogMessage(Map<String, Object> document) {
        this.message = (String) document.get(MESSAGE_KEY);
        this.senderFirstName = (String) document.get(SENDER_FIRST_NAME_KEY);
        this.senderLastName = (String) document.get(SENDER_LAST_NAME_KEY);
        this.senderPermissionLevel = ((Long) document.get(SENDER_PERMISSION_LEVEL_KEY)).intValue();
        messageCreationDate = (Date) document.get(MESSAGE_CREATION_DATE_KEY);
    }

    public Map<String, Object> generateFirebaseMap(){
        HashMap<String,Object> map = new HashMap<>();
        map.put(MESSAGE_KEY,this.message);
        map.put(SENDER_FIRST_NAME_KEY,this.senderFirstName);
        map.put(SENDER_LAST_NAME_KEY,this.senderLastName);
        map.put(SENDER_PERMISSION_LEVEL_KEY,this.senderPermissionLevel);
        map.put(MESSAGE_CREATION_DATE_KEY,messageCreationDate);
        return map;
    }


    public String getMessage() {
        return message;
    }

    public String getSenderFirstName() {
        return senderFirstName;
    }

    public String getSenderLastName() {
        return senderLastName;
    }

    public int getSenderPermissionLevel() {
        return senderPermissionLevel;
    }

    public Date getMessageCreationDate(){
        return messageCreationDate;
    }
}
