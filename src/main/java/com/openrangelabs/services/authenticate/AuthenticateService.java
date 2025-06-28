package com.openrangelabs.services.authenticate;

import com.openrangelabs.services.authenticate.bloxops.dao.PasswordRequestKeyDAO;
import com.openrangelabs.services.authenticate.bonita.BonitaAuthenticateAPIService;
import com.openrangelabs.services.authenticate.bonita.model.BonitaForgotPasswordRequest;
import com.openrangelabs.services.authenticate.bonita.model.BonitaCaseResponse;
import com.openrangelabs.services.authenticate.model.*;
import com.openrangelabs.services.authenticate.model.*;
import com.openrangelabs.services.authenticate.permission.PermissionService;
import com.openrangelabs.services.authenticate.permission.model.Organization;
import com.openrangelabs.services.authenticate.verify.VerifyAPIService;
import com.openrangelabs.services.eula.EulaService;
import com.openrangelabs.services.log.model.LogRecord;
import com.openrangelabs.services.message.ErrorMessageService;
import com.openrangelabs.services.message.MessagingService;
import com.openrangelabs.services.organization.bloxops.dao.BloxopsOrganizationDAO;
import com.openrangelabs.services.ticket.model.ProcessResponse;
import com.openrangelabs.services.tools.Commons;
import com.openrangelabs.services.user.bonita.BonitaUserAPIService;
import com.openrangelabs.services.authenticate.bloxops.dao.SessionDAO;
import com.openrangelabs.services.authenticate.bonita.dao.AuthenticateDetailsDAO;
import com.openrangelabs.services.user.bonita.model.*;
import com.openrangelabs.services.user.model.*;
import com.openrangelabs.services.user.bonita.model.BonitaUserContactDetails;
import com.openrangelabs.services.user.bonita.model.BonitaUserDetails;
import com.openrangelabs.services.user.model.PasswordUrlRequest;
import com.openrangelabs.services.user.model.PasswordUrlResponse;
import com.openrangelabs.services.user.model.UserIdentificationResponse;
import com.openrangelabs.services.user.model.UserResponse;
import com.openrangelabs.services.user.profile.ProfileService;
import com.openrangelabs.services.user.profile.dao.UserBloxopsDAO;
import com.openrangelabs.services.user.profile.model.UserProfile;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;
@Slf4j
@Service
public class AuthenticateService {

    VerifyAPIService verifyAPIService;
    private ProfileService profileService;
    BonitaAuthenticateAPIService bonitaAuthenticateAPIService;
    BonitaUserAPIService bonitaUserAPIService;
    EulaService eulaService;
    SessionDAO sessionDAO;
    ErrorMessageService errorMessageService;
    AuthenticateDetailsDAO authDetailsDAO;
    PermissionService permissionService;
    PasswordRequestKeyDAO passwordRequestKeyDAO;
    UserBloxopsDAO userBloxopsDAO;
    BloxopsOrganizationDAO bloxopsOrganizationDAO;
    RabbitTemplate rabbitTemplate;
    MessagingService messagingService;

    @Value("${MyOrlDomain}")
    String myDCbloxDomain;

    @Value("${bonitaAdminUser}")
    String bonitaAdminUser;

    @Value("${bonitaAdminPassword}")
    String bonitaAdminPassword;

    @Value("${middlewareAdminKey}")
    String middlewareAdminKey;

    @Value("${twilio.serviceSID}")
    String serviceSID;

    String numberRegex ="[^0-9]";

    String bonitaInfoMessage = "Trying to get Professional Contact Details from Bonita for username:";

