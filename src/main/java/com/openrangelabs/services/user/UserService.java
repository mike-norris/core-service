package com.openrangelabs.services.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.openrangelabs.services.authenticate.AuthenticateService;
import com.openrangelabs.services.authenticate.bloxops.dao.PasswordRequestKeyDAO;
import com.openrangelabs.services.authenticate.bonita.BonitaAuthenticateAPIService;
import com.openrangelabs.services.authenticate.model.NewUserPasswordChangeRequest;
import com.openrangelabs.services.authenticate.model.PasswordResetKey;
import com.openrangelabs.services.authenticate.permission.PermissionService;
import com.openrangelabs.services.authenticate.permission.bloxops.dao.PermissionDAO;
import com.openrangelabs.services.authenticate.permission.enitity.Permission;
import com.openrangelabs.services.bonita.BonitaWebAPI;
import com.openrangelabs.services.datacenter.ShipmentService;
import com.openrangelabs.services.datacenter.bloxops.dao.mapper.DatacenterBloxopsDAO;
import com.openrangelabs.services.datacenter.entity.DataCenterUserAccessLog;
import com.openrangelabs.services.datacenter.model.bloxops.dao.DatacenterAccessResponse;
import com.openrangelabs.services.datacenter.model.bloxops.dao.shipments.AddUserRequest;
import com.openrangelabs.services.datacenter.model.bloxops.dao.shipments.AddUserResponse;
import com.openrangelabs.services.log.model.LogRecord;
import com.openrangelabs.services.message.ErrorMessageService;
import com.openrangelabs.services.message.MessagingService;
import com.openrangelabs.services.message.sendGrid.SendGridAPIService;
import com.openrangelabs.services.organization.bloxops.dao.BloxopsOrganizationDAO;
import com.openrangelabs.services.organization.model.OrganizationUser;
import com.openrangelabs.services.signing.dao.SigningBloxopsDAO;
import com.openrangelabs.services.ticket.model.*;
import com.openrangelabs.services.ticket.model.CGComment;
import com.openrangelabs.services.ticket.model.CGTicketUpdateRequest;
import com.openrangelabs.services.ticket.model.TicketUpdateDetails;
import com.openrangelabs.services.ticket.model.TicketUpdateRequest;
import com.openrangelabs.services.tools.Commons;
import com.openrangelabs.services.user.bloxops.dao.PendingUserBloxopsDAO;
import com.openrangelabs.services.user.bonita.BonitaADUserAPIService;
import com.openrangelabs.services.user.bonita.model.*;
import com.openrangelabs.services.user.bonita.model.*;
import com.openrangelabs.services.user.entity.User;
import com.openrangelabs.services.user.entity.UserShort;
import com.openrangelabs.services.user.model.*;
import com.openrangelabs.services.authenticate.model.SessionInfo;
import com.openrangelabs.services.user.bonita.BonitaUserAPIService;
import com.openrangelabs.services.user.model.*;
import com.openrangelabs.services.user.profile.ProfileService;
import com.openrangelabs.services.user.profile.dao.UserBloxopsDAO;
import com.openrangelabs.services.user.profile.model.ProfileImageUpdateResponse;
import com.openrangelabs.services.user.profile.model.UserProfile;
import com.openrangelabs.services.user.repository.PendingUser;
import com.sendgrid.helpers.mail.objects.Personalization;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import org.apache.commons.lang3.StringUtils;
import org.bonitasoft.web.client.model.Group;
import org.bonitasoft.web.client.model.Role;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;

import java.util.List;

@Slf4j
@Service
public class UserService {
    
    @Value("${middlewareAdminKey}")
    String middlewareAdminKey;

    @Value("${bonitaAdminUser}")
    String bonitaAdminUser;

    @Value("${bonitaAdminPassword}")
    String bonitaAdminPassword;

    BonitaUserAPIService bonitaUserService;
    BonitaAuthenticateAPIService bonitaAuthenticateAPIService;
    ProfileService profileService;
    ErrorMessageService errorMessageService;
    AuthenticateService authenticateService;
    BonitaUserAPIService bonitaUserAPIService;
    PendingUserBloxopsDAO pendingUserBloxopsDAO;
    BonitaADUserAPIService bonitaADUserAPIService;
    UserBloxopsDAO userBloxopsDAO;
    PasswordRequestKeyDAO passwordRequestKeyDAO;
    BloxopsOrganizationDAO bloxopsOrganizationDAO;
    PermissionDAO permissionDAO;
    PermissionService permissionService;
    ShipmentService shipmentService;
    DatacenterBloxopsDAO datacenterBloxopsDAO;
    SendGridAPIService sendGridAPIService;
    SigningBloxopsDAO signingBloxopsDAO;
    MessagingService messagingService;
    private final RabbitTemplate rabbitTemplate;
    BonitaWebAPI bonitaWebAPI;

    String FALSE_STRING = "false";
    String SAVE_ERROR = "Unable to save pending user";
    String PASSWORD_ERROR = "Unable to change password.";
    String ERROR = "error";

    @Autowired
    UserService(BonitaUserAPIService bonitaUserService, ErrorMessageService errorMessageService,
                BonitaAuthenticateAPIService bonitaAuthenticateAPIService, ProfileService profileService, BonitaUserAPIService bonitaUserAPIService,
                AuthenticateService authenticateService, PendingUserBloxopsDAO pendingUserBloxopsDAO, BonitaADUserAPIService bonitaADUserAPIService,
                UserBloxopsDAO userBloxopsDAO, PasswordRequestKeyDAO passwordRequestKeyDAO, BloxopsOrganizationDAO bloxopsOrganizationDAO,
                PermissionDAO permissionDAO, ShipmentService shipmentService, PermissionService permissionService , DatacenterBloxopsDAO datacenterBloxopsDAO , SendGridAPIService sendGridAPIService,RabbitTemplate rabbitTemplate , BonitaWebAPI bonitaWebAPI,SigningBloxopsDAO signingBloxopsDAO, MessagingService messagingService) {
        this.bonitaUserService = bonitaUserService;
        this.errorMessageService = errorMessageService;
        this.bonitaAuthenticateAPIService = bonitaAuthenticateAPIService;
        this.profileService = profileService;
        this.bonitaUserAPIService = bonitaUserAPIService;
        this.authenticateService = authenticateService;
        this.pendingUserBloxopsDAO = pendingUserBloxopsDAO;
        this.bonitaADUserAPIService = bonitaADUserAPIService;
        this.userBloxopsDAO = userBloxopsDAO;
        this.passwordRequestKeyDAO = passwordRequestKeyDAO;
        this.bloxopsOrganizationDAO = bloxopsOrganizationDAO;
        this.permissionDAO = permissionDAO;
        this.permissionService = permissionService;
        this.shipmentService = shipmentService;
        this.datacenterBloxopsDAO = datacenterBloxopsDAO;
        this.sendGridAPIService = sendGridAPIService;
        this.rabbitTemplate = rabbitTemplate;
        this.bonitaWebAPI = bonitaWebAPI;
        this.signingBloxopsDAO = signingBloxopsDAO;
        this.messagingService = messagingService;

    }

