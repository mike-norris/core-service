package com.openrangelabs.services.authenticate.model;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class OrganizationServices {
    Long id;
    Long organizationId;
    Long serviceId;
    Boolean active;
    OffsetDateTime createdAt;

}
