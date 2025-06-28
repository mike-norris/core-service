package com.openrangelabs.services.microsoft.graph.events.model;

import com.openrangelabs.services.microsoft.graph.events.entity.SingleEvent;
import lombok.Data;

import java.util.List;

@Data
public class EventsResponse {
    List<SingleEvent> events;
    String error;

    public EventsResponse(List<SingleEvent> events, String error) {
        this.events =events;
        this.error = error;
    }


}
