package com.openrangelabs.services.organization.model;

import com.openrangelabs.services.authenticate.permission.model.OrganizationService;
import lombok.Data;

import java.util.List;
@Data
public class OrgServicesResponse {
    List<OrganizationService> organizationServices;
    String error;

    public OrgServicesResponse(List<OrganizationService> organizationServices, String error) {
        this.organizationServices = organizationServices;
        this.error = error;
    }
}
