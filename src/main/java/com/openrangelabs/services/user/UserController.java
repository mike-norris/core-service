package com.openrangelabs.services.user;

import com.openrangelabs.services.authenticate.bonita.model.BonitaCaseResponse;
import com.openrangelabs.services.authenticate.model.ForgotPasswordRequest;
import com.openrangelabs.services.authenticate.model.NewUserPasswordChangeRequest;
import com.openrangelabs.services.authenticate.bonita.BonitaAuthenticateAPIService;
import com.openrangelabs.services.authenticate.model.SessionInfo;
import com.openrangelabs.services.authenticate.AuthenticateService;
import com.openrangelabs.services.authenticate.tools.Security;
import com.openrangelabs.services.bonita.ParentBonitaAPIService;
import com.openrangelabs.services.bonita.model.BdmActiveMessageResponse;
import com.openrangelabs.services.datacenter.model.bloxops.dao.DatacenterAccessResponse;
import com.openrangelabs.services.log.LogResponseBodyService;
import com.openrangelabs.services.user.bonita.BonitaUserAPIService;
import com.openrangelabs.services.user.model.*;
import com.openrangelabs.services.user.model.*;
import com.openrangelabs.services.user.profile.model.ProfileImageUpdateResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

import java.lang.management.ManagementFactory;
import java.util.List;

import static com.openrangelabs.services.authenticate.bonita.BonitaAuthenticateAPIService.BONITA_API_TOKEN_NAME;
import static com.openrangelabs.services.authenticate.bonita.BonitaAuthenticateAPIService.SESSION_ID_NAME;

@RestController
@RequestMapping("/user")

public class UserController {

    @Value("${bonitaAdminUser}")
    String bonitaAdminUser;

    @Value("${bonitaAdminPassword}")
    String bonitaAdminPassword;

    @Value("${middlewareAdminKey}")
    String middlewareAdminKey;

    UserService userService;
    BonitaUserAPIService bonitaUserAPIService;
    AuthenticateService authenticateService;
    LogResponseBodyService logService;
    BonitaAuthenticateAPIService bonitaAuthenticateAPIService;
    ParentBonitaAPIService parentBonitaAPIService;
    Security security;
     RabbitTemplate rabbitTemplate;

    @Autowired()
    public void userController(UserService userService, BonitaUserAPIService bonitaUserAPIService,
                               AuthenticateService authenticateService, LogResponseBodyService logService,
                               ParentBonitaAPIService parentBonitaAPIService, Security security, RabbitTemplate rabbitTemplate,
                               BonitaAuthenticateAPIService bonitaAuthenticateAPIService) {
        this.userService = userService;
        this.bonitaUserAPIService = bonitaUserAPIService;
        this.authenticateService = authenticateService;
        this.logService = logService;
        this.bonitaAuthenticateAPIService = bonitaAuthenticateAPIService;
        this.parentBonitaAPIService = parentBonitaAPIService;
        this.security = security;
        this.rabbitTemplate = rabbitTemplate;
    }
    
