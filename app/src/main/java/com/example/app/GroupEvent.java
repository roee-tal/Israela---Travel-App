package com.example.app;

import java.text.SimpleDateFormat;

public class GroupEvent {
    private String dateEvent;
    private int peopleEvent;
    private String meetingDetail;

    public GroupEvent() {}

    public GroupEvent(String dateEvent, int peopleEvant, String meetingDetail) {
        this.dateEvent = dateEvent;
        this.peopleEvent = peopleEvant;
        this.meetingDetail = meetingDetail;
    }

    public String getDateEvent() {
        return dateEvent;
    }

    public void setDateEvent(String dateEvent) {
        this.dateEvent = dateEvent;
    }

    public int getPeopleEvent() {
        return peopleEvent;
    }

    public void setPeopleEvent(int peopleEvent) {
        this.peopleEvent = peopleEvent;
    }

    public String getMeetingDetail() {
        return meetingDetail;
    }

    public void setMeetingDetail(String meetingDetail) {
        this.meetingDetail = meetingDetail;
    }

}
