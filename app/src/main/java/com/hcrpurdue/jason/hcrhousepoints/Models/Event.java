package com.hcrpurdue.jason.hcrhousepoints.Models;

import java.io.Serializable;

public class Event implements Serializable {



    private  int claimedCount;
    private String name;
    private String details;
    private String startDate;
    private String endDate;
  //  private  Date startDate;
   // private  Date endDate;
    private String location;
    private int point;
    private int pointTypeId;
    private String pointTypeName;
    private String pointTypeDescription;
    private String[] floorIds;
    private String id;
    //automatically set to false for now
    private boolean isPublicEvent;
    private boolean isAllFloors;
    private String creatorId;
    private String host;
    private String[] floorColors;

    public Event(String name, String details, String startDate, String endDate, String location,
                 int point, int pointTypeId, String pointTypeName, String pointTypeDescription,
                 String[] floorIds, String id, String creatorId, String host, String[] floorColors,boolean isPublicEvent, int claimedCount) {
        this.name = name;
        this.details = details;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.point = point;
        this.pointTypeId = pointTypeId;
        this.pointTypeName = pointTypeName;
        this.pointTypeDescription = pointTypeDescription;
        this.floorIds = floorIds;
        this.id = id;
        this.creatorId = creatorId;
        this.host = host;
        this.floorColors = floorColors;
        this.isPublicEvent = isPublicEvent;
        this.claimedCount = claimedCount;
    }
//constructor used for creating an Event
    public Event(String name, String details, String startDate, String endDate, String location, int pointTypeId,String[] floorIds, boolean isPublicEvent, boolean isAllFloors,String host) {
        this.name = name;
        this.details = details;
        this.startDate= startDate;
        this.endDate = endDate;
        this.location = location;
        this.pointTypeId = pointTypeId;
        this.host = host;
        this.isPublicEvent = isPublicEvent;
        this.isAllFloors = isAllFloors;
        this.floorIds = floorIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }



    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getPointTypeId() {
        return pointTypeId;
    }

    public void setPointTypeId(int pointTypeId) {
        this.pointTypeId = pointTypeId;
    }

    public String getPointTypeName() {
        return pointTypeName;
    }

    public void setPointTypeName(String pointTypeName) {
        this.pointTypeName = pointTypeName;
    }

    public String getPointTypeDescription() {
        return pointTypeDescription;
    }

    public void setPointTypeDescription(String pointTypeDescription) {
        this.pointTypeDescription = pointTypeDescription;
    }

    public String[] getFloorIds() {
        return floorIds;
    }

    public void setFloorIds(String[] floorIds) {
        this.floorIds = floorIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String[] getFloorColors() {
        return floorColors;
    }

    public void setFloorColors(String[] floorColors) {
        this.floorColors = floorColors;
    }

    public boolean isPublicEvent() {
        return isPublicEvent;
    }

    public void setPublicEvent(boolean publicEvent) {
        isPublicEvent = publicEvent;
    }

    public boolean isAllFloors() {
        return isAllFloors;
    }

    public void setAllFloors(boolean allFloors) {
        isAllFloors = allFloors;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    public int getClaimedCount() {
        return claimedCount;
    }

    public void setClaimedCount(int claimedCount) {
        this.claimedCount = claimedCount;
    }
}
