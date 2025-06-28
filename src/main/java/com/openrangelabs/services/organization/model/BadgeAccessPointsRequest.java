package com.openrangelabs.services.organization.model;

import lombok.Data;

@Data
public class BadgeAccessPointsRequest {
    int organizationId;
    String timezone;
}
