package com.openrangelabs.services.authenticate

import com.openrangelabs.services.authenticate.authy.AuthyRestAPIService
import com.openrangelabs.services.authenticate.authy.dao.SecondFactorLookupDAO
import com.openrangelabs.services.authenticate.authy.model.AuthyStatusResponse
import com.openrangelabs.services.authenticate.authy.model.AuthyUserResponse
import com.openrangelabs.services.authenticate.authy.model.SecondFactorIdentifier
import com.openrangelabs.services.authenticate.bloxops.dao.PasswordRequestKeyDAO
import com.openrangelabs.services.authenticate.bloxops.dao.SessionDAO
import com.openrangelabs.services.authenticate.bonita.BonitaAuthenticateAPIService
import com.openrangelabs.services.authenticate.bonita.dao.AuthenticateDetailsDAO
import com.openrangelabs.services.authenticate.gauthify.model.GAuthifyOneTimeCodeResponseData
import com.openrangelabs.services.authenticate.gauthify.model.GAuthifyUserResponse
import com.openrangelabs.services.authenticate.gauthify.model.GauthifyOneTimeCodeResponse
import com.openrangelabs.services.authenticate.model.LoginRequest
import com.openrangelabs.services.authenticate.model.NewUserPasswordChangeRequest
import com.openrangelabs.services.authenticate.model.PasswordResetKey
import com.openrangelabs.services.authenticate.model.SecondFactorAuthRequest
import com.openrangelabs.services.authenticate.model.SecondFactorSendAuthCodeRequest
import com.openrangelabs.services.authenticate.model.SessionInfo
import com.openrangelabs.services.authenticate.permission.PermissionService
import com.openrangelabs.services.message.ErrorMessageService
import com.openrangelabs.services.user.bonita.BonitaUserAPIService
import com.openrangelabs.services.user.bonita.model.BonitaGroup
import com.openrangelabs.services.user.bonita.model.BonitaPasswordChangeResponse
import com.openrangelabs.services.user.bonita.model.BonitaUserContactDetails
import com.openrangelabs.services.user.bonita.model.BonitaUserCustomDetail
import com.openrangelabs.services.user.bonita.model.BonitaUserCustomDetailDefinition
import com.openrangelabs.services.user.bonita.model.BonitaUserDetails
import com.openrangelabs.services.user.bonita.model.BonitaUserMembership
import com.openrangelabs.services.user.model.PasswordUrlRequest
import com.openrangelabs.services.user.model.UserIdentificationResponse
import com.openrangelabs.services.user.model.UserResponse
import com.openrangelabs.services.user.profile.ProfileService
import com.openrangelabs.services.user.profile.dao.UserBloxopsDAO
import com.openrangelabs.services.user.profile.model.UserProfile
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification

import java.time.OffsetDateTime

class AuthenticationServiceSpec extends Specification {

    AuthyRestAPIService authyRestAPIService
    ProfileService profileService
    BonitaAuthenticateAPIService bonitaAuthenticateAPIService
    BonitaUserAPIService bonitaUserAPIService
    SessionDAO sessionDAO
    ErrorMessageService errorMessageService
    AuthenticateDetailsDAO authDetailsDAO
    AuthenticateService authenticateService
    PasswordRequestKeyDAO passwordRequestKeyDAO
    PermissionService permissionService
    SecondFactorLookupDAO secondFactorLookupDAO
    UserBloxopsDAO userBloxopsDAO

    def setup() {
        authyRestAPIService = Mock(AuthyRestAPIService)
        profileService = Mock(ProfileService)
        bonitaAuthenticateAPIService = Mock(BonitaAuthenticateAPIService)
        bonitaUserAPIService = Mock(BonitaUserAPIService)
        sessionDAO = Mock(SessionDAO)
        errorMessageService = Mock(ErrorMessageService)
        authDetailsDAO = Mock(AuthenticateDetailsDAO)
        passwordRequestKeyDAO = Mock(PasswordRequestKeyDAO)
        permissionService = Mock(PermissionService)
        secondFactorLookupDAO = Mock(SecondFactorLookupDAO)
        userBloxopsDAO = Mock(UserBloxopsDAO)

        authenticateService = new AuthenticateService(authyRestAPIService, bonitaAuthenticateAPIService, profileService,
                sessionDAO, errorMessageService, authDetailsDAO, bonitaUserAPIService, passwordRequestKeyDAO, permissionService, secondFactorLookupDAO, userBloxopsDAO)

        authenticateService.middlewareAdminKey = "1234"
    }

