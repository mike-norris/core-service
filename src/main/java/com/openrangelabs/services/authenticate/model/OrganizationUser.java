package com.openrangelabs.services.authenticate.model;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class OrganizationUser {
    Long organizationId;
    Long userId;
    Long roleId;
    Boolean active;
    OffsetDateTime createdAt;
}