    public UserValidatorResponse userOrganizationSetupCheck(Long organizationId, Integer userId, SessionInfo sessionInfo) {
        UserValidatorResponse userValidatorResponse = new UserValidatorResponse();
        userValidatorResponse.setOrganization(bloxopsOrganizationDAO.getOrganizationByOrganizationId(organizationId));
        userValidatorResponse.setOrganizationServices(bloxopsOrganizationDAO.getOrganizationServices(organizationId));

        User bonUser = bonitaUserAPIService.getUserById(userId, sessionInfo);
        userValidatorResponse.setBonUserStatus(false);
        if (bonUser.getEnabled().contains("true")) {
            userValidatorResponse.setBonUserStatus(true);
        }
        List<BonitaUserMembership> memberships = bonitaUserAPIService.getUserMembershipsById(userId, sessionInfo);
        userValidatorResponse.setBonOrganizationGroup(false);
        for (BonitaUserMembership bonitaUserMembership : memberships) {
            BonitaGroup bonitaGroup = bonitaUserAPIService.getGroup(bonitaUserMembership.getGroup_id(), sessionInfo);
            try {
                if (Long.valueOf(bonitaGroup.getName()).equals(organizationId)) {
                    userValidatorResponse.setBonOrganizationGroup(true);
                }
            } catch (NumberFormatException nfe) {
                log.info("The group name "+bonitaGroup.getName()+" is not numeric");
            }
        }

        userValidatorResponse.setAdAccount(false);
        List<BonitaUserCustomDetail> bonitaUserCustomDetailList = bonitaUserAPIService.getUserCustomInfoById(userId, sessionInfo);
        for (BonitaUserCustomDetail bonitaUserCustomDetail : bonitaUserCustomDetailList) {
            if (bonitaUserCustomDetail.getDefinitionId().getName().contains("GUID") && bonitaUserCustomDetail.getValue().length() > 24) {
                    userValidatorResponse.setAdAccount(true);
            }
        }

        userValidatorResponse.setHasPermissions(permissionDAO.hasUserPermsSet(userId, organizationId));
        permissionDAO.isActive(userId, organizationId);
        userValidatorResponse.setMlStatus(permissionDAO.isActive(userId, organizationId));
        String role = permissionDAO.getRole(userId, organizationId);
        userValidatorResponse.setRole(role);
        userValidatorResponse.setInOrganization(true);
        userValidatorResponse.setSuccessfulLogin(true);
        if (null == role) {
            userValidatorResponse.setInOrganization(false);
            userValidatorResponse.setSuccessfulLogin(false);
        }

        userValidatorResponse.setErrorList(new ArrayList<>());
        return userValidatorResponse;
    }

    public void checkUserAddedToOrgUsers(long companyId, int userId , boolean active, String role , String firstname , String lastname){
       try {
           log.info("Setting new user details in organization_user table.");
           OrganizationUser orgUser = bloxopsOrganizationDAO.getOrganizationUser(companyId, userId);

           if (orgUser == null) {
               log.info("Adding new user record to organization_user table.");
               userBloxopsDAO.addUserRecord(companyId, userId, active, role, firstname, lastname);
           } else if (orgUser.getFirstName() == null && orgUser.getLastName() == null) {
               log.info("Updating name for new user record in organization_user table.");
               userBloxopsDAO.updateUsersName(userId, firstname, lastname);
           }
       }catch(Exception e){
           log.error("Error setting user details in organization_user table.");
       }
    }

    @Deprecated
    public void addUserToEnvoy(UserCreate userCreate, BonitaADAddUserResponse bonitaADAddUserResponse){
        /**
         * add user to envoy system
         */
        try{
            AddUserRequest addUserRequest = new AddUserRequest();
            addUserRequest.setFullName(userCreate.getFirstName() +" " +userCreate.getLastName());
            addUserRequest.setEmail(userCreate.getEmailAddress());
            AddUserResponse addUserResponse = shipmentService.addUserToShipmentList(addUserRequest);
            if(addUserResponse.isSuccessful()){
                log.info("User added to envoy system.");
            }else{
                errorMessageService.sendSlackErrorMessage("New Portal User Add : Error adding user " + userCreate.getFirstName() + " " +userCreate.getLastName() +" to envoy system. Manual add required. User ID : " + bonitaADAddUserResponse.getUserId() + " . EMAIL : "+ userCreate.getEmailAddress());
                log.error("User not added to envoy system");
            }
        }catch(Exception e){
            errorMessageService.sendSlackErrorMessage("New Portal User Add : Error adding user " + userCreate.getFirstName() + " " +userCreate.getLastName() +" to envoy system. Manual add required. User ID : " + bonitaADAddUserResponse.getUserId() + " . EMAIL : "+ userCreate.getEmailAddress());
            log.error("User not added to envoy system");
        }
    }

    protected boolean adUserCheck(UserCreate userCreate, SessionInfo sessionInfo) {
        BonitaADUserCheck bonitaADUserCheck;
        try {
            bonitaADUserCheck = bonitaADUserAPIService.checkIfEmailAndNameIsUsed(userCreate.getEmailAddress(), userCreate.getFirstName(), userCreate.getLastName(), sessionInfo);
            if("NAME_MISMATCH".equals(bonitaADUserCheck.getClazz())) {
                log.error("Email already assigned to different user");
                return true;
            }
        } catch (Exception e) {
            log.error("Could not confirm email address");
            log.error(e.getMessage());
        }
        return false;
    }

