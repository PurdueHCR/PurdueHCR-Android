package com.hcrpurdue.jason.hcrhousepoints.Models.Enums;

public enum UserPermissionLevel {

    RESIDENT            (0),
    RHP                 (1),
    PROFESSIONAL_STAFF  (2),
    FHP                 (3),
    PRIVILEGED_RESIDENT(4);

    //Int value saved on the server for the levels
    private final int serverValue;

    /**
     * Constructor for UserPermissionLevel
     * @param value Integer that relates to this level
     */
    UserPermissionLevel(int value){
        this.serverValue = value;
    }

    /**
     * Get the Integer that the server uses to track permission level
     * @return
     */
    public int getServerValue(){
        return this.serverValue;
    }

    /**
     * Check if this permission level represents an account that can submit points
     * @return true if user is Resident, RHP, or Privileged_Resident
     */
    public boolean canSubmitPoints(){
        return serverValue == 0 || serverValue == 1 || serverValue == 4;
    }


    /**
     * Given an integer from a server, return the correct UserPermissionLevel that matches
     * @param value     Integer from server
     * @return  UserPermissionLevel that matches. Default is Resident
     */
    public static UserPermissionLevel fromServerValue(int value){
        switch (value){
            case 1:
                return RHP;
            case 2:
                return PROFESSIONAL_STAFF;
            case 3:
                return FHP;
            case 4:
                return PRIVILEGED_RESIDENT;
            default:
                return RESIDENT;
        }
    }

}
