package com.openrangelabs.services.microsoft.graph.events.entity;

import lombok.Data;

@Data
public class Recurrence {
    String type;
    int interval;
    int day;
    String startDate;
    String endDate;
}