    @GetMapping(value = "/{orgId}/{userId}")
    public UserDetailsResponse getUserById(@PathVariable("userId") int userId, @PathVariable("orgId") int orgId,
                                           @CookieValue(BONITA_API_TOKEN_NAME )  String sessionToken ,
                                           @CookieValue(SESSION_ID_NAME) String sessionId,
                                           HttpServletRequest request) {
        return (UserDetailsResponse) logService.logResponse(
                userService.getUserDetailsById(userId, orgId, new SessionInfo(sessionToken, sessionId)),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }

    @GetMapping(value = "admin/{orgId}/{userId}")
    public UserDetailsResponse getUserById( @PathVariable("userId") int userId,@PathVariable("orgId") int orgId,
                                            HttpServletRequest request) {
        SessionInfo sessionInfo = null;
        String query = request.getQueryString();
        String middlewareAdminKeyMD5 = security.getHash(middlewareAdminKey);

        if (query.contains(middlewareAdminKeyMD5)) {
            sessionInfo = authenticateService.adminAuth();
        }

        return (UserDetailsResponse) logService.logResponse(
                userService.getUserDetailsById(userId, orgId, sessionInfo),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }

    @PutMapping(value = "/")
    public UserResponse updateUser(
                                   @RequestBody UserCreateRequest userCreateRequest,
                                   @CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                   @CookieValue(SESSION_ID_NAME) String sessionId,
                                   HttpServletRequest request) {
        UserIdentificationResponse user = (UserIdentificationResponse) request.getAttribute("user");
        return (UserResponse) logService.logResponse(
                userService.updateUser(user, userCreateRequest, new SessionInfo(sessionToken, sessionId)),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }


    @PutMapping(value = "/mfa")
    public UpdateMFAResponse updateMFA(
            @RequestBody UpdateMFARequest updateMFARequest,
            @CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
            @CookieValue(SESSION_ID_NAME) String sessionId,
            HttpServletRequest request) {
        UserIdentificationResponse user = (UserIdentificationResponse) request.getAttribute("user");

        return (UpdateMFAResponse) logService.logResponse(
                userService.updateUserMFA(user, updateMFARequest, new SessionInfo(sessionToken, sessionId)),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }

//    @PostMapping(value = "/")
//    public UserCreateResponse createUser(@RequestBody UserCreateRequest userCreateRequest,
//                                         @CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
//                                         @CookieValue(SESSION_ID_NAME) String sessionId,
//                                         HttpServletRequest request) {
//        UserIdentificationResponse user = (UserIdentificationResponse) request.getAttribute("user");
//        UserCreate userCreate = userCreateRequest.getUserDetails();
//
//        return (UserCreateResponse) logService.logResponse(
//                userService.createUserNew(userCreate),
//                request.getMethod(),
//                request.getPathInfo(),
//                ManagementFactory.getRuntimeMXBean().getName());
//    }

    @GetMapping(value = "/alerts")
    public List<BdmActiveMessageResponse> getActiveAlerts(@CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                         @CookieValue(SESSION_ID_NAME) String sessionId,
                                         HttpServletRequest request) {
        return (List<BdmActiveMessageResponse>) logService.logResponse(
                parentBonitaAPIService.getActiveMessages(new SessionInfo(sessionToken, sessionId)),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }

//    @PostMapping(value = "/admin/create")
//    public UserCreateResponse createUserByAdmin(@RequestBody UserCreateRequestAdmin userCreateRequest,
//                                                HttpServletRequest request) {
//        SessionInfo sessionInfo = authenticateService.adminAuth();
//        return (UserCreateResponse) logService.logResponse(
//                userService.createUserNew(userCreateRequest),
//                request.getMethod(),
//                request.getPathInfo(),
//                ManagementFactory.getRuntimeMXBean().getName());
//    }


    @GetMapping(value = "/admin/{organizationId}/{userId}/check")
    public UserValidatorResponse userOrganizationSetupCheck(@PathVariable int userId,
                                                   @PathVariable Long organizationId,
                                                   HttpServletRequest request) {
        SessionInfo sessionInfo = authenticateService.adminAuth();
        return (UserValidatorResponse) logService.logResponse(
                userService.userOrganizationSetupCheck(organizationId, userId, sessionInfo),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }


    @PostMapping(value = "/password")
    public PasswordChangeResponse updatePassword(@RequestBody PasswordChangeRequest passwordChangeRequest,
                                                 @CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                                 @CookieValue(SESSION_ID_NAME) String sessionId,
                                                 HttpServletRequest request) {
        UserIdentificationResponse user = (UserIdentificationResponse) request.getAttribute("user");
        return (PasswordChangeResponse) logService.logResponse(
                userService.updatePassword(user, passwordChangeRequest, new SessionInfo(sessionToken, sessionId)),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }

    @PostMapping(value = "/passwordurl")
    public PasswordUrlResponse getChangePasswordURL(@RequestBody PasswordUrlRequest passwordUrlRequest,
                                                    HttpServletRequest request) {
        return (PasswordUrlResponse) logService.logResponse(
                authenticateService.generatePasswordChangeURL(passwordUrlRequest),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }

    @PostMapping(value = "/newuserreset")
    public PasswordChangeResponse changePasswordNewUser(
                                                               @RequestBody NewUserPasswordChangeRequest changeRequest,
                                                               HttpServletRequest request) {
        return (PasswordChangeResponse) logService.logResponse(
                userService.updateNewUserPassword(changeRequest),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }

    @PostMapping(value = "/forgotpassword")
    public BonitaCaseResponse forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest,
                                             HttpServletRequest request) {
        return (BonitaCaseResponse) logService.logResponse(
                authenticateService.requestForgotPasswordEmail(forgotPasswordRequest),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());

    }

    @PutMapping(value = "/profileimage")
    public ProfileImageUpdateResponse updateProfileImage(@RequestBody Integer imageId,
                                                         @CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                                         @CookieValue(SESSION_ID_NAME) String sessionId,
                                                         HttpServletRequest request) {
        UserIdentificationResponse user = (UserIdentificationResponse) request.getAttribute("user");
        return (ProfileImageUpdateResponse) logService.logResponse(
                userService.updateUserProfileImage(user, imageId),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }

    
    @PutMapping(value = "/processed")
    public PendingUserProceessedResponse setPendingUserToProcessed(@RequestBody PendingUserProcessedRequest pendingUserProcessedRequest,
                                                             @CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                                             @CookieValue(SESSION_ID_NAME) String sessionId,
                                                             HttpServletRequest request) {
        return (PendingUserProceessedResponse) logService.logResponse(
                userService.setPendingUserIsProcessed( pendingUserProcessedRequest),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }

    
    @PutMapping(value = "/activation")
    public UserActivationToggleResponse toggleUserActivation(@RequestBody UserActivationToggleRequest userActivationRequest,
                                   @CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                   @CookieValue(SESSION_ID_NAME) String sessionId,
                                   HttpServletRequest request) {
        return (UserActivationToggleResponse) logService.logResponse(
                userService.toggleUserActivation(userActivationRequest),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }
    
    @PostMapping(value = "/customimage")
    public UserCustomImageResponse saveUserProfileImage(@RequestParam("file") MultipartFile file,
                                                             @CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                                             @CookieValue(SESSION_ID_NAME) String sessionId,
                                                             HttpServletRequest request) {
        UserIdentificationResponse user = (UserIdentificationResponse) request.getAttribute("user");
        return (UserCustomImageResponse) logService.logResponse(
                userService.saveUserProfileImage(user, file),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }
    
    @GetMapping(value = "/customimage/delete")
    public UpdateUserImageResponse deleteUserProfileImage(@CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                                          @CookieValue(SESSION_ID_NAME) String sessionId,
                                                          HttpServletRequest request) {
        UserIdentificationResponse user = (UserIdentificationResponse) request.getAttribute("user");
        return (UpdateUserImageResponse) logService.logResponse(
                userService.deleteUserProfileImage(user, new SessionInfo(sessionToken, sessionId)),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }

    @GetMapping(value = "/list")
    public UserListResponse getPortalUserList(HttpServletRequest request) {
        return userService.getPortalUserList();
    }

    @GetMapping(value = "/isowner/{orgID}/{userID}")
    public Boolean checkIsUserOwner(@PathVariable String userID , @PathVariable String orgID ,HttpServletRequest request) {
        return userService.checkIfUserIsOwner(Integer.valueOf(userID) , Long.valueOf(orgID), authenticateService.adminAuth());
    }

    @DeleteMapping(value = "/purge/{orgID}/{userID}")
    public UserActivationToggleResponse purgeUser(@PathVariable String userID , @PathVariable String orgID ,HttpServletRequest request) {
        return userService.purgeUser(userID, orgID);
    }

    @DeleteMapping(value = "/admin/purge/{orgID}/{userID}/{ticketID}")
    public UserActivationToggleResponse purgeUserAdmin(@PathVariable String userID , @PathVariable String orgID, @PathVariable String ticketID  ,HttpServletRequest request) {
        return userService.purgeUserAdmin(userID, orgID , ticketID);
    }

    @PostMapping(value = "/admin/add/{orgID}/{userID}/{ticketID}")
    public UserActivationToggleResponse addUserToOrg(@PathVariable String userID , @PathVariable String orgID, @PathVariable String ticketID  ,HttpServletRequest request) {
        return userService.addExistingUserToOrganization(userID, orgID , ticketID);
    }

    @PostMapping(value = "/admin/email")
    public EmailResponseAdmin sendEmailAdmin(@RequestBody EmailRequestAdmin emailRequest , HttpServletRequest request) {
        return userService.sendEmailAdmin(emailRequest);
    }

    @PutMapping(value = "/admin/activation")
    public UserActivationToggleResponse adminToggleUserActivation(@RequestBody UserActivationToggleRequest userActivationRequest,
                                                             HttpServletRequest request) {
        return (UserActivationToggleResponse) logService.logResponse(
                userService.toggleUserActivation(userActivationRequest),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }

    @GetMapping(value = "/datacenter/{cardNumber}")
    public DatacenterAccessResponse getDatacenterUserAccessLogs(@PathVariable int cardNumber,
                                                                @CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                                                @CookieValue(SESSION_ID_NAME) String sessionId,
                                                                HttpServletRequest request) {
        return (DatacenterAccessResponse) logService.logResponse(
                userService.getDatacenterUserAccessLogs(cardNumber),
                request.getMethod(),
                request.getPathInfo(),
                request.getHeader("pid"));
    }

    @PostMapping(value = "/role")
    public UserRoleChangeResponse changeUserRole(@RequestBody UserRoleChangeRequest userRoleChangeRequest,
                                                        @CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                                        @CookieValue(SESSION_ID_NAME) String sessionId,
                                                        HttpServletRequest request) {
        return (UserRoleChangeResponse) logService.logResponse(
                userService.changeUserRole(userRoleChangeRequest),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }

    @PostMapping(value = "/admin/role")
    public UserRoleChangeResponse changeUserRoleAdmin(@RequestBody UserRoleChangeRequest userRoleChangeRequest,
                                                 HttpServletRequest request) {
        return (UserRoleChangeResponse) logService.logResponse(
                userService.changeUserRole(userRoleChangeRequest),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }

}