    def 'Login user first factor successfully with no second factor'() {
        given:'A Login Request with correct user details'
        LoginRequest loginRequest = new LoginRequest("standford.stanlee@openrangelabs.com", "Wowwowow")
        String userName = "standford.stanlee"
        SessionInfo sessionInfo = new SessionInfo("Bonita-Token-Value", "SessionId")
        UserIdentificationResponse userIdResponse = new UserIdentificationResponse(userId:"123",  userName:userName)
        BonitaUserCustomDetail[] userCustomDetails = [
                new BonitaUserCustomDetail(userId:userIdResponse.userId, value:"22222", definitionId: new BonitaUserCustomDetailDefinition(name:"NOTGUID")),
                new BonitaUserCustomDetail(userId:userIdResponse.userId, value:"11111", definitionId: new BonitaUserCustomDetailDefinition(name:"GUID"))]

        UserProfile profile = new UserProfile()
        BonitaUserDetails userDetails = new BonitaUserDetails(id:"123")
        BonitaUserContactDetails professionalContractDetails = new BonitaUserContactDetails(email:"standford.stanlee@openrangelabs.com", mobileNumber: "(563) 505-3543" )
        AuthyStatusResponse authyStatusResponse = new AuthyStatusResponse(success: true)

        when:'authenticateUserCredentials service method call'
        UserResponse response = authenticateService.authenticateUserCredentials(loginRequest)

        then:'Expect service calls and partial user object as result'
        1 * authyRestAPIService.appStatus() >> authyStatusResponse
        1 * authDetailsDAO.getUserName(loginRequest.userName) >> userName
        1 * bonitaAuthenticateAPIService.loginUser(userName, loginRequest.password) >> sessionInfo
        1 * sessionDAO.setSessionToken(loginRequest.getUserName(), sessionInfo)
        1 * bonitaAuthenticateAPIService.getUserIdentification(sessionInfo) >> userIdResponse
        1 * bonitaAuthenticateAPIService.getUserDetails(_, sessionInfo) >> userDetails
        1 * bonitaAuthenticateAPIService.getUserProfessionalDetails(userIdResponse.getUserId(), sessionInfo) >> professionalContractDetails
        1 * profileService.retrieveUserProfile(123, "standford.stanlee@openrangelabs.com")
        1 * permissionService.getOrganizationServices(userIdResponse, sessionInfo)

        assert response.error == null
        assert response.user == userDetails
    }

    def 'Login user after creating required user in authy with text for second factor'() {
        given:'A Login Request with correct user details'
        LoginRequest loginRequest = new LoginRequest("standford.stanlee@openrangelabs.com", "Wowwowow")
        String userName = "standford.stanlee"
        SessionInfo sessionInfo = new SessionInfo("Bonita-Token-Value", "SessionId")
        UserIdentificationResponse userIdResponse = new UserIdentificationResponse(userId:"123",  userName:userName)
        BonitaUserCustomDetail[] userCustomDetails = [
                new BonitaUserCustomDetail(userId:userIdResponse.userId, value:"22222", definitionId: new BonitaUserCustomDetailDefinition(name:"NOTGUID")),
                new BonitaUserCustomDetail(userId:userIdResponse.userId, value:"11111", definitionId: new BonitaUserCustomDetailDefinition(name:"GUID"))]

        UserProfile profile = new UserProfile()
        BonitaUserDetails userDetails = new BonitaUserDetails(id:"123")
        BonitaUserContactDetails professionalContractDetails = new BonitaUserContactDetails(email:"stan@openrangelabs.com", mobileNumber: "(563) 505-3543"  )

        when:'authenticateUserCredentials service method call'
        UserResponse response = authenticateService.authenticateUserCredentialsSecondFactor(loginRequest)

        then:'Expect service calls including gAuthifyAPIService.createUser and partial user object as result'
        1 * authDetailsDAO.getUserName(loginRequest.userName) >> userName
        1 * bonitaAuthenticateAPIService.loginUser(userName, loginRequest.password) >> sessionInfo
        1 * bonitaAuthenticateAPIService.getUserIdentification(sessionInfo) >> userIdResponse
        1 * bonitaAuthenticateAPIService.getCustomUserDetails(userIdResponse.userId, sessionInfo) >> userCustomDetails
        1 * bonitaAuthenticateAPIService.extractGUID(userCustomDetails) >> userCustomDetails[1].value
        1 * sessionDAO.setSessionToken(loginRequest.userName, sessionInfo) >> 1
        1 * bonitaAuthenticateAPIService.getUserDetails(userIdResponse.getUserId(), sessionInfo) >> userDetails
        1 * bonitaAuthenticateAPIService.getUserProfessionalDetails(_, _) >> professionalContractDetails

        assert response.error == null
        assert response.user == userDetails
    }

