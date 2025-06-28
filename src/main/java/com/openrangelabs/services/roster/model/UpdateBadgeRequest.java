package com.openrangelabs.services.roster.model;

import lombok.Data;

@Data
public class UpdateBadgeRequest {
    int rosterUserId;
    String datacenter;
    boolean disabled;
    String guid;
    int objectId;
    String reason;
    long organizationId;
    String userId;
}
