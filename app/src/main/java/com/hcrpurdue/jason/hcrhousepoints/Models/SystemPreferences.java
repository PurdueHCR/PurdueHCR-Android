package com.hcrpurdue.jason.hcrhousepoints.Models;

import java.util.Map;

public class SystemPreferences {

    private static final String CURRENT_APP_VERSION = "2.0.0";


    private boolean isHouseEnabled;
    private String houseIsEnabledMsg;
    private String appVersion;


    public void setHouseEnabled(boolean houseEnabled) {
        isHouseEnabled = houseEnabled;
    }

    public SystemPreferences(Map<String,Object> data) {
        this.isHouseEnabled = (boolean) data.get("isHouseEnabled");
        this.houseIsEnabledMsg = (String) data.get("houseEnabledMessage");
        this.appVersion = (String) data.get("Android_Version");
    }

    public String getHouseIsEnabledMsg() {
        return houseIsEnabledMsg;
    }

    public void setHouseIsEnabledMsg(String houseIsEnabledMsg) {
        this.houseIsEnabledMsg = houseIsEnabledMsg;
    }

    public boolean isHouseEnabled() {
        return isHouseEnabled;
    }

    public boolean isAppUpToDate() {
        return appVersion.equals(CURRENT_APP_VERSION);
    }

    public void updateValues(Map<String,Object> data) {
        this.isHouseEnabled = (boolean) data.get("isHouseEnabled");
        this.houseIsEnabledMsg = (String) data.get("houseEnabledMessage");
        this.appVersion = (String) data.get("Version");
    }
}
