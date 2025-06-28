package com.openrangelabs.services.organization.model;

import com.openrangelabs.services.organization.entity.PortalUserSession;
import lombok.Data;

import java.util.List;

@Data
public class OrgUserSessionsResponse {
    List<PortalUserSession> orgUserSessionList;
    String error;

    public OrgUserSessionsResponse(String error) {
        this.error = error;
    }

    public OrgUserSessionsResponse(List<PortalUserSession> orgUserSessionList, String error) {
        this.orgUserSessionList = orgUserSessionList;
        this.error = error;

    }

}
