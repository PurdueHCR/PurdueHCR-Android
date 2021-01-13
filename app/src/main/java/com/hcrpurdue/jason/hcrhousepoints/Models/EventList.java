package com.hcrpurdue.jason.hcrhousepoints.Models;

import java.util.ArrayList;

public class EventList {

    private ArrayList<Event> events;

    public EventList(ArrayList<Event> events) {
        this.events = events;
    }
    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }
}