    def 'Login user failed to get user profile defaulting to email second factor'() {
        given:'A Login Request with correct user details'
        LoginRequest loginRequest = new LoginRequest("standford.stanlee@openrangelabs.com", "Wowwowow")
        String userName = "standford.stanlee"
        SessionInfo sessionInfo = new SessionInfo("Bonita-Token-Value", "SessionId")
        UserIdentificationResponse userIdResponse = new UserIdentificationResponse(userId:"123",  userName:userName)
        BonitaUserCustomDetail[] userCustomDetails = [
                new BonitaUserCustomDetail(userId:userIdResponse.userId, value:"22222", definitionId: new BonitaUserCustomDetailDefinition(name:"NOTGUID")),
                new BonitaUserCustomDetail(userId:userIdResponse.userId, value:"11111", definitionId: new BonitaUserCustomDetailDefinition(name:"GUID"))]

        BonitaUserDetails userDetails = new BonitaUserDetails(id:"123")
        BonitaUserContactDetails professionalContractDetails = new BonitaUserContactDetails(email:"stan@openrangelabs.com", mobileNumber: "(563) 505-3543")

        when:'authenticateUserCredentials service method call'
        UserResponse response = authenticateService.authenticateUserCredentialsSecondFactor(loginRequest)

        then:'Expect service calls and partial user object as result'
        1 * authDetailsDAO.getUserName(loginRequest.userName) >> userName
        1 * bonitaAuthenticateAPIService.loginUser(userName, loginRequest.password) >> sessionInfo
        1 * bonitaAuthenticateAPIService.getUserIdentification(sessionInfo) >> userIdResponse
        1 * bonitaAuthenticateAPIService.getCustomUserDetails(userIdResponse.userId, sessionInfo) >> userCustomDetails
        1 * bonitaAuthenticateAPIService.extractGUID(userCustomDetails) >> userCustomDetails[1].value
        1 * sessionDAO.setSessionToken(loginRequest.userName, sessionInfo) >> 1
        1 * bonitaAuthenticateAPIService.getUserDetails(userIdResponse.getUserId(), sessionInfo) >> userDetails
        1 * bonitaAuthenticateAPIService.getUserProfessionalDetails(_, _) >> professionalContractDetails

        assert response.user == userDetails
        assert response.error == null
    }

    def 'Login user failure authenticating'() {
        given:'A Login Request with incorrect user details'
        LoginRequest loginRequest = new LoginRequest("standford.stanlee@openrangelabs.com", "Wowwowow")
        String userName = "standford.stanlee"
        SessionInfo sessionInfo = new SessionInfo("Bonita-Token-Value", "SessionId")
        UserIdentificationResponse userIdResponse = new UserIdentificationResponse(userId:"123",  userName:userName)
        BonitaUserCustomDetail[] userCustomDetails = [
                new BonitaUserCustomDetail(userId:userIdResponse.userId, value:"22222", definitionId: new BonitaUserCustomDetailDefinition(name:"NOTGUID")),
                new BonitaUserCustomDetail(userId:userIdResponse.userId, value:"11111", definitionId: new BonitaUserCustomDetailDefinition(name:"GUID"))]

        UserProfile profile = new UserProfile()
        BonitaUserDetails userDetails = new BonitaUserDetails(id:"123")
        BonitaUserContactDetails professionalContractDetails = new BonitaUserContactDetails(email:"stan@openrangelabs.com" )

        when:'authenticateUserCredentials service method call'
        UserResponse response = authenticateService.authenticateUserCredentials(loginRequest)

        then:'Expect service calls and empty result with error string'
        1 * authDetailsDAO.getUserName(loginRequest.userName) >> userName
        1 * bonitaAuthenticateAPIService.loginUser(userName, loginRequest.password) >> sessionInfo
        1 * sessionDAO.setSessionToken(loginRequest.getUserName(), sessionInfo)
        1 * bonitaAuthenticateAPIService.getUserIdentification(sessionInfo) >> userIdResponse
        1 * bonitaAuthenticateAPIService.getUserDetails(_, sessionInfo) >> userDetails
        1 * bonitaAuthenticateAPIService.getUserProfessionalDetails(userIdResponse.getUserId(), sessionInfo) >> { throw new Exception() }
        1 * errorMessageService.getAuthenicateErrorMessage(_) >> "Error Happened"

        assert response.user == null
        assert response.error == "Error Happened"
    }

