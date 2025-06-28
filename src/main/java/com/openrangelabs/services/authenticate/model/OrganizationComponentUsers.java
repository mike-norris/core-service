package com.openrangelabs.services.authenticate.model;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class OrganizationComponentUsers {
    Long id;
    Long componentId;
    Long organizationId;
    Long UserId;
    Boolean active;
    String access;
    OffsetDateTime createdAt;
}
