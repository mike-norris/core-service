package com.openrangelabs.services.notifications;


import com.openrangelabs.services.authenticate.model.SessionInfo;
import com.openrangelabs.services.log.LogResponseBodyService;
import com.openrangelabs.services.notifications.model.NotificationsResponse;
import com.openrangelabs.services.organization.model.OrgUserSessionsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.management.ManagementFactory;

import static com.openrangelabs.services.authenticate.bonita.BonitaAuthenticateAPIService.BONITA_API_TOKEN_NAME;
import static com.openrangelabs.services.authenticate.bonita.BonitaAuthenticateAPIService.SESSION_ID_NAME;

@RestController
@RequestMapping("/notifications")

public class NotificationsController {

    NotificationsService notificationsService;
    LogResponseBodyService logService;

    @Autowired
    public void notificationsController(NotificationsService notificationsService , LogResponseBodyService logService) {
        this.notificationsService = notificationsService;
        this.logService = logService;
    }

    @PostMapping(value = "/portalAccess")
    public NotificationsResponse getOrgUsersSessions(@RequestBody OrgUserSessionsRequest orgUserSessionRequest,
                                                       HttpServletRequest request) {
        return (NotificationsResponse) logService.logResponse(
                notificationsService.getPortalSessions(orgUserSessionRequest),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());

    }

    @GetMapping(value = "/{organizationId}/{userId}/badgeAccess")
    public NotificationsResponse getDatacenterOrganizationUserAccessLogs(@PathVariable Long organizationId,@PathVariable int userId,
                                                                            @CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                                                            @CookieValue(SESSION_ID_NAME) String sessionId,
                                                                            HttpServletRequest request) {
        return (NotificationsResponse) logService.logResponse(
                notificationsService.getDatacenterUserAccess(organizationId, userId , new SessionInfo(sessionToken, sessionId)),
                request.getMethod(),
                request.getPathInfo(),
                request.getHeader("pid"));
    }

    @GetMapping(value = "/{userId}/useraccess")
    public NotificationsResponse getUserAccessLogs(@PathVariable int userId,
                                                                            @CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                                                            @CookieValue(SESSION_ID_NAME) String sessionId,
                                                                            HttpServletRequest request) {
        return (NotificationsResponse) logService.logResponse(
                notificationsService.getUserAccess( userId),
                request.getMethod(),
                request.getPathInfo(),
                request.getHeader("pid"));
    }

}