    @Autowired
    AuthenticateService(VerifyAPIService verifyAPIService, BonitaAuthenticateAPIService bonitaAuthenticateAPIService,
                        ProfileService profileService, SessionDAO sessionDAO, ErrorMessageService errorMessageService,
                        AuthenticateDetailsDAO authDetailsDAO, BonitaUserAPIService bonitaUserAPIService,
                        PasswordRequestKeyDAO passwordRequestKeyDAO, PermissionService permissionService,
                        UserBloxopsDAO userBloxopsDAO , EulaService eulaService,BloxopsOrganizationDAO bloxopsOrganizationDAO ,  RabbitTemplate rabbitTemplate, MessagingService messagingService) {
        this.bonitaAuthenticateAPIService = bonitaAuthenticateAPIService;
        this.verifyAPIService = verifyAPIService;
        this.profileService = profileService;
        this.sessionDAO = sessionDAO;
        this.errorMessageService = errorMessageService;
        this.authDetailsDAO = authDetailsDAO;
        this.bonitaUserAPIService = bonitaUserAPIService;
        this.passwordRequestKeyDAO = passwordRequestKeyDAO;
        this.permissionService = permissionService;
        this.userBloxopsDAO = userBloxopsDAO;
        this.eulaService = eulaService;
        this.bloxopsOrganizationDAO = bloxopsOrganizationDAO;
        this.rabbitTemplate = rabbitTemplate;
        this.messagingService = messagingService;
    }

    @Deprecated
    private void logoutOldSessions(String userName){
      try {
            SessionInfo oldSession = sessionDAO.getSessionToken(userName);
//            if(!"".equals(oldSession.getSessionToken())) {
//                bonitaAuthenticateAPIService.logout(oldSession);
//            }
        } catch(Exception e) {
          log.warn(e.getMessage());
        }
    }

    public SessionInfo adminAuth() {
        return bonitaAuthenticateAPIService.loginUser(bonitaAdminUser, bonitaAdminPassword);
    }

