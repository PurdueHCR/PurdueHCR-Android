package com.hcrpurdue.jason.hcrhousepoints.Models;

public enum MessageType {
    COMMENT("comment"),
    APPROVE("approve"),
    REJECT("reject");

    private final String value;

    MessageType(String val){
        this.value = val;
    }

    public static MessageType getMessageTypeFromString(String str){
        switch (str){
            case "approve":
                return APPROVE;
            case "reject":
                return REJECT;
            default:
                return COMMENT;
        }
    }

    public String getValue(){
        return value;
    }
}
