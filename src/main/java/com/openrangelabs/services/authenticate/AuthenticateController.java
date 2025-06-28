package com.openrangelabs.services.authenticate;

import com.openrangelabs.services.authenticate.bonita.BonitaAuthenticateAPIService;
import com.openrangelabs.services.authenticate.model.*;
import com.openrangelabs.services.authenticate.model.*;
import com.openrangelabs.services.authenticate.permission.PermissionService;
import com.openrangelabs.services.authenticate.permission.model.*;
import com.openrangelabs.services.authenticate.permission.model.*;
import com.openrangelabs.services.authenticate.permission.model.Organization;
import com.openrangelabs.services.log.LogResponseBodyService;
import com.openrangelabs.services.user.model.*;


import com.openrangelabs.services.user.model.UserIdentificationResponse;
import com.openrangelabs.services.user.model.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.lang.management.ManagementFactory;
import java.util.List;

import static com.openrangelabs.services.authenticate.bonita.BonitaAuthenticateAPIService.BONITA_API_TOKEN_NAME;
import static com.openrangelabs.services.authenticate.bonita.BonitaAuthenticateAPIService.SESSION_ID_NAME;

@Slf4j
@RestController
@RequestMapping("/authenticate")

public class AuthenticateController {

    @Value("${bonitaEnvironment}")
    String environment;

    BonitaAuthenticateAPIService bonitaUserAPI;
    AuthenticateService authenticateService;
    PermissionService permissionService;
    LogResponseBodyService logService;

    @Autowired()
    public void authenticateController(BonitaAuthenticateAPIService bonitaUserAPI,
                               AuthenticateService authenticateService, PermissionService permissionService,
                                       LogResponseBodyService logService) {
        this.bonitaUserAPI = bonitaUserAPI;
        this.authenticateService = authenticateService;
        this.permissionService = permissionService;
        this.logService = logService;
    }

