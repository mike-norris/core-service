package com.openrangelabs.services.authenticate.model;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class OrganizationService {
    long id;
    long organizationId;
    long serviceId;
    boolean active;
    OffsetDateTime createdAt;
}
