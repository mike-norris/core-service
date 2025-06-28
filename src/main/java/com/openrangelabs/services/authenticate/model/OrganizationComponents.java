package com.openrangelabs.services.authenticate.model;

import lombok.Data;

@Data
public class OrganizationComponents {
    Long id;
    Long serviceId;
    String name;
    String defaultAccess;
}
