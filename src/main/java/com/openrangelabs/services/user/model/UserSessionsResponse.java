package com.openrangelabs.services.user.model;

import com.openrangelabs.services.organization.entity.PortalUserSession;
import lombok.Data;

import java.util.List;
@Data
public class UserSessionsResponse {
    List<PortalUserSession> portalUserSessionList;
    String error;

    public UserSessionsResponse(String error) {
        this.error = error;
    }

    public UserSessionsResponse(List<PortalUserSession> portalUserSessionList, String error) {
        this.portalUserSessionList = portalUserSessionList;
        this.error = error;

    }
}