    def 'Authenticate second factor successfully'() {
        given:"A correct/current second factor auth code"
        SecondFactorAuthRequest authRequest = new SecondFactorAuthRequest(oneTimeCode: "21254")
        SessionInfo sessionInfo = new SessionInfo()
        UserIdentificationResponse userIdResponse = new UserIdentificationResponse(userId:"333" )
        BonitaUserCustomDetail[] userCustomDetails = [
                new BonitaUserCustomDetail(userId:userIdResponse.userId, value:"22222", definitionId: new BonitaUserCustomDetailDefinition(name:"NOTGUID")),
                new BonitaUserCustomDetail(userId:userIdResponse.userId, value:"11111", definitionId: new BonitaUserCustomDetailDefinition(name:"GUID"))]
        BonitaUserDetails userDetails = new BonitaUserDetails(id:"123")
        BonitaUserContactDetails professionalContractDetails = new BonitaUserContactDetails(email:"stan@openrangelabs.com", mobileNumber: "(563) 505-3543"  )
        UserProfile profile = new UserProfile()
        SecondFactorIdentifier secondFactorIdentifier = new SecondFactorIdentifier(uniqueIdentifier: 333)
        AuthyStatusResponse authyStatusResponse = new AuthyStatusResponse(success: true)

        when:'authenticateUserCredentials service method call'
        UserResponse response = authenticateService.authenticateUserSecondFactor(authRequest)

        then:'Expect service calls and full user object as result'
        1 * sessionDAO.getSessionToken(authRequest.getUserName()) >> sessionInfo
        1 * bonitaAuthenticateAPIService.getUserIdentification(sessionInfo) >> userIdResponse
        1 * secondFactorLookupDAO.getSecondFactorIdentifier(333,"Authy") >> secondFactorIdentifier
        1 * authyRestAPIService.checkCode(_,_) >> authyStatusResponse
        1 * bonitaAuthenticateAPIService.getUserDetails(_, _) >> userDetails
        1 * bonitaAuthenticateAPIService.getUserProfessionalDetails(userIdResponse.getUserId(), sessionInfo) >> professionalContractDetails
        1 * profileService.retrieveUserProfile(_,_) >> profile
        1 * permissionService.getOrganizationServices(userIdResponse, sessionInfo) >> new ArrayList<>()

        assert response.user == userDetails
        assert response.error == null
        assert response.sessionInfo == sessionInfo
        assert response.user.userProfile == profile
    }

    def 'Authenticate second factor unsuccessfully'() {
        given:"An incorrect second factor auth code"
        SecondFactorAuthRequest authRequest = new SecondFactorAuthRequest(oneTimeCode: "21254")
        SessionInfo sessionInfo = new SessionInfo()
        UserIdentificationResponse userIdResponse = new UserIdentificationResponse(userId:"333" )
        BonitaUserCustomDetail[] userCustomDetails = [
                new BonitaUserCustomDetail(userId:userIdResponse.userId, value:"22222", definitionId: new BonitaUserCustomDetailDefinition(name:"NOTGUID")),
                new BonitaUserCustomDetail(userId:userIdResponse.userId, value:"11111", definitionId: new BonitaUserCustomDetailDefinition(name:"GUID"))]
        BonitaUserDetails userDetails = new BonitaUserDetails(id:"123")
        BonitaUserContactDetails professionalContractDetails = new BonitaUserContactDetails(email:"stan@openrangelabs.com" )
        UserProfile profile = new UserProfile()
        List<BonitaUserMembership> memberships = [new BonitaUserMembership(group_id: 111)]
        BonitaGroup group = new BonitaGroup(id: 111)
        List<BonitaGroup> groups = new ArrayList<>()
        groups.add(group)
        SecondFactorIdentifier secondFactorIdentifier = new SecondFactorIdentifier(uniqueIdentifier: 333)

        when:'authenticateUserCredentials service method call'
        UserResponse response = authenticateService.authenticateUserSecondFactor(authRequest)

        then:'Expect service calls until authenticated returns false and error is returned'
        1 * sessionDAO.getSessionToken(authRequest.getUserName()) >> sessionInfo
        1 * bonitaAuthenticateAPIService.getUserIdentification(sessionInfo) >> userIdResponse
        1 * secondFactorLookupDAO.getSecondFactorIdentifier(333,"Authy") >> secondFactorIdentifier
        1 * authyRestAPIService.checkCode(_,_) >> new GauthifyOneTimeCodeResponse(data: new GAuthifyOneTimeCodeResponseData(authenticated:false))
        1 * errorMessageService.getPasswordErrorMessage(_) >> "Error Happened"

        assert response.user == null
        assert response.error == "Error Happened"
        assert response.sessionInfo == null
    }

    def 'Authenticate second factor error'() {
        given:"A second factor auth code"
        SecondFactorAuthRequest authRequest = new SecondFactorAuthRequest(oneTimeCode: "21254")
        SessionInfo sessionInfo = new SessionInfo()
        UserIdentificationResponse userIdResponse = new UserIdentificationResponse(userId:"333" )
        BonitaUserCustomDetail[] userCustomDetails = [
                new BonitaUserCustomDetail(userId:userIdResponse.userId, value:"22222", definitionId: new BonitaUserCustomDetailDefinition(name:"NOTGUID")),
                new BonitaUserCustomDetail(userId:userIdResponse.userId, value:"11111", definitionId: new BonitaUserCustomDetailDefinition(name:"GUID"))]
        BonitaUserDetails userDetails = new BonitaUserDetails(id:"123")
        BonitaUserContactDetails professionalContractDetails = new BonitaUserContactDetails(email:"stan@openrangelabs.com" )
        UserProfile profile = new UserProfile()
        List<BonitaUserMembership> memberships = [new BonitaUserMembership(group_id: 111)]
        SecondFactorIdentifier secondFactorIdentifier = new SecondFactorIdentifier(uniqueIdentifier: 333)
        BonitaGroup group = new BonitaGroup(id: 111)
        List<BonitaGroup> groups = new ArrayList<>()
        groups.add(group)

        when:'authenticateUserCredentials service method call'
        UserResponse response = authenticateService.authenticateUserSecondFactor(authRequest)

        then:'Expect service calls until exception is thrown and error is returned'
        1 * sessionDAO.getSessionToken(authRequest.getUserName()) >> sessionInfo
        1 * bonitaAuthenticateAPIService.getUserIdentification(sessionInfo) >> userIdResponse
        1 * secondFactorLookupDAO.getSecondFactorIdentifier(333,"Authy") >> secondFactorIdentifier
        1 * authyRestAPIService.checkCode(_,_) >> { throw new Exception() }
        1 * errorMessageService.getPasswordErrorMessage(_) >> "Error Happened"

        assert response.user == null
        assert response.error == "Error Happened"
        assert response.sessionInfo == null
    }

