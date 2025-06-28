package com.openrangelabs.services.organization.model;

import lombok.Data;

@Data
public class UpdateOrgAdminRequest {
    String middlewareAdminKey;
    String jiraId;
    Organization organization;
}
