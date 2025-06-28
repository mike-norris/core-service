package com.openrangelabs.services.roster.entity;

import lombok.Data;

@Data
public class UserAccess {
    private long id;
    private String eventStatus;
    private String eventType;
    private String accessType;
    private String createdDT;
    private long datacenterId;
    private long rosterUserId;
    private int userId;
    String datacenterCity;
}