    def 'Get Authenticate User Successfully'() {
        given:"A logged in user"
        UserIdentificationResponse user = new UserIdentificationResponse()
        SessionInfo sessionInfo = new SessionInfo("Bonita-Token-Value", "SessionId")
        BonitaUserDetails userDetails = new BonitaUserDetails(id:"123")
        BonitaUserContactDetails professionalContractDetails = new BonitaUserContactDetails(email:"stan@openrangelabs.com", mobileNumber: "(563) 505-3543"  )
        UserProfile profile = new UserProfile()
        List<BonitaUserMembership> memberships = [new BonitaUserMembership(group_id: 111)]
        ResponseEntity<List<BonitaUserMembership>> memresponse= new ResponseEntity<>(memberships,HttpStatus.OK)
        BonitaGroup group = new BonitaGroup(id: 111)
        List<BonitaGroup> groups = new ArrayList<>()
        groups.add(group)

        when:'getAuthenticatedUser service method call'
        UserResponse response = authenticateService.getAuthenticatedUser(user, sessionInfo)

        then:'Expect service calls and full user object response'
        1 * bonitaAuthenticateAPIService.getUserDetails(user.getUserId(), sessionInfo) >> userDetails
        1 * bonitaAuthenticateAPIService.getUserProfessionalDetails(user.getUserId(), sessionInfo) >> professionalContractDetails
        1 * profileService.retrieveUserProfile(_,_) >> profile

        assert response.error == null
        assert response.user == userDetails
        assert response.user.userProfile == profile
        assert response.user.contactDetails == professionalContractDetails
    }

    def 'Get Authenticate User Failure'() {
        given:"A second factor auth code"
        UserIdentificationResponse user = new UserIdentificationResponse(userName: "stan.stanlee")
        SessionInfo sessionInfo = new SessionInfo("Bonita-Token-Value", "SessionId")
        BonitaUserDetails userDetails = new BonitaUserDetails(id:"123")
        BonitaUserContactDetails professionalContractDetails = new BonitaUserContactDetails(email:"stan@openrangelabs.com" )
        UserProfile profile = new UserProfile()
        List<BonitaUserMembership> memberships = [new BonitaUserMembership(group_id: 111)]

        BonitaGroup group = new BonitaGroup(id: 111)
        List<BonitaGroup> groups = new ArrayList<>()
        groups.add(group)

        when:'getAuthenticatedUser service method call'
        UserResponse response = authenticateService.getAuthenticatedUser(user, sessionInfo)

        then:'Expect service calls and response error'
        1 * bonitaAuthenticateAPIService.getUserDetails(_,_) >> { throw new Exception() }
        1 * errorMessageService.getAuthenicateErrorMessage(_) >> "Error Happened"

        assert response.error == "Error Happened"
        assert response.user == null
    }

    def 'Generate Password Change URL Successfully'() {
        given:'A password Change Request'
        PasswordUrlRequest passwordUrlRequest = new PasswordUrlRequest(middlewareAdminKey: authenticateService.middlewareAdminKey)

        when:
        def response = authenticateService.generatePasswordChangeURL(passwordUrlRequest)

        then:'Returns a proper URL'
        1 * passwordRequestKeyDAO.save(_)

        assert response.error == null
        assert response.url.contains("passwordRegistration/") == true
    }

    def 'Generate Password Change URL Failure'() {
        given:'A password Change Request'
        PasswordUrlRequest passwordUrlRequest = new PasswordUrlRequest(middlewareAdminKey: authenticateService.middlewareAdminKey)
        Exception exception = new Exception("Error")

        when:
        def response = authenticateService.generatePasswordChangeURL(passwordUrlRequest)

        then:'Returns an Error'
        1 * passwordRequestKeyDAO.save(_) >> {throw new Exception()}

        assert response.error != null
        assert response.url == null
    }

