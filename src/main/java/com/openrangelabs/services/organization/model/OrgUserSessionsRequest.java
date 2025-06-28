package com.openrangelabs.services.organization.model;

import lombok.Data;

@Data
public class OrgUserSessionsRequest {

    Long organizationId;
    String timezone;
}