    protected PendingUser createPendingUser(UserCreate userCreate, String companyId) {
        PendingUser pendingUser = new PendingUser();
        try {
            pendingUser.setEmailAddress(userCreate.getEmailAddress().replace("|none",""));
            pendingUser.setFirstName(userCreate.getFirstName());
            pendingUser.setLastName(userCreate.getLastName());
            pendingUser.setOrganizationId(Integer.parseInt(companyId));
            pendingUser.setIsProcessed(false);
            pendingUser.setTimeStamp(OffsetDateTime.now());
            pendingUser.setId(pendingUserBloxopsDAO.save(pendingUser));
        } catch (Exception e) {
            log.error(SAVE_ERROR);
            log.error(e.getMessage());
        }
        return pendingUser;
    }

    public UserResponse updateUser(UserIdentificationResponse user, UserCreateRequest userCreateRequest, SessionInfo sessionInfo) {
        BonitaUserDetails bonitaUserDetails = new BonitaUserDetails();
        BonitaUserContactDetails contactDetails = new BonitaUserContactDetails();
        UserResponse userResponse;

        if(userCreateRequest.getUserDetails() == null && userCreateRequest.getContactDetails() == null) {
            log.error("No user info to save user");
            bonitaUserDetails.setErrorFields();
            contactDetails.setErrorFields();
            return new UserResponse(bonitaUserDetails, errorMessageService.getAuthenicateErrorMessage(1));
        }

        if(userCreateRequest.getUserDetails() != null) {
            try {
                bonitaUserService.updateUser(userCreateRequest.getUserDetails(), user.getUserId(), sessionInfo);
            } catch (Exception e) {
                log.error("Could not update user:"+userCreateRequest.getUserDetails().getUsername());
                log.error(e.getMessage());
                bonitaUserDetails.setErrorFields();
                user.setError(errorMessageService.getAuthenicateErrorMessage(2));
            }
        }

        if(userCreateRequest.getContactDetails() != null) {
            try {
                bonitaUserService.updateProfessionalContactDetails(userCreateRequest.getContactDetails(), user.getUserId(), sessionInfo);
                bonitaUserService.updatePersonalContactDetails(userCreateRequest.getContactDetails(), user.getUserId(), sessionInfo);
            } catch (Exception e) {
                log.error("Could not update user:"+userCreateRequest.getUserDetails().getUsername());
                log.error(e.getMessage());
                contactDetails.setErrorFields();
                bonitaUserDetails.setContactDetails(contactDetails);
                user.setError(errorMessageService.getAuthenicateErrorMessage(3));
            }
        }

        if(userCreateRequest.getContactDetails() != null && userCreateRequest.getContactDetails().getEmail() != null) {
                try {
                    profileService.updateUserProfileEmail(Integer.parseInt(user.getUserId()), userCreateRequest.getContactDetails().getEmail());
                } catch (Exception e) {
                    log.error("Unable to update user profile. user: "+userCreateRequest.getUserDetails().getUsername());
                    log.error(e.getMessage());
                    contactDetails.setEmail(ERROR);
                    user.setError(errorMessageService.getAuthenicateErrorMessage(1));
                }
        }

        try {
            String error = user.getError();
            userResponse = authenticateService.getAuthenticatedUser(user, sessionInfo);
            userResponse.setError(error);

            userResponse = checkForErrors(contactDetails, userResponse , bonitaUserDetails);

            return userResponse;
        } catch (Exception e) {
            log.error("Unable to update user:"+userCreateRequest.getUserDetails().getUsername());
            userResponse = new UserResponse();
            userResponse.setError("Unable to retrieve updated user.");
            return userResponse;
        }
    }

    public UserResponse checkForErrors(BonitaUserContactDetails contactDetails, UserResponse userResponse, BonitaUserDetails bonitaUserDetails){
        if(ERROR.equals(contactDetails.getId())) {
            userResponse.getUser().setContactDetails(contactDetails);
        } else {
            userResponse.getUser().setContactDetails(userResponse.getUser().getContactDetails());
        }

        if(ERROR.equals(bonitaUserDetails.getId())) {
            bonitaUserDetails.setContactDetails(userResponse.getUser().getContactDetails());
            bonitaUserDetails.setUserProfile(userResponse.getUser().getUserProfile());
            userResponse.setUser(bonitaUserDetails);
        }

        if(ERROR.equals(contactDetails.getEmail())) {
            userResponse.getUser().getContactDetails().setEmail(ERROR);
        }
        return userResponse;
    }

    public PasswordChangeResponse updatePassword(UserIdentificationResponse user, PasswordChangeRequest passwordChangeRequest, SessionInfo sessionInfo){
        PasswordChangeResponse response = new PasswordChangeResponse();
        BonitaUserContactDetails contactDetailsResponse = bonitaAuthenticateAPIService.getUserProfessionalDetails(user.getUserId(), sessionInfo);
        String userName = "";
        String email = "";

        if(!passwordChangeRequest.getNewPassword().equals(passwordChangeRequest.getPasswordConfirm())) {
            response.setError(PASSWORD_ERROR+" New password and confirmation do not match.");
            response.setSuccess(false);
            return response;
        }

        if(passwordChangeRequest.isAdminChange()){
            log.info("Owner is changing another users password");
            UserProfile userProfile = userBloxopsDAO.getUserProfileByEmail(passwordChangeRequest.getEmail());
            userName = userProfile.getFirstName() +"."+userProfile.getLastName();
            email = userProfile.getEmailAddress();

        }else{
            log.info("User is changing their own password");
            userName = user.getUserName();
            email = contactDetailsResponse.getEmail();;
        }

        try {
            log.info("Changing password for " + userName);
            BonitaPasswordChangeResponse bonitaResponse = bonitaPasswordChange(new BonitaPasswordChangeRequest(userName, passwordChangeRequest.getNewPassword()), sessionInfo);

            if(bonitaResponse.getError() != null && !bonitaResponse.getError().isEmpty()){
                    response.setSuccess(false);
                    response.setError(PASSWORD_ERROR);
                    errorMessageService.sendSlackErrorMessage("Update Password : Error changing password for user" + userName + " . ERROR : "+ bonitaResponse.getError());
                    log.error("Error returned from bonita trying to change password.");
                    return response;
            }else{
                log.info("Sending email notification of password change.");
                Personalization personalization = new Personalization();
                sendGridAPIService.sendEmail(email , null , personalization, "password_change");
            }

            response.setSuccess(true);
            response.setError(null);
            return response;
        } catch (Exception e) {
            log.error(PASSWORD_ERROR);
            log.error(e.getMessage());
            errorMessageService.sendSlackErrorMessage("Update Password : Error changing password for user" + userName  + " . ERROR : " + e.getMessage());
            response.setError(PASSWORD_ERROR);
            response.setSuccess(false);
            return response;
        }
    }

