package com.hcrpurdue.jason.hcrhousepoints.Models;

public class ResponseCodeMessage extends ResponseMessage {

    private int responseCode;

    public ResponseCodeMessage(int responseCode, String message){
        super(message);
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return responseCode;
    }
}
