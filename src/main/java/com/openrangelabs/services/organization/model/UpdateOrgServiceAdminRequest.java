package com.openrangelabs.services.organization.model;

import com.openrangelabs.services.authenticate.permission.model.OrganizationService;
import lombok.Data;

@Data
public class UpdateOrgServiceAdminRequest {
    String middlewareAdminKey;
    OrganizationService organizationService;
}