    public PasswordChangeResponse updateNewUserPassword(NewUserPasswordChangeRequest changeRequest){
        PasswordChangeResponse response = new PasswordChangeResponse();

        try {
            log.info("Change Request Key: "+changeRequest.getKey());
            PasswordResetKey requestKey = passwordRequestKeyDAO.findById(changeRequest.getKey());
            log.info("Change Request requestKey [PasswordResetKey]: "+requestKey);
            log.info("Logging into bonita with Admin user.");
            SessionInfo sessionInfo = bonitaAuthenticateAPIService.loginUser(bonitaAdminUser, bonitaAdminPassword);

            if(requestKey.isExpired() == true) {
                log.error("Password Change Key already used. Email: "+requestKey.getUserName());
                PasswordChangeResponse errorResponse = new PasswordChangeResponse(false,"Password Change Key already used.");
                return errorResponse;
            }

            if(requestKey.getTimestamp().plusDays(7).isBefore(OffsetDateTime.now())) {
                log.error("Window to update password has expired. Email: "+requestKey.getUserName());
                PasswordChangeResponse errorResponse = new PasswordChangeResponse(false,"Window to update password has expired.");
                return errorResponse;
            }
            BonitaPasswordChangeResponse bonitaResponse = bonitaPasswordChange(new BonitaPasswordChangeRequest(requestKey.getUserName(), changeRequest.getNewpassword()), sessionInfo);
            BonitaUserDetails bonitaUserDetails = bonitaAuthenticateAPIService.findUserByUserName(requestKey.getUserName(), sessionInfo);
            if(bonitaResponse.getError() != null && !bonitaResponse.getError().isEmpty()){
                    response.setSuccess(false);
                    response.setError(PASSWORD_ERROR);
                    errorMessageService.sendSlackErrorMessage("Update Password - New User : Error setting password for user.  " + requestKey.getUserName() + " ERROR returned from bonita : "+ bonitaResponse.getError());
                    log.error("Error returned from bonita trying to change password.");
                    return response;
            }else{
                BonitaUserContactDetails contactDetailsResponse = bonitaAuthenticateAPIService.getUserProfessionalDetails(bonitaUserDetails.getId(), sessionInfo);
                log.info("Sending email notification of password change.");
                Personalization personalization = new Personalization();
                sendGridAPIService.sendEmail(contactDetailsResponse.getEmail() , null , personalization, "password_change");
            }
            requestKey.setExpired(true);
            passwordRequestKeyDAO.save(requestKey);
            response.setSuccess(true);
            response.setError(null);
            return response;
        } catch (Exception e) {
            log.error(PASSWORD_ERROR + "Sending slack alert to development team.");
            log.error(e.getMessage());
            response.setError("Apologies, we did not see this coming... We have alerted our development team and this will be resolved shortly.");
            response.setSuccess(false);
            errorMessageService.sendSlackErrorMessage("Update Password - New User : Error setting password for user. ERROR : "+ e.getMessage());
            return response;
        }
    }
    public BonitaPasswordChangeResponse bonitaPasswordChange(BonitaPasswordChangeRequest bonitaPasswordChangeRequest,SessionInfo session){
        try {
            BonitaPasswordChangeResponse bonitaResponse = bonitaUserService.updatePassword(bonitaPasswordChangeRequest,session);
            return bonitaResponse;

        } catch (Exception e) {
            log.error(e.getMessage());
            return  new BonitaPasswordChangeResponse("Could not change bonita password",FALSE_STRING);
        }

    }

    public ProfileImageUpdateResponse updateUserProfileImage(UserIdentificationResponse user, int imageId) {
        try {
            profileService.updateUserProfileImage(Integer.parseInt(user.getUserId()), imageId);
        } catch (Exception e) {
            log.error("Could update profile image for user: " + user.getUserName());
            log.error(e.getMessage());
            return new ProfileImageUpdateResponse(false,"Could not update user profile image");
        }

        return new ProfileImageUpdateResponse(true);
    }

    public DatacenterAccessResponse getDatacenterUserAccessLogs(int card_number) {
        try{
            List<DataCenterUserAccessLog> dataCenterUserAccessLogs = datacenterBloxopsDAO.getDatacenterUserAccessLogs(card_number);
            return new DatacenterAccessResponse(dataCenterUserAccessLogs);
        }catch(Exception e){
            log.error(e.getMessage());
            return new DatacenterAccessResponse("Error getting datacenter access.");
        }
    }

    public PendingUserProceessedResponse setPendingUserIsProcessed(PendingUserProcessedRequest pendingUserProcessedRequest) {
        int companyId;

        try {
            companyId = Integer.parseInt(pendingUserProcessedRequest.getOrganizationId());
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("Could not retrieve group or users for customerid.");
            return new PendingUserProceessedResponse("Could not retrieve group or users");
        }

        PendingUser pendingUser = pendingUserBloxopsDAO.findById(Integer.parseInt(pendingUserProcessedRequest.getUserId()));
        if(pendingUser.getOrganizationId() == companyId) {
            pendingUser.setIsProcessed(true);
            long updated = pendingUserBloxopsDAO.save(pendingUser);
            if(updated > 0) {
                return new PendingUserProceessedResponse(true);
            }
        }
        return new PendingUserProceessedResponse("Could not retrieve group or users");
    }

