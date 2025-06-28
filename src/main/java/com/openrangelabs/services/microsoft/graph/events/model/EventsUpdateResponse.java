package com.openrangelabs.services.microsoft.graph.events.model;

import lombok.Data;

@Data
public class EventsUpdateResponse {
    Boolean eventUpdated;
    String error;

    public EventsUpdateResponse(boolean eventUpdated, String error) {
        this.eventUpdated = eventUpdated;
        this.error = error;
    }
}
