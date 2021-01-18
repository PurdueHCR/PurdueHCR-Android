package com.hcrpurdue.jason.hcrhousepoints.Models;

import java.io.Serializable;

public class PassEvent implements Serializable {
    String eventName;
    String description;
    String location;
    int pointValue;
    String pointTypeTitle;
    String startDate;
    String endDate;
    String startDateMonth;
    String startDateDay;
    String startDateHour;
    String startDateMinute;
    String endDateMonth;
    String endDateDay;
    String endDateHour;
    String endDateMinute;
    String[] floorIDs;
    String host;
    boolean publicEvent;
    String id;

    public PassEvent(String eventName, String host, String description, String location, String pointTypeTitle, String startDate, String endDate, String startDateMonth, String startDateDay, String startDateHour, String startDateMinute, String endDateMonth, String endDateDay, String endDateHour, String endDateMinute, String[] floorIDs, boolean publicEvent, String id) {
        this.eventName = eventName;
        this.host = host;
        this.description = description;
        this.location = location;
        this.pointTypeTitle = pointTypeTitle;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startDateMonth = startDateMonth;
        this.startDateDay = startDateDay;
        this.startDateHour = startDateHour;
        this.startDateMinute = startDateMinute;
        this.endDateMonth = endDateMonth;
        this.endDateDay = endDateDay;
        this.endDateHour = endDateHour;
        this.endDateMinute = endDateMinute;
        this.floorIDs = floorIDs;
        this.publicEvent = publicEvent;
        this.id = id;
    }
    public PassEvent(String eventName, String host, String description, String location, int pointValue, String startDate, String endDate, String startDateMonth, String startDateDay, String startDateHour, String startDateMinute, String endDateMonth, String endDateDay, String endDateHour, String endDateMinute, String[] floorIDs, boolean publicEvent, String id) {
        this.eventName = eventName;
        this.description = description;
        this.location = location;
      this.host = host;
      this.pointValue = pointValue;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startDateMonth = startDateMonth;
        this.startDateDay = startDateDay;
        this.startDateHour = startDateHour;
        this.startDateMinute = startDateMinute;
        this.endDateMonth = endDateMonth;
        this.endDateDay = endDateDay;
        this.endDateHour = endDateHour;
        this.endDateMinute = endDateMinute;
        this.floorIDs = floorIDs;
        this.publicEvent = publicEvent;
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPointTypeTitle() {
        return pointTypeTitle;
    }

    public void setPointTypeTitle(String pointTypeTitle) {
        this.pointTypeTitle = pointTypeTitle;
    }

    public String getStartDateMonth() {
        return startDateMonth;
    }

    public void setStartDateMonth(String startDateMonth) {
        this.startDateMonth = startDateMonth;
    }

    public String getStartDateDay() {
        return startDateDay;
    }

    public void setStartDateDay(String startDateDay) {
        this.startDateDay = startDateDay;
    }

    public String getStartDateHour() {
        return startDateHour;
    }

    public void setStartDateHour(String startDateHour) {
        this.startDateHour = startDateHour;
    }

    public String getStartDateMinute() {
        return startDateMinute;
    }

    public void setStartDateMinute(String startDateMinute) {
        this.startDateMinute = startDateMinute;
    }

    public String getEndDateMonth() {
        return endDateMonth;
    }

    public void setEndDateMonth(String endDateMonth) {
        this.endDateMonth = endDateMonth;
    }

    public String getEndDateDay() {
        return endDateDay;
    }

    public void setEndDateDay(String endDateDay) {
        this.endDateDay = endDateDay;
    }

    public String getEndDateHour() {
        return endDateHour;
    }

    public void setEndDateHour(String endDateHour) {
        this.endDateHour = endDateHour;
    }

    public String getEndDateMinute() {
        return endDateMinute;
    }

    public void setEndDateMinute(String endDateMinute) {
        this.endDateMinute = endDateMinute;
    }

    public String[] getFloorIDs() {
        return floorIDs;
    }

    public void setFloorIDs(String[] floorIDs) {
        this.floorIDs = floorIDs;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isPublicEvent() {
        return publicEvent;
    }

    public void setPublicEvent(boolean publicEvent) {
        this.publicEvent = publicEvent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