    public UserActivationToggleResponse toggleUserActivation(UserActivationToggleRequest userActivationRequest) {
        UserCreate userCreate = new UserCreate();
        userCreate.setEnabled(String.valueOf(userActivationRequest.isEnabled()));
        userCreate.setUserId(Integer.parseInt(userActivationRequest.getUserId()));

        int userId = Integer.parseInt(userActivationRequest.getUserId());
        int orgId = Integer.parseInt(userActivationRequest.getOrganizationId());
        String role = userActivationRequest.getRole();

        try {
            bloxopsOrganizationDAO.updateOrganizationUsersStatus(userActivationRequest.isEnabled(), Long.valueOf(userActivationRequest.getUserId()));
            if(userActivationRequest.isEnabled()) {
                log.info("Add back permissions");
                List<Permission> permissions = new ArrayList<>();
                if(role.equals("owner")){
                     permissions =  permissionDAO.setDefaultOwnerPerms(userId ,orgId);

                }else{
                    permissions = permissionDAO.setDefaultPerms(userId ,orgId);
                }

                if(permissions.size() > 0){
                    if(userActivationRequest.getTicketId() != null && !userActivationRequest.getTicketId().isEmpty()){
                        messagingService.sendTicketUpdate("User has been enabled" , userActivationRequest.getTicketId());
                    }
                    return new UserActivationToggleResponse(true,"");
                }else{
                    return new UserActivationToggleResponse(false,"Error adding permissions.");
                }

            }else{
                log.info("Remove permissions");
                Boolean flushed = permissionDAO.flushUserPerms(userId, orgId);
             if(flushed){
                 messagingService.sendTicketUpdate("User has been de-activated", userActivationRequest.getTicketId());
                 return new UserActivationToggleResponse(true,"");
             }else{
                    return new UserActivationToggleResponse(false,"Error removing permissions.");
             }

            }

        } catch (Exception e) {
            log.error("Could not update USERID: " + userActivationRequest.getUserId());
            return new UserActivationToggleResponse(false,"Could not toggle User Activation. Not Authorized.");
        }
    }

    public UserDetailsResponse getUserDetailsById(int userId, int orgId, SessionInfo sessionInfo) {
        try {
            User user  = bonitaUserService.getUserById(userId ,sessionInfo);
            log.info("Getting User details for:  " + userId);
            if(user.getRole() == null){
                OrganizationUser organizationUser = bloxopsOrganizationDAO.getOrganizationUser(orgId,userId);
                String role  = organizationUser.getRole();
                user.setRole(role.replace(" ",""));
            }
            BonitaUserContactDetails contactDetailsResponse = bonitaAuthenticateAPIService.getUserProfessionalDetails(user.getId(), sessionInfo);
            log.warn("Trying to get profile for user: "+user.getUserName());
            user.setContactDetailsResponse(contactDetailsResponse);
            user.setUserProfile(profileService.retrieveUserProfile(Integer.parseInt(user.getId()), contactDetailsResponse.getEmail()));
            log.warn("Got profile for user: "+user.getUserName());
            try{
                user.setLastLogin(bloxopsOrganizationDAO.getUsersLastLogin(contactDetailsResponse.getEmail()));
            }catch(Exception e){
                log.error("Could not get last login for : "+user.getUserName());
            }

            boolean active = permissionDAO.isActive(userId, Long.valueOf(String.valueOf(orgId)));
            log.info("User "+userId+" active status: "+active);
            String status = FALSE_STRING;
            if (active) {
                status = "true";
            }
            user.setEnabled(status);

            return new UserDetailsResponse(user);
        } catch (Exception e) {
            log.error("Could not get user details for user user_id: " + userId);
            log.error(e.getMessage());
            return new UserDetailsResponse("Could not get user details.");
        }


    }

    public UserCustomImageResponse saveUserProfileImage(UserIdentificationResponse user, MultipartFile file) {
        try {
            byte[] fileBytes = file.getBytes();
            UserProfile userProfile = userBloxopsDAO.getUserProfile(Integer.parseInt(user.getUserId()));
            log.info("Getting user Details for:  " + user.getUserId());
            String fileName= DigestUtils.md5Hex(file.getName() + OffsetDateTime.now().toString());

            if(userProfile.getUserImageFilename() != null){
                log.info("Remove previous image for user:  " + user.getUserId());
                profileService.deletePreviousImage(userProfile.getUserImageFilename());
            }
            log.info("Saving users image filename.");
            profileService.saveUserProfileFilename(Integer.parseInt(user.getUserId()), fileName );

            log.info("Writing users image to the directory.");
            profileService.writeUserProfileImageFile(fileName , fileBytes);
        } catch (Exception e) {
            log.error("Could not save profile image for user: " + user.getUserName());
            log.error(e.getMessage());
            return new UserCustomImageResponse(false,"Could not save user profile image");
        }
        return new UserCustomImageResponse(true,null);
    }


    public UpdateUserImageResponse deleteUserProfileImage(UserIdentificationResponse user, SessionInfo sessionInfo) {
        try {
            UserProfile userProfile = userBloxopsDAO.getUserProfile(Integer.parseInt((user.getUserId())));
            log.info("Getting user details for:  " + user.getUserId());
            log.info("Removing users profile image from the directory for user:  " + user.getUserId());
            profileService.deletePreviousImage(userProfile.getUserImageFilename());

            log.info("Removing users profile filename.");
            profileService.deleteUserProfileFilename(Integer.parseInt((user.getUserId())));
        } catch (Exception e) {
            log.error("Could not delete profile image for user: " + user.getUserName());
            log.error(e.getMessage());
            return new UpdateUserImageResponse(false,"Could not delete user profile image");
        }

        return new UpdateUserImageResponse(true,null);

    }

    public UserListResponse getPortalUserList() {
        try{
            List<UserShort> portalUserList = userBloxopsDAO.getPortalUsers();
            return new UserListResponse(portalUserList, null);
        }catch(Exception e){
            log.error(e.getMessage());
            log.error("Error getting portal users list.");
            return new UserListResponse("error getting portal user list.");
        }
    }

    public UserActivationToggleResponse purgeUser(String userId , String orgId) {
        try{
            log.info("About to purge user "+userId + ". For Org " + orgId);
            Boolean hasUpdated = bonitaUserAPIService.purgeUser(userId , orgId ,authenticateService.adminAuth());
            return new UserActivationToggleResponse(hasUpdated, null);
        }catch(Exception e){
            log.error(e.getMessage());
            log.error("Error purging user.");
            return new UserActivationToggleResponse("error purging user.");
        }
    }