    def 'Change Password New User Successful'(){
        given:'A Change Password Request'
        NewUserPasswordChangeRequest changeRequest = new NewUserPasswordChangeRequest()
        PasswordResetKey requestKey = new PasswordResetKey()
        requestKey.setTimestamp(OffsetDateTime.now())

        SessionInfo sessionInfo = new SessionInfo()
        BonitaPasswordChangeResponse pwResponse = new BonitaPasswordChangeResponse()

        when:
        def response = authenticateService.changePasswordNewUser(changeRequest)

        then:
        1 * passwordRequestKeyDAO.findById(changeRequest.getKey()) >> requestKey
        1 * bonitaAuthenticateAPIService.loginUser(_, _) >> sessionInfo
        1 * bonitaUserAPIService.updatePassword(_, sessionInfo) >> pwResponse

        assert response.error == null
        assert response.successful == true
    }

    def 'Change Password New User Failure from Exception'() {
        given:'A Change Password Request'
        NewUserPasswordChangeRequest changeRequest = new NewUserPasswordChangeRequest()
        PasswordResetKey requestKey = new PasswordResetKey()
        requestKey.setExpired(false)
        requestKey.setTimestamp(OffsetDateTime.now())
        SessionInfo sessionInfo = new SessionInfo()

        when:
        def response = authenticateService.changePasswordNewUser(changeRequest)

        then:
        1 * passwordRequestKeyDAO.findById(changeRequest.getKey()) >> requestKey
        1 * bonitaAuthenticateAPIService.loginUser(_, _) >> sessionInfo
        1 * bonitaUserAPIService.updatePassword(_, sessionInfo) >> {throw new Exception()}

        assert response.error != null
        assert response.successful == false
    }


    def 'Change Password New User Failure from Expired Key'() {
        given:'A Change Password Request with an expired key'
        NewUserPasswordChangeRequest changeRequest = new NewUserPasswordChangeRequest()
        PasswordResetKey requestKey = new PasswordResetKey()
        requestKey.setExpired(true)
        requestKey.setTimestamp(OffsetDateTime.now())
        Optional<PasswordResetKey> optional = Optional.of((PasswordResetKey) requestKey)

        SessionInfo sessionInfo = new SessionInfo()
        BonitaPasswordChangeResponse pwResponse = new BonitaPasswordChangeResponse()

        when:
        def response = authenticateService.changePasswordNewUser(changeRequest)

        then:
        1 * passwordRequestKeyDAO.findById(changeRequest.getKey()) >> optional

        assert response.error != null
        assert response.successful == false
    }

    def 'Change Password New User Failure from outside of date range'() {
        given:'A Change Password Request with a timestamp that is 8 days old'
        NewUserPasswordChangeRequest changeRequest = new NewUserPasswordChangeRequest()
        PasswordResetKey requestKey = new PasswordResetKey()
        requestKey.setExpired(false)
        requestKey.setTimestamp(OffsetDateTime.now().minusDays(8))
        Optional<PasswordResetKey> optional = Optional.of((PasswordResetKey) requestKey)

        SessionInfo sessionInfo = new SessionInfo()
        BonitaPasswordChangeResponse pwResponse = new BonitaPasswordChangeResponse()

        when:
        def response = authenticateService.changePasswordNewUser(changeRequest)

        then:
        1 * passwordRequestKeyDAO.findById(changeRequest.getKey()) >> optional

        assert response.error != null
        assert response.successful == false
    }

    def"Send second factor code successfully with email"() {
        given:"a request to send an auth code with EMAIL or SMS specified"
        SecondFactorSendAuthCodeRequest authCodeRequest = new SecondFactorSendAuthCodeRequest(userName: "bob.cob", authType: "EMAIL")
        SessionInfo sessionInfo = new SessionInfo(sessionToken: "123", sessionId: "324324")
        UserIdentificationResponse userIdResponse = new UserIdentificationResponse(userId:"123",  userName:"bryce.stock")
        BonitaUserDetails userDetails = new BonitaUserDetails(id:"123")
        BonitaUserCustomDetail[] userCustomDetails = [
                new BonitaUserCustomDetail(userId:userIdResponse.userId, value:"22222", definitionId: new BonitaUserCustomDetailDefinition(name:"NOTGUID")),
                new BonitaUserCustomDetail(userId:userIdResponse.userId, value:"11111", definitionId: new BonitaUserCustomDetailDefinition(name:"GUID"))]
        BonitaUserContactDetails professionalContractDetails = new BonitaUserContactDetails(email:"stan@openrangelabs.com" )
        AuthyUserResponse authifyUserResponse = new AuthyUserResponse()
        SecondFactorIdentifier secondFactorIdentifier = new SecondFactorIdentifier(uniqueIdentifier: "4444")
        AuthyStatusResponse authyStatusResponse = new AuthyStatusResponse(success: true)

        when:
        def response = authenticateService.sendSecondFactorOneTimeCode(authCodeRequest)

        then:
        1 * sessionDAO.getSessionToken(authCodeRequest.getUserName()) >> sessionInfo
        1 * bonitaAuthenticateAPIService.getUserIdentification(sessionInfo) >> userIdResponse
        1 * bonitaAuthenticateAPIService.getUserDetails(userIdResponse.getUserId(), sessionInfo) >> userDetails
        1 * bonitaAuthenticateAPIService.extractGUID(_) >> userCustomDetails[1].value
        1 * bonitaAuthenticateAPIService.getUserProfessionalDetails(_, _) >> professionalContractDetails
        1 * secondFactorLookupDAO.getSecondFactorIdentifier(123,"Authy") >> secondFactorIdentifier
        1 * authyRestAPIService.getAuthyUser(_) >> authifyUserResponse
        1 * authyRestAPIService.sendEmail(_) >> authyStatusResponse

        assert response.error == null
        assert response.user == userDetails
    }