    @PostMapping(path="/session")
    public UserIdentificationResponse getUserId(@CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                                @CookieValue(SESSION_ID_NAME) String sessionId,
                                                HttpServletRequest request) {
        return (UserIdentificationResponse) logService.logResponse(
                request.getAttribute("user"),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }

    @GetMapping( path ="/session")
    public GetSessionResponse getSession(@CookieValue(BONITA_API_TOKEN_NAME ) String sessionToken,
                                         @CookieValue(SESSION_ID_NAME) String sessionId,
                                         HttpServletRequest request) {
        return (GetSessionResponse) logService.logResponse(
                authenticateService.getSession(sessionToken , sessionId),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }

    
    @PostMapping(value = "/login")
    public ResponseEntity loginUser(@RequestBody LoginRequest loginRequest,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {
        //TODO remove cookie - temporary for testing
        UserResponse userResponse = authenticateService.authenticateUserCredentials(loginRequest);
        log.info("Authenticate Controller " + userResponse.isMfa());
        if(!userResponse.isMfa()) {
            try {
                Cookie sessionIdCookie = new Cookie(BONITA_API_TOKEN_NAME, userResponse.getSessionInfo().getSessionToken());
                Cookie sessionTokenCookie = new Cookie(SESSION_ID_NAME, userResponse.getSessionInfo().getSessionId());
                sessionTokenCookie.setSecure(true);
                sessionIdCookie.setSecure(true);
                sessionIdCookie.setHttpOnly(true);
                sessionTokenCookie.setHttpOnly(true);
                sessionTokenCookie.setPath("/");
                sessionIdCookie.setPath("/");
                response.addCookie(sessionIdCookie);
                response.addCookie(sessionTokenCookie);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return (ResponseEntity) logService.logResponse(
                ResponseEntity.ok(userResponse),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }

    
    @PostMapping(path = "/login/test")
    public ResponseEntity loginUserTest(@RequestBody LoginRequest loginRequest,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {

        /**
         * Endpoint for cypress testing app. Ensure only accessible on testing and stg.
         */
        UserResponse userResponse = null;
        if (environment.contains("prod")) {
            userResponse = new UserResponse();
            userResponse.setError("Unauthorized action requested.");
        }else{
             userResponse = authenticateService.authenticateUserCredentials(loginRequest);
        }
            try{
                Cookie sessionIdCookie = new Cookie(BONITA_API_TOKEN_NAME, userResponse.getSessionInfo().getSessionToken());
                Cookie sessionTokenCookie = new Cookie(SESSION_ID_NAME, userResponse.getSessionInfo().getSessionId());
                sessionTokenCookie.setSecure(true);
                sessionIdCookie.setSecure(true);
                sessionIdCookie.setHttpOnly(true);
                sessionTokenCookie.setHttpOnly(true);
                sessionTokenCookie.setPath("/");
                sessionIdCookie.setPath("/");
                response.addCookie(sessionIdCookie);
                response.addCookie(sessionTokenCookie);
            } catch (Exception e) {

            }

        return (ResponseEntity) logService.logResponse(
                ResponseEntity.ok(userResponse),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }

    
    @PostMapping(path = "/requestSecondFactor")
    public ResponseEntity requestSecondFactor(@RequestBody SecondFactorSendAuthCodeRequest requestBody,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) {

        //TODO remove cookie - temporary for testing
        UserResponse userResponse = authenticateService.sendSecondFactorOneTimeCode(requestBody);
        return (ResponseEntity) logService.logResponse(
                ResponseEntity.ok(userResponse),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }

    
    @PostMapping(path = "/secondfactor")
    public ResponseEntity authenicateUserSecondFactor(@RequestBody SecondFactorAuthRequest secondFactorAuthRequest,
                                                         HttpServletRequest request,
                                                         HttpServletResponse response) {
        UserResponse userResponse = authenticateService.authenticateUserSecondFactor(secondFactorAuthRequest);
        try {
            Cookie sessionIdCookie = new Cookie(BONITA_API_TOKEN_NAME, userResponse.getSessionInfo().getSessionToken());
            Cookie sessionTokenCookie = new Cookie(SESSION_ID_NAME, userResponse.getSessionInfo().getSessionId());
            sessionTokenCookie.setSecure(true);
            sessionIdCookie.setSecure(true);
            sessionIdCookie.setHttpOnly(true);
            sessionTokenCookie.setHttpOnly(true);
            log.warn("x-bonita-token cookie returned: "+sessionIdCookie.getValue() +" username:"+userResponse.getUser().getUserName());
            sessionTokenCookie.setPath("/");
            sessionIdCookie.setPath("/");
            response.addCookie(sessionIdCookie);
            response.addCookie(sessionTokenCookie);
        } catch (Exception e){
            log.error(e.getMessage());
        }

        return (ResponseEntity) logService.logResponse(
                ResponseEntity.ok(userResponse),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }

    
    @GetMapping(path = "/logout")
    public void logout(@CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                       @CookieValue(SESSION_ID_NAME) String sessionId,
                       HttpServletRequest request) {
        bonitaUserAPI.logout(new SessionInfo(sessionToken, sessionId));
        logService.logResponse(
                "LOGOUT",
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }

    
    @GetMapping(path = "/user")
    public ResponseEntity getAuthenticatedUser(@CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                                  @CookieValue(SESSION_ID_NAME) String sessionId,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response) {
        UserIdentificationResponse user = (UserIdentificationResponse) request.getAttribute("user");
        UserResponse userResponse = authenticateService.getAuthenticatedUser(user, new SessionInfo(sessionToken, sessionId));
        try {
            Cookie sessionIdCookie = new Cookie(BONITA_API_TOKEN_NAME, userResponse.getSessionInfo().getSessionToken());
            Cookie sessionTokenCookie = new Cookie(SESSION_ID_NAME, userResponse.getSessionInfo().getSessionId());
            sessionTokenCookie.setSecure(true);
            sessionIdCookie.setSecure(true);
            sessionIdCookie.setHttpOnly(true);
            sessionTokenCookie.setHttpOnly(true);
            sessionTokenCookie.setPath("/");
            sessionIdCookie.setPath("/");
            response.addCookie(sessionIdCookie);
            response.addCookie(sessionTokenCookie);
        } catch (Exception e){
            log.error(e.getMessage());
        }

        return (ResponseEntity) logService.logResponse(
                ResponseEntity.ok(userResponse),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }
    
    @GetMapping(path = "/memberships/{orgId}/{userId}")
    public List<Organization> updatePermissions(@PathVariable("userId") Long userId,
                                                @PathVariable("orgId") Long orgId,
                                                @CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                                @CookieValue(SESSION_ID_NAME) String sessionId,
                                                HttpServletRequest request) {
        return (List<Organization>) logService.logResponse(
                permissionService.getUsersOrganizations(userId,orgId),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }
    
    @PostMapping(path = "/permission")
    public PermissionsResponse getPermissions(@RequestBody PermissionRequest requestBody,
                                              @CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                              @CookieValue(SESSION_ID_NAME) String sessionId,
                                              HttpServletRequest request) {

        return (PermissionsResponse) logService.logResponse(
                permissionService.getServicePermissons(requestBody.getUserId(), requestBody.getServiceId(), requestBody.getOrganizationId()),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }
    
    @PostMapping(path = "/permission/update")
    public UpdatePermissionsResponse updatePermissions(@RequestBody UpdatePermissionsRequest requestBody,
                                                       @CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                                       @CookieValue(SESSION_ID_NAME) String sessionId,
                                                       HttpServletRequest request) {

        return (UpdatePermissionsResponse) logService.logResponse(
                permissionService.updateUsersPermissions(requestBody),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }
    
    @PostMapping(path = "/permission/user/{userId}")
    public UpdatePermissionsResponse updateModulePermissions(@RequestBody UpdateModulePermissionsRequest requestBody,
                                                             @PathVariable("userId") Long userId,
                                                             @CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                                             @CookieValue(SESSION_ID_NAME) String sessionId,
                                                             HttpServletRequest request) {
        return (UpdatePermissionsResponse) logService.logResponse(
                permissionService.updateUsersModulePermissions(requestBody , userId),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }

    @PostMapping(path = "/admin/permission/user/{userId}")
    public UpdatePermissionsResponse updateModulePermissionsAdmin(@RequestBody UpdateModulePermissionsRequest requestBody,
                                                             @PathVariable("userId") Long userId,
                                                             HttpServletRequest request) {
        return (UpdatePermissionsResponse) logService.logResponse(
                permissionService.updateUsersModulePermissionsAdmin(requestBody , userId),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }

    
    @PostMapping(path = "/permissions")
    public PermissionsResponse getUserPermissions(@RequestBody PermissionRequest requestBody,
                                              @CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                              @CookieValue(SESSION_ID_NAME) String sessionId,
                                              HttpServletRequest request) {
        return (PermissionsResponse) logService.logResponse(
                permissionService.getServicePermissons(requestBody.getUserId(),requestBody.getServiceId(), requestBody.getOrganizationId()),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }

    @PostMapping(path = "/permission/check")
    public PermissionCheckResponse checkPermission(@RequestBody PermissionCheckRequest requestBody,
                                                   HttpServletRequest request) {
        return (PermissionCheckResponse) logService.logResponse(
                permissionService.checkPermission(requestBody),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }

    @GetMapping(value = "/admin/login/check/{userName}/{ticketID}")
    public PermissionCheckResponse checkLogin(@PathVariable String userName ,@PathVariable String ticketID ,HttpServletRequest request) {
        return permissionService.checkLogin(userName, ticketID);
    }

}
