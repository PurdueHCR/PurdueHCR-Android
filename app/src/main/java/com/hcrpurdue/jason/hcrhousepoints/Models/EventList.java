package com.hcrpurdue.jason.hcrhousepoints.Models;

import java.util.List;

public class EventList {

    private List<Event> events;

    public EventList(List<Event> events) {
        this.events = events;
    }
    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