    def"Send second factor code successfully created user with email"() {
        given:"a request to send an auth code with EMAIL or SMS specified"
        SecondFactorSendAuthCodeRequest authCodeRequest = new SecondFactorSendAuthCodeRequest(userName: "bob.cob", authType: "EMAIL")
        SessionInfo sessionInfo = new SessionInfo(sessionToken: "123", sessionId: "324324")
        UserIdentificationResponse userIdResponse = new UserIdentificationResponse(userId:"123",  userName:"bryce.stock")
        BonitaUserDetails userDetails = new BonitaUserDetails(id:"123")
        BonitaUserCustomDetail[] userCustomDetails = [
                new BonitaUserCustomDetail(userId:userIdResponse.userId, value:"22222", definitionId: new BonitaUserCustomDetailDefinition(name:"NOTGUID")),
                new BonitaUserCustomDetail(userId:userIdResponse.userId, value:"11111", definitionId: new BonitaUserCustomDetailDefinition(name:"GUID"))]
        BonitaUserContactDetails professionalContractDetails = new BonitaUserContactDetails(email:"stan@openrangelabs.com" )
        AuthyUserResponse authifyUserResponse = new AuthyUserResponse()
        SecondFactorIdentifier secondFactorIdentifier = new SecondFactorIdentifier(uniqueIdentifier: "4444")
        AuthyStatusResponse authyStatusResponse = new AuthyStatusResponse(success: true)

        when:
        def response = authenticateService.sendSecondFactorOneTimeCode(authCodeRequest)

        then:
        1 * sessionDAO.getSessionToken(authCodeRequest.getUserName()) >> sessionInfo
        1 * bonitaAuthenticateAPIService.getUserIdentification(sessionInfo) >> userIdResponse
        1 * bonitaAuthenticateAPIService.getUserDetails(userIdResponse.getUserId(), sessionInfo) >> userDetails
        1 * bonitaAuthenticateAPIService.extractGUID(_) >> userCustomDetails[1].value
        1 * bonitaAuthenticateAPIService.getUserProfessionalDetails(_, _) >> professionalContractDetails
        1 * secondFactorLookupDAO.getSecondFactorIdentifier(123,"Authy") >> secondFactorIdentifier
        1 * authyRestAPIService.getAuthyUser(_) >> authifyUserResponse
        1 * authyRestAPIService.sendEmail(_) >> authyStatusResponse

        assert response.error == null
        assert response.user == userDetails
    }

    def"Send second factor code successfully with sms"() {
        given:"a request to send an auth code with EMAIL or SMS specified"
        SecondFactorSendAuthCodeRequest authCodeRequest = new SecondFactorSendAuthCodeRequest(userName: "bob.cob", authType: "SMS")
        SessionInfo sessionInfo = new SessionInfo(sessionToken: "123", sessionId: "324324")
        UserIdentificationResponse userIdResponse = new UserIdentificationResponse(userId:"123",  userName:"bryce.stock")
        BonitaUserDetails userDetails = new BonitaUserDetails(id:"123")
        BonitaUserCustomDetail[] userCustomDetails = [
                new BonitaUserCustomDetail(userId:userIdResponse.userId, value:"22222", definitionId: new BonitaUserCustomDetailDefinition(name:"NOTGUID")),
                new BonitaUserCustomDetail(userId:userIdResponse.userId, value:"11111", definitionId: new BonitaUserCustomDetailDefinition(name:"GUID"))]
        BonitaUserContactDetails professionalContractDetails = new BonitaUserContactDetails(email:"stan@openrangelabs.com", mobileNumber: "122-222-2222" )
        AuthyUserResponse authifyUserResponse = new AuthyUserResponse()
        SecondFactorIdentifier secondFactorIdentifier = new SecondFactorIdentifier(uniqueIdentifier: "4444")
        AuthyStatusResponse authyStatusResponse = new AuthyStatusResponse(success: true)

        when:
        def response = authenticateService.sendSecondFactorOneTimeCode(authCodeRequest)

        then:
        1 * sessionDAO.getSessionToken(authCodeRequest.getUserName()) >> sessionInfo
        1 * bonitaAuthenticateAPIService.getUserIdentification(sessionInfo) >> userIdResponse
        1 * bonitaAuthenticateAPIService.getUserDetails(userIdResponse.getUserId(), sessionInfo) >> userDetails
        1 * bonitaAuthenticateAPIService.extractGUID(_) >> userCustomDetails[1].value
        1 * bonitaAuthenticateAPIService.getUserProfessionalDetails(_, _) >> professionalContractDetails
        1 * secondFactorLookupDAO.getSecondFactorIdentifier(123,"Authy") >> secondFactorIdentifier
        1 * authyRestAPIService.getAuthyUser(_) >> authifyUserResponse
        1 * authyRestAPIService.sendEmail(_) >> authyStatusResponse

        assert response.error == null
        assert response.user == userDetails
    }