    public UserResponse authenticateUserCredentials(LoginRequest loginRequest) {
        Boolean useSecondFactorLogin = true;
        log.info("Start of login process.");
        log.info("Use second factor login : " + useSecondFactorLogin);
        IpDetails ipDetails = loginRequest.getIpDetails();

        if (useSecondFactorLogin) {
            try {
                useSecondFactorLogin = userBloxopsDAO.getUserProfileSecondFactor(loginRequest.getUserName());
                log.info("Got second factor from user profile : " + useSecondFactorLogin);
                if (!useSecondFactorLogin) {
                    log.info("Second factor login false : " + useSecondFactorLogin);
                    UserProfile userProfile = userBloxopsDAO.getUserProfileByEmail(loginRequest.getUserName());
                    log.info("Getting user profile : " + userProfile);
                    if (null == userProfile) {
                        log.info("Creating user profile.");
                        useSecondFactorLogin = createUserProfile(loginRequest);
                        log.info("User profile created. " + useSecondFactorLogin);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                useSecondFactorLogin = false;
            }
        }
        if (useSecondFactorLogin) {
            useSecondFactorLogin = initialiseVerifyAPI();
        }
        UserResponse userResponse;
        log.info("Second Factor Login :" + useSecondFactorLogin);
        if(useSecondFactorLogin) {
            log.info("Using second factor login.");
            userResponse = authenticateUserCredentialsSecondFactor(loginRequest);
        } else {
            log.info("Using single factor login.");
            userResponse = authenticateUserCredentialsSingleFactor(loginRequest);
        }
        userResponse.setMfa(useSecondFactorLogin);
        sendLogRecord(userResponse,"Portal Login Success - " + "\n Public IP: "+ ipDetails.getPublicIp() + ". \nPrivate IP: " + ipDetails.getPrivateIp());
        return userResponse;
    }

    public void sendLogRecord(UserResponse userResponse, String message){
        try {
            BonitaUserDetails user = userResponse.getUser();
            List<Organization> organizations = userResponse.getMemberships();

            if (null != user) {
                log.info("Sending log record for " + user.getId());
                int organizationId = 0;
                if(null != organizations){
                    organizationId = (int) organizations.get(0).getOrganizationId();
                }
                LogRecord logRecord = new LogRecord(Integer.valueOf(user.getId()),organizationId, message + user.getUserName() , "Login");
                rabbitTemplate.convertAndSend(Commons.LOGGING_EXCHANGE, "logs-user", logRecord);
                rabbitTemplate.convertAndSend(Commons.LOGGING_EXCHANGE, "logs-system", logRecord);
            }
        }catch(Exception e){
            log.error("Error sending logging on login. ");
            log.error(e.getMessage());
        }
    }

    public boolean createUserProfile(LoginRequest loginRequest){
        UserProfile userProfile;
        Boolean useSecondFactorLogin =false;
        try {
            int id = authDetailsDAO.getUserId(loginRequest.getUserName());
            if (id < 1) {
                userBloxopsDAO.createUserProfile(id, loginRequest.getUserName() , false);
                userProfile = userBloxopsDAO.getUserProfileByEmail(loginRequest.getUserName());
                userBloxopsDAO.updateUserProfileMfa(userProfile.getId(), false);
                useSecondFactorLogin = userBloxopsDAO.getUserProfileSecondFactor(loginRequest.getUserName());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            useSecondFactorLogin = false;
        }
        return useSecondFactorLogin;
    }

    public boolean initialiseVerifyAPI(){
        try {
            return verifyAPIService.initialise();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("failed to initialise verify api " + e.getMessage());
            return false;
        }
    }

    public UserResponse authenticateUserCredentialsSingleFactor(LoginRequest loginRequest) {
        UserIdentificationResponse userIdResponse = null;
        SessionInfo sessionInfo;
        String email = "";
        //logoutOldSessions(loginRequest.getUserName());
        try {
            // Bonita username replaces the email the user logged in with
            email = loginRequest.getUserName();
            String password = loginRequest.getPassword();
            log.info("Trying to get username with email: "+email+" from Bonita" );
            String bonitaUserName = authDetailsDAO.getUserName(email);
            log.info("Found username "+bonitaUserName+" with email: "+email+" from  bonita" );
            log.info("Trying trying to login to bonita with username: "+bonitaUserName);
            sessionInfo = bonitaAuthenticateAPIService.loginUser(bonitaUserName, password);
            log.info("Got user GUID from bonita");
            log.info("Trying to save session token to middleware database");
            sessionDAO.setSessionToken(email, sessionInfo);
            log.info("Logged and got bonita-x-token: "+sessionInfo.getSessionToken()+"for User"+bonitaUserName);
            log.info("Logged user into bonita");
            log.info("Trying to get user identification from Bonita for username: "+bonitaUserName);
            userIdResponse = bonitaAuthenticateAPIService.getUserIdentification(sessionInfo);
            log.info("Got user ID from bonita userID: "+userIdResponse.getUserId()+ " userName: "+userIdResponse.getUserName());

            if(userIdResponse.getUserId() == null){
                return checkIfActiveUser(email ,loginRequest);
            }
            return getAuthenticatedUser(userIdResponse, sessionInfo);
        } catch (Exception e) {
            LogRecord logRecord = new LogRecord(0,0,e.getMessage() + email , "Login Error");
            rabbitTemplate.convertAndSend(Commons.LOGGING_EXCHANGE, "logs-user", logRecord);
            rabbitTemplate.convertAndSend(Commons.LOGGING_EXCHANGE, "logs-system", logRecord);
            log.error("Unable to authenticate email: "+email+" username: "+loginRequest.getUserName()+"with Bonita. " + e.toString());
            return checkIfActiveUser(email ,loginRequest);
        }
    }
    public UserResponse checkIfActiveUser(String email ,LoginRequest loginRequest){

        try {
            UserProfile user = userBloxopsDAO.getUserProfileByEmail(email);

            if (user != null) {
                SessionInfo sessionInfo = adminAuth();
                int userId = user.getId();
                BonitaUserDetails bonitaUser = bonitaAuthenticateAPIService.getUserDetails(String.valueOf(userId), sessionInfo);
                String enabled = bonitaUser.getEnabled();

                if (enabled.contains("true")) {
                    log.error("Active user - Could not get user from bonita userID: " + userId + " email: " + email);
                    UserResponse errorUser = new UserResponse();
                    errorUser.setError(errorMessageService.getPasswordErrorMessage(loginRequest.getAttemptCount()));
                    return errorUser;
                } else {
                    log.error("Inactive user - Could not get user from bonita userID: " + userId + " email: " + email);
                    UserResponse errorUser = new UserResponse();
                    errorUser.setError(errorMessageService.getInactiveUserErrorMessage(loginRequest.getAttemptCount()));
                    return errorUser;
                }
                //Todo - bonita user is active but user is not active in cust_portal_user table or vice versa.
            } else {
                log.error("No record of user " + email);
                UserResponse errorUser = new UserResponse();
                errorUser.setError(errorMessageService.getPasswordErrorMessage(loginRequest.getAttemptCount()));
                return errorUser;
            }
        }catch(Exception e){
            log.error("Error getting user from bonita email: " + email);
            UserResponse errorUser = new UserResponse();
            errorUser.setError(errorMessageService.getInactiveUserErrorMessage(loginRequest.getAttemptCount()));
            return errorUser;
        }

    }

    public UserResponse authenticateUserCredentialsSecondFactor(LoginRequest loginRequest){
        UserResponse userResponse = new UserResponse();
        UserIdentificationResponse userIdResponse;
        BonitaUserDetails userDetailResponse;
        BonitaUserContactDetails contactDetailsResponse;
        SessionInfo sessionInfo;
        String email = "";
        //logoutOldSessions(loginRequest.getUserName());

        try {
            // Bonita username replaces the email the user logged in with
            email = loginRequest.getUserName();
            String password = loginRequest.getPassword();
            log.warn("Trying to get username with email: "+email+" from bonita" );
            String bonitaUserName = authDetailsDAO.getUserName(email);
            log.warn("Found username "+bonitaUserName+" with email: "+email+" from bonita" );
            log.warn("Trying trying to login to bonita with username: "+bonitaUserName);
            sessionInfo = bonitaAuthenticateAPIService.loginUser(bonitaUserName, password);
            log.warn("Logged and got bonita-x-token: "+sessionInfo.getSessionToken()+"for user "+bonitaUserName);
            log.warn("Logged user into bonita");
            log.warn("Trying to get user identification from Bonita for username: "+bonitaUserName);
            userIdResponse = bonitaAuthenticateAPIService.getUserIdentification(sessionInfo);
            log.warn("Got user ID from bonita userID: "+userIdResponse.getUserId()+ " userName: "+userIdResponse.getUserName());
            if(userIdResponse.getUserId() == null){
                return checkIfActiveUser(email ,loginRequest);
            }
            log.warn("Trying to get GUID from bonita for userName: "+userIdResponse.getUserName());
            bonitaAuthenticateAPIService.extractGUID(bonitaAuthenticateAPIService.getCustomUserDetails(userIdResponse.getUserId(), sessionInfo));
            log.warn("Got user GUID from bonita");
            log.warn("Trying to save session token to middleware database");
            sessionDAO.setSessionToken(email, sessionInfo);
            log.warn("Saved the session to middleware database");
            log.warn("Trying to get user details. Username: "+userIdResponse.getUserId());
            userDetailResponse = bonitaAuthenticateAPIService.getUserDetails(userIdResponse.getUserId(), sessionInfo);
            log.warn("Got user details from bonita");
            log.warn(bonitaInfoMessage+userIdResponse.getUserName());
            contactDetailsResponse = bonitaAuthenticateAPIService.getUserProfessionalDetails(userIdResponse.getUserId(), sessionInfo);
            log.warn("Got user professional details from bonita");

            List<SecondFactorAuthType> authTypes = new ArrayList<>();
            authTypes.add(SecondFactorAuthType.EMAIL);

            userDetailResponse.setEmailPartial(contactDetailsResponse.getEmail().substring(0,1)
                    +"****"+contactDetailsResponse.getEmail().substring(contactDetailsResponse.getEmail().indexOf("@"),contactDetailsResponse.getEmail().length()));

            String phoneArea = contactDetailsResponse.getMobileNumber();
            if (phoneArea.length() > 3) {
                phoneArea = contactDetailsResponse.getMobileNumber().replaceAll(numberRegex, "").substring(0, 3);
            } else {
                phoneArea = String.valueOf(9072000000L + Long.parseLong(userIdResponse.getUserId()));
                phoneArea = phoneArea.replaceAll(numberRegex, "").substring(0, 3);
            }
            if(contactDetailsResponse.getMobileNumber() != null && !"".equals(contactDetailsResponse.getMobileNumber())
                && !"907".equals(phoneArea)) {
                 String mobileNumber = contactDetailsResponse.getMobileNumber().replaceAll(numberRegex, "");
                 if (mobileNumber.length() == 10) {
                     authTypes.add(SecondFactorAuthType.SMS);
                     userDetailResponse.setMobilePhonePartial("("+mobileNumber.substring(0, 1)
                             + "**) ***-" + mobileNumber.substring(6, mobileNumber.length()));
                 }
             }
            userDetailResponse.setSharedUser(userBloxopsDAO.getUserProfileByEmail(email).isSharedUser());
            userDetailResponse.setAuthTypes(authTypes);
            log.warn("Set second factor allowable types");

            userResponse.setUser(userDetailResponse);
        } catch (Exception e) {
            e.printStackTrace();
            LogRecord logRecord = new LogRecord(0,0,e.getMessage() + email , "Login Error");
            rabbitTemplate.convertAndSend(Commons.LOGGING_EXCHANGE, "logs-user", logRecord);
            rabbitTemplate.convertAndSend(Commons.LOGGING_EXCHANGE, "logs-system", logRecord);
            log.error("Unable to authenticate email: "+email+" username: "+loginRequest.getUserName()+"with Bonita. " + e.toString());
            UserResponse errorUser = new UserResponse();
            errorUser.setError(errorMessageService.getPasswordErrorMessage(loginRequest.getAttemptCount()));
            return errorUser;
        }
        log.warn("Returning final user");
        return userResponse;
    }

    public UserResponse sendSecondFactorOneTimeCode(SecondFactorSendAuthCodeRequest authCodeRequest) {
        UserResponse userResponse = new UserResponse();
        UserIdentificationResponse userIdResponse;
        BonitaUserContactDetails contactDetailsResponse;
        String mobileNumber;

        try {
            SessionInfo sessionInfo = sessionDAO.getSessionToken(authCodeRequest.getUserName());
            log.info("Found bonita-x-token: "+sessionInfo.getSessionToken()+"for user"+authCodeRequest.getUserName());
            userIdResponse = bonitaAuthenticateAPIService.getUserIdentification(sessionInfo);
            log.info("Trying to get user details. Username: "+userIdResponse.getUserId());
            userResponse.setUser(bonitaAuthenticateAPIService.getUserDetails(userIdResponse.getUserId(), sessionInfo));
            log.info(bonitaInfoMessage+userIdResponse.getUserName());
            contactDetailsResponse = bonitaAuthenticateAPIService.getUserProfessionalDetails(userIdResponse.getUserId(), sessionInfo);
        } catch (Exception e) {
            log.error("Could not get user identification or session token: " + authCodeRequest.getUserName());
            UserResponse errorUser = new UserResponse();
            errorUser.setError(errorMessageService.getAuthenicateErrorMessage(authCodeRequest.getAttemptCount()));
            return errorUser;
        }

        try {
            mobileNumber = contactDetailsResponse.getMobileNumber();
            if(mobileNumber != null && !mobileNumber.isEmpty()){
                mobileNumber = contactDetailsResponse.getMobileNumber().replaceAll(numberRegex, "");
            }

            if(SecondFactorAuthType.SMS.equals(authCodeRequest.getAuthType())) {
               Boolean sent = sendSMS(userIdResponse ,contactDetailsResponse , mobileNumber);
               if(!sent){
                   log.error("Unable to send sms. User: " + userIdResponse.getUserName() + " Phone: " + contactDetailsResponse.getMobileNumber());
                   authCodeRequest.setAuthType(SecondFactorAuthType.EMAIL);
               }
            }
        } catch(Exception e) {
            log.error("Unable to send sms will attempt email");
            authCodeRequest.setAuthType(SecondFactorAuthType.EMAIL);
        }

        try {
            sendEmailSecondFactorCode(authCodeRequest,userIdResponse ,contactDetailsResponse);
        } catch(Exception e) {
            log.error("Unable to send email. User: " + userIdResponse.getUserName()+" Email: "+contactDetailsResponse.getEmail());
            UserResponse errorUser = new UserResponse();
            errorUser.setError(errorMessageService.getAuthenicateErrorMessage(authCodeRequest.getAttemptCount()));
            return errorUser;
        }

        return userResponse;
    }

    private void sendEmailSecondFactorCode(SecondFactorSendAuthCodeRequest authCodeRequest ,UserIdentificationResponse userIdResponse ,BonitaUserContactDetails contactDetailsResponse) throws Exception {
        if(SecondFactorAuthType.EMAIL.equals(authCodeRequest.getAuthType())) {
            log.info(userIdResponse.getUserName()+"Trying to send second factor email: "+contactDetailsResponse.getEmail());

           if(userBloxopsDAO.getUserProfileByEmail(contactDetailsResponse.getEmail()).isSharedUser() && !authCodeRequest.getSharedUserEmail().isEmpty()){
               if (!verifyAPIService.sendVerifyCode(authCodeRequest.getSharedUserEmail(), "email")) {
                   throw new Exception("unable to send email to input email for shared user " + authCodeRequest.getSharedUserEmail());
               }
           }else {
               if (!verifyAPIService.sendVerifyCode(contactDetailsResponse.getEmail(), "email")) {
                   throw new Exception("unable to send email");
               }
           }
        }
        log.info("Sent email to user: " + userIdResponse.getUserName()
                + " with email address: " + contactDetailsResponse.getEmail() + "Email Status: ");
    }

    public boolean sendSMS(UserIdentificationResponse userIdResponse, BonitaUserContactDetails contactDetailsResponse, String mobileNumber){
        Boolean sent = false;
        log.info(userIdResponse.getUserName()+"Trying to send second factor sms: "+contactDetailsResponse.getEmail());
        if (contactDetailsResponse.getMobileNumber() != null && !"".equals(contactDetailsResponse.getMobileNumber())
                && mobileNumber.length() == 10) {

            String phoneNumber = verifyAPIService.formatPhoneNumber(mobileNumber);

            if (phoneNumber == null || !verifyAPIService.sendVerifyCode(phoneNumber,"sms")) {
                log.error("unable to send sms");
            }else{
                sent = true;
            }
        }
        return sent;
    }

    public boolean authenticateOneTimeCode(String oneTimeCode, String to) {
        try {
            VerificationCheck verificationCheck = VerificationCheck.creator(
                   serviceSID )
                    .setTo(to)
                    .setCode(oneTimeCode)
                    .create();

            return verificationCheck.getStatus().equals("approved");

        } catch(Exception e) {
            log.warn("Could not authenticate with one time code. GUID: " +e );
            return false;
        }
    }

    public UserResponse authenticateUserSecondFactor(SecondFactorAuthRequest authRequest) {
        UserIdentificationResponse userIdResponse;
        BonitaUserContactDetails contactDetailsResponse;
        String to = "";
        String mobileNumber ="";
        UserResponse errorUser = new UserResponse();

        try {
            SessionInfo sessionInfo = sessionDAO.getSessionToken(authRequest.getUserName());
            log.warn("Found bonita-x-token: "+sessionInfo.getSessionToken()+"for user"+authRequest.getUserName());
            userIdResponse = bonitaAuthenticateAPIService.getUserIdentification(sessionInfo);
            log.warn("Got User ID: "+userIdResponse.toString());
            contactDetailsResponse = bonitaAuthenticateAPIService.getUserProfessionalDetails(userIdResponse.getUserId(), sessionInfo);
            if(SecondFactorAuthType.EMAIL.equals(authRequest.getAuthType())) {
                if(userBloxopsDAO.getUserProfileByEmail(contactDetailsResponse.getEmail()).isSharedUser() && !authRequest.getSharedUserEmail().isEmpty()){
                    to = authRequest.getSharedUserEmail();
                }else{
                    to = contactDetailsResponse.getEmail();
                }
            }else if(SecondFactorAuthType.SMS.equals(authRequest.getAuthType())){
                mobileNumber = contactDetailsResponse.getMobileNumber();
                if(null != mobileNumber  && !mobileNumber.isEmpty()){
                    mobileNumber = contactDetailsResponse.getMobileNumber().replaceAll(numberRegex, "");
                }

                if (contactDetailsResponse.getMobileNumber() != null && !"".equals(contactDetailsResponse.getMobileNumber())
                        && null != mobileNumber && mobileNumber.length() == 10) {
                    String phoneNumber = verifyAPIService.formatPhoneNumber(mobileNumber);
                    to  = checkPhoneNumber(phoneNumber , contactDetailsResponse);

                }else{
                    log.info("If phone number is not valid try email");
                    to = contactDetailsResponse.getEmail();
                }


            }
            if(authenticateOneTimeCode(authRequest.getOneTimeCode(), to)) {
                return getAuthenticatedUser(userIdResponse, sessionInfo);
            }else{
                errorUser.setError(errorMessageService.getBonitaTokenErrorMessage(authRequest.getAttemptCount()));
                return errorUser;
            }

        } catch (Exception e) {
            log.error("Unable to second factor authenticate. User: " + authRequest.getUserName());
            errorUser.setError(errorMessageService.getBonitaTokenErrorMessage(authRequest.getAttemptCount()));
            return errorUser;
        }
    }

    public String checkPhoneNumber(String phoneNumber, BonitaUserContactDetails contactDetailsResponse){
        String recipient  = "";
        if (phoneNumber == null ) {
            log.info("If phone number is not valid try email as email is sent by default");
            recipient = contactDetailsResponse.getEmail();
        }else{
            recipient = phoneNumber;
        }
        return recipient;
    }

    public BonitaCaseResponse requestForgotPasswordEmail(ForgotPasswordRequest forgotPasswordRequest) {
        try {
            String userName = forgotPasswordRequest.getUserName();

            if(null != userName  && userName.length() != 0 || null != userName && !userName.trim().isEmpty()){
               userName = userName.toLowerCase().trim();
            }

            String bonitaUserName = authDetailsDAO.getUserName(userName);
            SessionInfo sessionInfo = bonitaAuthenticateAPIService.loginUser(bonitaAdminUser, bonitaAdminPassword);
            BonitaUserDetails userDetails = bonitaAuthenticateAPIService.findUserByUserName(bonitaUserName, sessionInfo);
            ProcessResponse processResponse = bonitaAuthenticateAPIService.getNewUserProcess(sessionInfo);

            BonitaForgotPasswordRequest requestBody = new BonitaForgotPasswordRequest(bonitaUserName, userName,
                    userDetails.getFirstName(),userDetails.getLastName(), true);
            log.info("Send log record for password change request.");
            LogRecord logRecord = new LogRecord(0,0, "Password change requested by " + forgotPasswordRequest.getUserName() , "Password Change");
            rabbitTemplate.convertAndSend(Commons.LOGGING_EXCHANGE, "logs-user", logRecord);
            rabbitTemplate.convertAndSend(Commons.LOGGING_EXCHANGE, "logs-system", logRecord);
            if(forgotPasswordRequest.getTicketId() != null && !forgotPasswordRequest.getTicketId().isEmpty()){
                messagingService.sendTicketUpdate("Password reset sent." , forgotPasswordRequest.getTicketId());
            }
            return bonitaAuthenticateAPIService.requestForgotPasswordEmail(requestBody, processResponse.getId(), sessionInfo);
        } catch (Exception e) {
            log.error("Unable to start forgot password process." +e);
            return new BonitaCaseResponse("Unable to start forgot password process.");
        }
    }

    public PasswordUrlResponse generatePasswordChangeURL(PasswordUrlRequest passwordUrlRequest) {
        log.warn("PASSWORDCHANGEURL:Attempting authorization check");
        if(!passwordUrlRequest.getMiddlewareAdminKey().equals(middlewareAdminKey)) {
            log.warn("PASSWORDCHANGEURL:Not Authorized to generate password url");
            return new PasswordUrlResponse("Not authorized");
        }

        String secretKey = UUID.randomUUID().toString().replace("-", "");

        try {
            PasswordResetKey passwordResetKey = new PasswordResetKey(secretKey, passwordUrlRequest.getUserName(), OffsetDateTime.now(),false);
            passwordRequestKeyDAO.save(passwordResetKey);
            String url = myDCbloxDomain + "passwordRegistration/" + secretKey;
            return new PasswordUrlResponse(url, null);
        } catch (Exception e) {
            log.warn("PASSWORDCHANGEURL:Unable to generate password URL");
            log.error("Unable to create password url for user: "+passwordUrlRequest.getUserName());
            return new PasswordUrlResponse("Unable to create password url.");
        }
    }

    public UserResponse getAuthenticatedUser(UserIdentificationResponse user, SessionInfo sessionInfo) {
        UserResponse userResponse = new UserResponse();
        BonitaUserDetails userDetailResponse;
        BonitaUserContactDetails contactDetailsResponse;
        try {
            userDetailResponse = bonitaAuthenticateAPIService.getUserDetails(user.getUserId(), sessionInfo);
            log.warn("Got user details from bonita user: "+user.getUserName());
            log.warn(bonitaInfoMessage+user.getUserName());
            contactDetailsResponse = bonitaAuthenticateAPIService.getUserProfessionalDetails(user.getUserId(), sessionInfo);
            log.warn("Got user professional details from bonita for username: "+user.getUserName());
            try{
                log.warn("Trying to get profile for user: "+user.getUserName());
                userDetailResponse.setUserProfile(profileService.retrieveUserProfile(Integer.parseInt(userDetailResponse.getId()), contactDetailsResponse.getEmail()));
            }catch(Exception e){
                log.warn("User Found in bonita with no contact details. User :" +user.getUserName());
            }
            log.warn("Got profile for user: "+user.getUserName());
            /**
             * Setup Message Broker
             */

            try {
                String phoneArea = contactDetailsResponse.getMobileNumber().replaceAll(numberRegex, "").substring(0, 3);
                if ("907".equals(phoneArea)) {
                    contactDetailsResponse.setMobileNumber(null);
                }
            } catch (Exception e) {
                contactDetailsResponse.setMobileNumber(null);
                log.warn("User:"+user.getUserName()+" does not have a mobile phone number for second factor auth.");
            }
            try{
                 userDetailResponse.setLastLogin(bloxopsOrganizationDAO.getUsersLastLogin(contactDetailsResponse.getEmail()));
            }catch(Exception e){
                 log.error("Could not get last login for : "+user.getUserName());
            }
            userDetailResponse.setContactDetails(contactDetailsResponse);
            userDetailResponse.setSharedUser(userBloxopsDAO.getUserProfileByEmail(contactDetailsResponse.getEmail()).isSharedUser());
            userResponse.setUser(userDetailResponse);
            userResponse.setSessionInfo(sessionInfo);
            List<Organization> organizations = permissionService.getOrganizationServices(user);
            if(!organizations.isEmpty()) {
                userResponse.setMemberships(organizations);
            }else{
                log.error("No active companies set for user : " + user.getUserName());
                UserResponse errorUser = new UserResponse();
                errorUser.setError(errorMessageService.getInactiveOrgErrorMessage(1));
                return errorUser;
            }
            userResponse.setEulaUserDetails(eulaService.getEulaUserDetails(Integer.parseInt(userDetailResponse.getId())));
            return userResponse;

        } catch (Exception e) {
            log.error("Unable to authenticate user : " + user.getUserName());
            log.error(e.getMessage());
            UserResponse errorUser = new UserResponse();
            errorUser.setError(errorMessageService.getAuthenicateErrorMessage(1));
            return errorUser;
        }
    }

    public GetSessionResponse getSession(String sessionToken , String sessionId) {
        try{
        if(sessionToken != null && sessionId != null){
            return new GetSessionResponse(true, null);
        }else{
            return new GetSessionResponse(false, null);
        }}catch(Exception e) {
            return new GetSessionResponse(false, null);
        }

    }

}