    public UserActivationToggleResponse purgeUserAdmin(String userId , String orgId , String ticketId) {
        try{
            log.info("About to purge user "+userId + ". For Org " + orgId);
            Boolean hasUpdated = bonitaUserAPIService.purgeUser(userId , orgId ,authenticateService.adminAuth());
            try{
                log.info("Flush permissions");
                permissionDAO.flushUserPerms(Integer.valueOf(userId), Integer.valueOf(orgId));
                log.info("Remove from cust_prtl_user_profile");
                userBloxopsDAO.deleteUserById(Integer.valueOf(userId));
                log.info("Remove from organization_users");
                userBloxopsDAO.deleteUserOrg(Integer.valueOf(userId));
                log.info("Remove from roster users");
                userBloxopsDAO.deleteUserRoster(Integer.valueOf(userId));}
            catch(Exception e){
                log.error("Error purging user from database.");
                log.error(e.getMessage());
            }
            messagingService.sendTicketUpdate("User has been deleted." , ticketId);
            return new UserActivationToggleResponse(hasUpdated, null);
        }catch(Exception e){
            log.error(e.getMessage());
            log.error("Error purging user.");
            return new UserActivationToggleResponse("error purging user.");
        }
    }

        public boolean checkIfUserIsOwner(int user_id, long org_id , SessionInfo sessionInfo) {
        UserDetailsResponse userDetails = getUserDetailsById(user_id, (int) org_id,sessionInfo);
        User requestingUser = userDetails.getUser();
        List<OrganizationUser> ownerList = bloxopsOrganizationDAO.getOrganizationOwners(org_id);

        boolean granted = false;
        for (OrganizationUser owner : ownerList) {
            Long ownerId = owner.getUserId();
            Long userId = Long.valueOf(user_id);
            if (ownerId.equals(userId) && !granted) {
                granted = true;
            }
            if (owner.getEmail().trim().contains(requestingUser.getUserProfile().getEmailAddress().trim()) && !granted) {
                granted = true;
            }
        }
       return granted;
    }

    public UpdateMFAResponse updateUserMFA(UserIdentificationResponse user, UpdateMFARequest updateMFARequest, SessionInfo sessionInfo) {
        userBloxopsDAO.updateUserProfileMfaMethod(Integer.valueOf(user.getUserId()) , updateMFARequest.getMfaMethod());
        return new UpdateMFAResponse(true,null);
    }

    @RabbitListener(queues = {Commons.PORTAL_USER_QUEUE}, containerFactory = "ListenerContainerFactory")
    public void addPortalUser(UserCreate userCreate) {
        log.info("Add portal user received. ");
        try{
            createUserNew(userCreate);
        }catch(Exception e){
            rabbitTemplate.convertAndSend(Commons.CREATE_USER_DLX_EXCHANGE, "portal-user-dlq",userCreate);
        }
    }

    public void createUserNew(UserCreate userCreate){
        log.info("Creating new portal user.");

        userCreate.setFirstName(Commons.capitalizeFirst(userCreate.getFirstName()));
        userCreate.setLastName(Commons.capitalizeFirst(userCreate.getLastName()));
        userCreate.setEmailAddress(userCreate.getEmailAddress().toLowerCase().trim());
        String userName = Commons.usernameGenerator(userCreate.getFirstName(), userCreate.getLastName());

        SessionInfo sessionInfo = bonitaAuthenticateAPIService.loginUser(bonitaAdminUser, bonitaAdminPassword);
        PendingUser pendingUser;

        boolean adUserExists = adUserCheck(userCreate, sessionInfo);

        if (adUserExists) {
            log.error("User already exists in ad.");
            rabbitTemplate.convertAndSend(Commons.CREATE_USER_DLX_EXCHANGE, "portal-user-dlq",userCreate);
            return ;
        }

        // Correct the case in the name
        pendingUser = createPendingUser(userCreate, userCreate.getOrganizationId());
        try {
            org.bonitasoft.web.client.model.User user = addBonitaUser(userCreate , sessionInfo);
            log.info("Creating User in AD");
            String userPassword = "1q2w3e4r";
            String role = userCreate.getRole().toLowerCase();
            addUserAD(userCreate , sessionInfo ,userPassword);
            pendingUser.setIsProcessed(true);
            pendingUserBloxopsDAO.save(pendingUser);

            /**
             * create user in cust_prtl_user_profile table
             */
            boolean isSharedUser = role.contains("shared user") ? true :false;
            userBloxopsDAO.createUserProfile(Integer.parseInt(user.getId()), userCreate.getEmailAddress(), isSharedUser);
            /**
             * create user in organization_users table
             */
            checkUserAddedToOrgUsers(Long.parseLong(userCreate.getOrganizationId()), Integer.parseInt(user.getId()),true ,userCreate.getRole() ,userCreate.getFirstName(), userCreate.getLastName());

            boolean permissionsAdded = addUserPermissions(user.getId(), userCreate.getRole(),userCreate.getOrganizationId(),userCreate.getModules() , isSharedUser);
            //addUserToEnvoy(userCreate , bonitaADAddUserResponse);

            UserValidatorResponse userCheck = userOrganizationSetupCheck(Long.valueOf(userCreate.getOrganizationId()), Integer.valueOf(user.getId()), sessionInfo);

            pendingUser.setIsProcessed(true);
            pendingUserBloxopsDAO.save(pendingUser);

            log.info("log user into bonita check");
            SessionInfo newUserSession = bonitaAuthenticateAPIService.loginUser(userName, userPassword);

            if (userCheck.isInOrganization() && userCheck.isBonOrganizationGroup() && userCheck.isBonUserStatus() && permissionsAdded && !StringUtils.isEmpty(newUserSession.getSessionId())) {
                log.info("User has been set up correctly");
                sendTicketUpdate(userCreate);
                sendWelcomeEmail(userCreate , userName);
                log.info("Added logging");
                sendLogMessages(userCreate);
                log.info("Send message to roster user link to link roster and portal user.");
                rabbitTemplate.convertAndSend(Commons.CREATE_USER_EXCHANGE, "roster-user-link",userCreate);
                rabbitTemplate.convertAndSend(Commons.CMDB_CONTACT_EXCHANGE, "cmdb-contact", userCreate);
            }else{
                errorMessageService.sendSlackErrorMessage("New Portal User Add : Error adding user " + userCreate.getFirstName() + " " +userCreate.getLastName() +". User check added to DLQ - Investigate to see what failed.  User - EMAIL : "+ userCreate.getEmailAddress());
                rabbitTemplate.convertAndSend(Commons.CREATE_USER_DLX_EXCHANGE, "portal-user-dlq",userCheck);
            }
        } catch (Exception e) {
            errorMessageService.sendSlackErrorMessage("New Portal User Add : Error adding user " + userCreate.getFirstName() + " " +userCreate.getLastName() +". User check added to DLQ - Investigate to see what failed.  User - EMAIL : "+ userCreate.getEmailAddress());
            rabbitTemplate.convertAndSend(Commons.CREATE_USER_DLX_EXCHANGE, "portal-user-dlq",userCreate);
            log.error(SAVE_ERROR);
            e.printStackTrace();
            log.error(e.getMessage());
        }

    }

