package com.openrangelabs.services.microsoft.graph.events.model;

import lombok.Data;

@Data
public class EventsRequest {
    String datacenter;
    String startTime;
    String endTime;
}
