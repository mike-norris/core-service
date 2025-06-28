package com.openrangelabs.services.microsoft.graph.events.model;

import com.openrangelabs.services.microsoft.graph.events.entity.SingleEvent;
import lombok.Data;

@Data
public class EventResponse {
    SingleEvent event;
    String error;

    public EventResponse(SingleEvent event, String error) {
        this.event = event;
        this.error = error;
    }
}
