package com.openrangelabs.services.organization.model;

import java.util.List;

import lombok.Data;

@Data
public class OrganizationAdminResponse {
    List<OrganizationDetails> organizationDetails;
    String error;

    public OrganizationAdminResponse(List<OrganizationDetails> organizationDetails) {
        this.organizationDetails = organizationDetails;
    }

    public OrganizationAdminResponse(String error) {
        this.error = error;
    }

    public OrganizationAdminResponse() {
    }
}
