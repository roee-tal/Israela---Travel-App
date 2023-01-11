package com.example.app.model.objects;

public class EventID {
    private String siteDB_ID;
    private int eventID;

    public EventID(){}

    public EventID(String siteID, int eventID) {
        this.siteDB_ID = siteID;
        this.eventID = eventID;
    }

    public String getSiteDB_ID() {
        return siteDB_ID;
    }

    public void setSiteDB_ID(String siteDB_ID) {
        this.siteDB_ID = siteDB_ID;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }
}
