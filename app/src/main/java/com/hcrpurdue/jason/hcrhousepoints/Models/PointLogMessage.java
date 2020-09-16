package com.hcrpurdue.jason.hcrhousepoints.Models;


import com.google.firebase.Timestamp;
import com.hcrpurdue.jason.hcrhousepoints.Models.Enums.UserPermissionLevel;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PointLogMessage {

    private String message;
    private String senderFirstName;
    private String senderLastName;
    private UserPermissionLevel senderPermissionLevel;
    private Timestamp messageCreationDate;
    private MessageType messageType;

    public static final String MESSAGE_KEY = "Message";
    public static final String SENDER_FIRST_NAME_KEY = "SenderFirstName";
    public static final String SENDER_LAST_NAME_KEY = "SenderLastName";
    public static final String SENDER_PERMISSION_LEVEL_KEY = "SenderPermissionLevel";
    public static final String MESSAGE_CREATION_DATE_KEY = "CreationDate";
    public static final String MESSAGE_TYPE_KEY = "MessageType";


    public PointLogMessage(String message, String senderFirstName, String senderLastName, UserPermissionLevel senderPermissionLevel, MessageType messageType) {
        this.message = message;
        this.senderFirstName = senderFirstName;
        this.senderLastName = senderLastName;
        this.senderPermissionLevel = senderPermissionLevel;
        messageCreationDate = Timestamp.now();
        this.messageType = messageType;
    }

    public PointLogMessage(Map<String, Object> document) {
        this.message = (String) document.get(MESSAGE_KEY);
        this.senderFirstName = (String) document.get(SENDER_FIRST_NAME_KEY);
        this.senderLastName = (String) document.get(SENDER_LAST_NAME_KEY);
        this.senderPermissionLevel = UserPermissionLevel.fromServerValue(((Long) document.get(SENDER_PERMISSION_LEVEL_KEY)).intValue());
        messageCreationDate = (Timestamp) document.get(MESSAGE_CREATION_DATE_KEY);
        this.messageType = MessageType.getMessageTypeFromString((String) document.get(MESSAGE_TYPE_KEY));
    }

    public Map<String, Object> generateFirebaseMap(){
        HashMap<String,Object> map = new HashMap<>();
        map.put(MESSAGE_KEY,this.message);
        map.put(SENDER_FIRST_NAME_KEY,this.senderFirstName);
        map.put(SENDER_LAST_NAME_KEY,this.senderLastName);
        map.put(SENDER_PERMISSION_LEVEL_KEY,this.senderPermissionLevel.getServerValue());
        map.put(MESSAGE_CREATION_DATE_KEY,messageCreationDate);
        map.put(MESSAGE_TYPE_KEY,messageType.getValue());
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

    public UserPermissionLevel getSenderPermissionLevel() {
        return senderPermissionLevel;
    }

    public Timestamp getMessageCreationDate(){
        return messageCreationDate;
    }
}
