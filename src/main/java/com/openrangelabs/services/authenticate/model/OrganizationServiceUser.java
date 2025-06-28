package com.openrangelabs.services.authenticate.model;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class OrganizationServiceUser {
    Long id;
    Long organizationServiceId;
    Long userId;
    Long roleId;
    Boolean active;
    OffsetDateTime createdAt;
}
