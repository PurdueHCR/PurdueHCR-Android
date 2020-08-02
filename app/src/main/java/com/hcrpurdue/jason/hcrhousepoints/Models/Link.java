package com.hcrpurdue.jason.hcrhousepoints.Models;

import android.content.Context;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.hcrpurdue.jason.hcrhousepoints.Utils.CacheManager;

public class Link implements Serializable {
    private boolean isArchived;
    private int claimedCount;
    private String creatorId;
    private String description;
    private String dynamicLink;
    private boolean isEnabled;
    private int pointTypeId;
    private String linkId;
    private boolean singleUse;




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
        this.claimedCount = ((Long) document.get("ClaimedCount")).intValue();
        this.dynamicLink = (String) document.get("DynamicLink");
    }

    /**
     * Constructor for links when being created
     * @param userId
     * @param description
     * @param singleUse
     * @param pointTypeId
     */
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

    public void setHttpUpdates(Map<String, Object> data){
        if(data.containsKey("is_enabled")){
            setEnabled((boolean) data.get("is_enabled"));
        }
        if(data.containsKey("is_archived")){
            setArchived((boolean) data.get("is_archived"));
        }
        if(data.containsKey("description")){
            setDescription((String) data.get("description"));
        }
        if(data.containsKey("point_id")){
            setPointTypeId((int) data.get("point_id"));
        }
        if(data.containsKey("single_use")){
            setSingleUse((boolean) data.get("single_use"));
        }
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

    public void setPointTypeId(int id){
        this.pointTypeId = id;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDynamicLink(){ return this.dynamicLink; }

    public PointType getPointType(Context context){
        return CacheManager.getInstance(context).getPointTypeWithID(pointTypeId);
    }

}
