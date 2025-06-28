package com.openrangelabs.services.microsoft.graph.events.model;

import lombok.Data;

@Data
public class EventsCreateResponse {
    Boolean eventCreated;
    String eventId;
    String calendarGroup;
    String calendarID;
    String error;

    public EventsCreateResponse(boolean eventCreated, String error) {
        this.eventCreated = eventCreated;
        this.error = error;
    }

    public EventsCreateResponse(boolean eventCreated, String eventId, String calendarGroup, String calendarId, String error) {
        this.eventCreated = eventCreated;
        this.eventId = eventId;
        this.calendarGroup = calendarGroup;
        this.calendarID = calendarId;
        this.error = error;
    }
}
