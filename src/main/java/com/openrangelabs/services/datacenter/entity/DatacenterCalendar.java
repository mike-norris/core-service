package com.openrangelabs.services.datacenter.entity;

import lombok.Data;

@Data
public class DatacenterCalendar {
    int datacenterId;
    String calendarReference;
    String groupReference;
    String tenantId;
    String name;
    String clientId;
    String clientSecret;
}
