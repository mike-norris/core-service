package com.openrangelabs.services.authenticate.permission.model;

import lombok.Data;

import java.util.List;

@Data
public class Organization {
    long organizationId;
    String organizationName;
    String organizationIcon;
    String orgCode;
    String role;
    Boolean beta;
    List<OrganizationService> services;
}
