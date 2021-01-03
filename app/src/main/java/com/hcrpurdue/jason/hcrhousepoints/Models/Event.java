package com.hcrpurdue.jason.hcrhousepoints.Models;

import java.io.Serializable;
import java.util.Date;

public class Event implements Serializable {

    private String name;
    private String details;
    private final Date startDate;
    private final Date endDate;
    private String location;
    private int point;
    private int pointTypeId;
    private String pointTypeName;
    private String pointTypeDescription;
    private String[] floorIds;
    private String id;
    private String creatorId;
    private String host;
    private String[] floorColors;

    public Event(String name, String details, Date startDate, Date endDate, String location,
                 int point, int pointTypeId, String pointTypeName, String pointTypeDescription,
                 String[] floorIds, String id, String creatorId, String host, String[] floorColors) {
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
    }

    public Event(String name, String details, Date startDate, Date endDate, String location, int point, String[] floorIds, String host) {
        this.name = name;
        this.details = details;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.point = point;
        this.floorIds = floorIds;
        this.host = host;
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

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
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
}
