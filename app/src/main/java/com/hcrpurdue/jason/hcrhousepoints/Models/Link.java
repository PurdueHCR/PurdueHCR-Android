package com.hcrpurdue.jason.hcrhousepoints.Models;

import android.content.Context;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;

public class Link implements Serializable {
    private String linkId;
    private String description;
    private String creatorId;
    private boolean singleUse;
    private int pointTypeId;
    private boolean isEnabled;
    private boolean isArchived;

    public Link(String linkId, String description, boolean singleUse, int pointTypeId, boolean isEnabled, boolean isArchived) {
        this.linkId = linkId;
        this.description = description;
        this.singleUse = singleUse;
        this.pointTypeId = pointTypeId;
        this.isEnabled = isEnabled;
        this.isArchived = isArchived;
    }

    public Link(String linkId, Map<String, Object> document){
        this.linkId = linkId;
        this.description = ((String) document.get("Description"));
        this.creatorId = ((String) document.get("CreatorID"));
        this.singleUse = ((boolean) document.get("SingleUse"));
        this.pointTypeId = ((Long)document.get("PointID")).intValue();
        this.isEnabled = ((boolean) document.get("Enabled"));
        this.isArchived = ((boolean) document.get("Archived"));
    }

    public Link(String userId, String description, boolean singleUse, int pointTypeId) {
        this.creatorId = userId;
        this.description = description;
        this.singleUse = singleUse;
        this.pointTypeId = pointTypeId;
        this.isEnabled = false;
        this.isArchived = false;
    }

    @Override
    public String toString() {
        return "Link{" +
                "linkId='" + linkId + '\'' +
                ", description='" + description + '\'' +
                ", singleUse=" + singleUse +
                ", pointTypeId=" + pointTypeId +
                ", isEnabled=" + isEnabled +
                ", isArchived=" + isArchived +
                '}';
    }

    public Map<String, Object> convertToDict() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("Description", getDescription());
        map.put("PointID", getPointTypeId());
        map.put("SingleUse", isSingleUse());
        map.put("CreatorID", creatorId);
        map.put("Enabled", isEnabled());
        map.put("Archived", isArchived());
        return map;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public void setSingleUse(boolean singleUse) {
        this.singleUse = singleUse;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public String getLinkId() {
        return linkId;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSingleUse() {
        return singleUse;
    }

    public int getPointTypeId() {
        return pointTypeId;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public boolean isArchived() {
        return isArchived;
    }

    /**
     *
     * @return the address that should be represented with this QR code
     */
    public String getAddress(){
        return "hcrpoint://addpoints/"+this.linkId;
    }

    public String getAndroidDeepLinkAddress(){
        return "intent://addpoints/"+this.linkId+"#Intent;scheme=hcrpoint;package=com.hcrpurdue.jason.hcrhousepoints;end";
    }

    public PointType getPointType(Context context){
        return CacheManager.getInstance(context).getPointTypeWithID(pointTypeId);
    }

}