    public UserActivationToggleResponse addExistingUserToOrganization(String userId , String orgId , String ticketId){
        log.info("Adding existing portal user to new organization.");

        OrganizationUser user = bloxopsOrganizationDAO.getOrganizationUserById(Integer.parseInt(userId));
        if(user == null){
            return new UserActivationToggleResponse(false, "User not found in portal.");
        }
        try {
            /**
             * Add user to group in bonita
             */
            addUserToBonitaGroup(user, orgId);
            /**
             * create user in organization_users table
             */
            checkUserAddedToOrgUsers(Long.parseLong(orgId), Integer.parseInt(userId),true , user.getRole() ,user.getFirstName(), user.getLastName());
            List<String > modules = new ArrayList<>();
            boolean permissionsAdded = addUserPermissions(String.valueOf(user.getUserId()), user.getRole(),orgId,modules , false);
            messagingService.sendTicketUpdate("User has been added to organization - Organization ID: " + orgId, ticketId);
            return new UserActivationToggleResponse(true, null);
        } catch (Exception e) {
            errorMessageService.sendSlackErrorMessage("Existing Portal User Add : Error adding user " + user.getFirstName() + " " +user.getLastName() +". User check added to DLQ - Investigate to see what failed.  User - ID : "+ userId);
            log.error(SAVE_ERROR);
            log.error(e.getMessage());
            return new UserActivationToggleResponse(false, "User not found in portal.");
        }

    }



    private void addUserAD(UserCreate userCreate , SessionInfo sessionInfo , String userPassword) throws JsonProcessingException {
        BonitaADAddUserRequest bonitaADAddUserRequest = new BonitaADAddUserRequest(userCreate.getEmailAddress(), userCreate.getFirstName(), userCreate.getLastName(), userPassword);
        bonitaADUserAPIService.addUser(bonitaADAddUserRequest, sessionInfo);
    }

    private org.bonitasoft.web.client.model.User addBonitaUser(UserCreate userCreate , SessionInfo sessionInfo) {
        Role member = bonitaWebAPI.getRole("member");
        Group group = bonitaWebAPI.getGroup(String.valueOf(userCreate.getOrganizationId()));
        Group adGroup = bonitaWebAPI.getGroup("AD Users");
        //create addUserADuser in bonita
        org.bonitasoft.web.client.model.User user = bonitaWebAPI.createUser(userCreate);

        //add company membership to user
        bonitaWebAPI.addMembership(member.getId() ,group.getId(), user.getId());
        //add AD membership to user
        bonitaWebAPI.addMembership(member.getId() ,adGroup.getId(), user.getId());

        //add profile
        bonitaWebAPI.addProfile(user.getId());
        //add email to user in bonita
        BonitaUserContactDetails bonitaUserContactDetails = new BonitaUserContactDetails();
        bonitaUserContactDetails.setEmail(userCreate.getEmailAddress());
        bonitaUserAPIService.updatePersonalContactDetails(bonitaUserContactDetails ,user.getId(), sessionInfo);
        bonitaUserAPIService.updateProfessionalContactDetails(bonitaUserContactDetails,user.getId(), sessionInfo);
        return user;
    }

    private org.bonitasoft.web.client.model.User addUserToBonitaGroup(OrganizationUser userShort ,String orgId) {
        Role role = bonitaWebAPI.getRole(userShort.getRole());

        Group group = bonitaWebAPI.getGroup(String.valueOf(orgId));
        //create user in bonita
        org.bonitasoft.web.client.model.User user = bonitaWebAPI.getUser(userShort.getFirstName()+"."+ userShort.getLastName());

        //add company membership to user
        bonitaWebAPI.addMembership(role.getId() ,group.getId(), user.getId());

        return user;
    }

    private void sendLogMessages(UserCreate userCreate) {
        LogRecord logRecord = new LogRecord(userCreate.getCreatedBy() , Integer.valueOf(userCreate.getOrganizationId()),"User created - " +userCreate.getEmailAddress(),"User Create");
        rabbitTemplate.convertAndSend(Commons.LOGGING_EXCHANGE, "logs-user",logRecord);
        rabbitTemplate.convertAndSend(Commons.LOGGING_EXCHANGE, "logs-system",logRecord);
    }

