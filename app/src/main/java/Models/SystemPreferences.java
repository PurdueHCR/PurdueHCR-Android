package Models;

public class SystemPreferences {

    private boolean isHouseEnabled;
    private String houseIsEnabledMsg;


    public void setHouseEnabled(boolean houseEnabled) {
        isHouseEnabled = houseEnabled;
    }

    public SystemPreferences(boolean isHouseEnabled, String houseIsEnabledMsg) {
        this.isHouseEnabled = isHouseEnabled;
        this.houseIsEnabledMsg = houseIsEnabledMsg;
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
}
