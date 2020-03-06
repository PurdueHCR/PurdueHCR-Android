package com.hcrpurdue.jason.hcrhousepoints.Models;

import com.hcrpurdue.jason.hcrhousepoints.BuildConfig;

import java.util.Map;

public class SystemPreferences {



    private boolean isHouseEnabled;
    private String houseIsEnabledMsg;
    private String appVersion;
    private boolean isCompetitionVisible;


    public void setHouseEnabled(boolean houseEnabled) {
        isHouseEnabled = houseEnabled;
    }

    public SystemPreferences(Map<String, Object> data) {
        this.isHouseEnabled = (boolean) data.get("isHouseEnabled");
        this.houseIsEnabledMsg = (String) data.get("houseEnabledMessage");
        this.appVersion = (String) data.get("Android_Version");
        this.isCompetitionVisible = (boolean) data.get("isCompetitionVisible");
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
        return appVersion.equals(BuildConfig.VERSION_NAME);
    }

    public boolean isCompetitionVisible() {
        return isCompetitionVisible;
    }

    public void updateValues(Map<String, Object> data) {
        this.isHouseEnabled = (boolean) data.get("isHouseEnabled");
        this.houseIsEnabledMsg = (String) data.get("houseEnabledMessage");
        this.appVersion = (String) data.get("Version");
        this.isCompetitionVisible = (boolean) data.get("isCompetitionVisible");
    }
}
