package com.openrangelabs.services.roster.entity;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class RosterUserPhoto {

    private long rosteruserId;
    private long badgeId;
    private String photoLocation;
    private String photoName;
    private int status;
    private int createdBy;
    private OffsetDateTime createdDt;

}