    public boolean addUserPermissions(String userId, String role, String orgId, List<String> modules, boolean isSharedUser) {
        boolean updated = false;
        String roleLower = role.toLowerCase();
        log.info("Setting permissions for role - " + roleLower);
        if(roleLower.contains("member") || isSharedUser){
            log.info("Setting permissions member");
            permissionDAO.setDefaultPerms(Integer.parseInt(userId), Integer.parseInt(orgId));
            if(!modules.isEmpty()){
                boolean billingAccess = modules.contains("billing");
                if(billingAccess){
                    log.info("Setting module permissions - billing access member");
                    permissionDAO.setBillingPerms(Integer.parseInt(userId), Integer.parseInt(orgId));
                }
                boolean storageAccess = modules.contains("storage");
                if(storageAccess){
                    log.info("Setting module permissions - storage access member");
                    permissionDAO.setStoragePerms(Integer.parseInt(userId), Integer.parseInt(orgId));
                }
            }
            updated =true;
        }else if(roleLower.contains("owner")){
            log.info("Setting permissions owner");
            permissionDAO.setDefaultOwnerPerms(Integer.parseInt(userId), Integer.parseInt(orgId));
            if(!modules.isEmpty()){
                boolean billingAccess = modules.contains("billing");
                    if (billingAccess) {
                        log.info("Setting module permissions - billing access owner");
                        permissionDAO.setBillingOwnerPerms(Integer.parseInt(userId), Integer.parseInt(orgId));
                    }
                    boolean storageAccess = modules.contains("storage");
                    if (storageAccess) {
                        log.info("Setting module permissions - storage access owner");
                        permissionDAO.setStoragePerms(Integer.parseInt(userId), Integer.parseInt(orgId));
                    }
            }
            updated =true;
        }

        return updated;
    }

    public void sendWelcomeEmail(UserCreate userCreate , String username) throws IOException {
        Communication communication = signingBloxopsDAO.getCommunication("welcome_email");
        PasswordUrlRequest passwordUrlRequest = new PasswordUrlRequest(username ,middlewareAdminKey);
        PasswordUrlResponse passwordUrlResponse = authenticateService.generatePasswordChangeURL(passwordUrlRequest);
        sendGridAPIService.sendWelcomeEmail(userCreate.getEmailAddress(), communication.getChannel() , userCreate ,passwordUrlResponse.getUrl());
        LogRecord logRecord = new LogRecord(userCreate.getCreatedBy() , Integer.valueOf(userCreate.getOrganizationId()),"Welcome email sent to - " + userCreate.getEmailAddress(),"User Create");
        rabbitTemplate.convertAndSend(Commons.LOGGING_EXCHANGE, "logs-user",logRecord);
        rabbitTemplate.convertAndSend(Commons.LOGGING_EXCHANGE, "logs-system",logRecord);
    }

    private void sendTicketUpdate(UserCreate userCreate) {
            TicketUpdateRequest ticketUpdateRequest = new TicketUpdateRequest();
            ticketUpdateRequest.setCaseid(String.valueOf(userCreate.getTicketID()));
            ticketUpdateRequest.setComment("User created successfully.");
            ticketUpdateRequest.setStatus("New");
            ticketUpdateRequest.setOrganizationId(userCreate.getOrganizationId());
            rabbitTemplate.convertAndSend(Commons.SUPPORT_EXCHANGE, "support-update", ticketUpdateRequest);
    }

    private CGTicketUpdateRequest createCGTicketUpdateRequest(UserCreate userCreate) {
       CGTicketUpdateRequest cgTicketUpdateRequest = new CGTicketUpdateRequest();

        TicketUpdateDetails ticketUpdateDetails = new TicketUpdateDetails();
        List<CGComment> HistoryRecords = new ArrayList<>();
        CGComment cgComment = new CGComment();
        cgComment.setAction("Description");
        cgComment.setComment("User Created Successfully.");
        ticketUpdateDetails.setHistoryRecords(HistoryRecords);

        cgTicketUpdateRequest.setTicketId(userCreate.getTicketID());
        cgTicketUpdateRequest.setTicketType(userCreate.getTicketType());
        cgTicketUpdateRequest.setTicketUpdateDetails(ticketUpdateDetails);

        return cgTicketUpdateRequest;
    }

    public UserRoleChangeResponse changeUserRole(UserRoleChangeRequest userRoleChangeRequest) {
        UserRoleChangeResponse userRoleChangeResponse = new UserRoleChangeResponse();
        String role = userRoleChangeRequest.getRole();
        int orgId = userRoleChangeRequest.getOrgId();
        int userId = userRoleChangeRequest.getUserId();
        List<Permission> permissions = new ArrayList<>();
        log.info("Flushing user permissions.");
            permissionDAO.flushUserPerms(userId, orgId);
        if(role.contains("member")){
            permissions = permissionDAO.setDefaultPerms(userId, orgId);
            userBloxopsDAO.updateSharedUser(userRoleChangeRequest.getUserId() , false);
        }else if(role.contains("owner")){
            permissions = permissionDAO.setDefaultOwnerPerms(userId, orgId);
            userBloxopsDAO.updateSharedUser(userRoleChangeRequest.getUserId() , false);
        }else if(role.contains("shared")){
            permissions = permissionDAO.setDefaultPerms(userId, orgId);
            userBloxopsDAO.updateSharedUser(userRoleChangeRequest.getUserId() , true);
        }
        log.info("Update DB with role change");
        int completed = bloxopsOrganizationDAO.updateOrganizationUsersRole(orgId, userId, role);
        if(permissions.size() > 0 && completed > 0){
            userRoleChangeResponse.setSuccess(true, null);
        }else{
            userRoleChangeResponse.setSuccess(false, "User role update failed.");
        }

        if(userRoleChangeRequest.getTicketId() != null && !userRoleChangeRequest.getTicketId().isEmpty()){
            messagingService.sendTicketUpdate("Users role has been changed to " + userRoleChangeRequest.getRole() , userRoleChangeRequest.getTicketId());
        }
        return userRoleChangeResponse;
    }

    public EmailResponseAdmin sendEmailAdmin(EmailRequestAdmin emailRequest) {
        log.info("Sending email to - " + emailRequest.getEmail());
        try {
            if(emailRequest.getEmailType().contains("welcome")) {
                UserCreate userCreate = new UserCreate();
                userCreate.setEmailAddress(emailRequest.getEmail());
                userCreate.setOrganizationId(emailRequest.getOrgId());
                userCreate.setCreatedBy(0);

                sendWelcomeEmail(userCreate, emailRequest.getUserName());
                if (emailRequest.getTicketId() != null && !emailRequest.getTicketId().isEmpty()) {
                    messagingService.sendTicketUpdate("Welcome email has been sent to " + emailRequest.getEmail(), emailRequest.getTicketId());
                }
            }
           return new EmailResponseAdmin(true , null);
        } catch (IOException e) {
            return new EmailResponseAdmin(false , e.getMessage());
        }

    }
}
