package com.openrangelabs.services.organization.model;

import lombok.Data;

@Data
public class OrganizationBuildRequest {
    String ticketId;
    Organization organization;
}