    def"Send second factor failed for sms because mobile phone isn't found"() {
        given:"a request to send an auth code with EMAIL or SMS specified"
        SecondFactorSendAuthCodeRequest authCodeRequest = new SecondFactorSendAuthCodeRequest(userName: "bob.cob", authType: "SMS")
        SessionInfo sessionInfo = new SessionInfo(sessionToken: "123", sessionId: "324324")
        UserIdentificationResponse userIdResponse = new UserIdentificationResponse(userId:"123",  userName:"bryce.stock")
        BonitaUserDetails userDetails = new BonitaUserDetails(id:"123")
        BonitaUserCustomDetail[] userCustomDetails = [
                new BonitaUserCustomDetail(userId:userIdResponse.userId, value:"22222", definitionId: new BonitaUserCustomDetailDefinition(name:"NOTGUID")),
                new BonitaUserCustomDetail(userId:userIdResponse.userId, value:"11111", definitionId: new BonitaUserCustomDetailDefinition(name:"GUID"))]
        BonitaUserContactDetails professionalContractDetails = new BonitaUserContactDetails(email:"stan@openrangelabs.com")
        AuthyUserResponse authifyUserResponse = new AuthyUserResponse()
        SecondFactorIdentifier secondFactorIdentifier = new SecondFactorIdentifier(uniqueIdentifier: "4444")
        AuthyStatusResponse authyStatusResponse = new AuthyStatusResponse(success: true)

        when:
        def response = authenticateService.sendSecondFactorOneTimeCode(authCodeRequest)

        then:
        1 * sessionDAO.getSessionToken(authCodeRequest.getUserName()) >> sessionInfo
        1 * bonitaAuthenticateAPIService.getUserIdentification(sessionInfo) >> userIdResponse
        1 * bonitaAuthenticateAPIService.getUserDetails(userIdResponse.getUserId(), sessionInfo) >> userDetails
        1 * bonitaAuthenticateAPIService.extractGUID(_) >> userCustomDetails[1].value
        1 * bonitaAuthenticateAPIService.getUserProfessionalDetails(_, _) >> professionalContractDetails
        1 * errorMessageService.getAuthenicateErrorMessage(_) >> "error"

        assert response.error == "error"
        assert response.user == null
    }

    def"Send second factor fail to get session token"() {
        given:"a request to send an auth code with EMAIL or SMS specified"
        SecondFactorSendAuthCodeRequest authCodeRequest = new SecondFactorSendAuthCodeRequest(userName: "bob.cob", authType: "SMS")
        SessionInfo sessionInfo = new SessionInfo(sessionToken: "123", sessionId: "324324")
        UserIdentificationResponse userIdResponse = new UserIdentificationResponse(userId:"123",  userName:"bryce.stock")
        BonitaUserDetails userDetails = new BonitaUserDetails(id:"123")
        BonitaUserCustomDetail[] userCustomDetails = [
                new BonitaUserCustomDetail(userId:userIdResponse.userId, value:"22222", definitionId: new BonitaUserCustomDetailDefinition(name:"NOTGUID")),
                new BonitaUserCustomDetail(userId:userIdResponse.userId, value:"11111", definitionId: new BonitaUserCustomDetailDefinition(name:"GUID"))]
        BonitaUserContactDetails professionalContractDetails = new BonitaUserContactDetails(email:"stan@openrangelabs.com")
        GAuthifyUserResponse gAuthifyUserResponse = new GAuthifyUserResponse()

        when:
        def response = authenticateService.sendSecondFactorOneTimeCode(authCodeRequest)

        then:
        1 * sessionDAO.getSessionToken(authCodeRequest.getUserName()) >> {throw new Exception()}
        1 * errorMessageService.getAuthenicateErrorMessage(_) >> "error"

        assert response.error == "error"
        assert response.user == null
    }
}
