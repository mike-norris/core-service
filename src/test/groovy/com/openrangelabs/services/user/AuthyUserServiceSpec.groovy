package com.openrangelabs.services.user

import com.openrangelabs.services.authenticate.AuthenticateService
import com.openrangelabs.services.authenticate.bonita.BonitaAuthenticateAPIService

import com.openrangelabs.services.authenticate.model.SessionInfo
import com.openrangelabs.services.billing.fusebill.FusebillAPIService
import com.openrangelabs.services.billing.fusebill.model.FusebillCustomer
import com.openrangelabs.services.message.ErrorMessageService
import com.openrangelabs.services.organization.bonita.BonitaOrganizationAPIService

import com.openrangelabs.services.bonita.model.ProcessInitiationResponse
import com.openrangelabs.services.user.bloxops.dao.PendingUserBloxopsDAO
import com.openrangelabs.services.user.bonita.BonitaADUserAPIService
import com.openrangelabs.services.user.bonita.BonitaUserAPIService
import com.openrangelabs.services.user.bonita.model.BonitaADUserCheck
import com.openrangelabs.services.user.bonita.model.BonitaPasswordChangeRequest
import com.openrangelabs.services.user.bonita.model.BonitaPasswordChangeResponse
import com.openrangelabs.services.user.bonita.model.BonitaUserContactDetails
import com.openrangelabs.services.user.bonita.model.BonitaUserDetails
import com.openrangelabs.services.user.model.PasswordChangeRequest
import com.openrangelabs.services.user.model.UserCreate
import com.openrangelabs.services.user.model.UserCreateRequest
import com.openrangelabs.services.user.model.UserIdentificationResponse
import com.openrangelabs.services.user.model.UserResponse
import com.openrangelabs.services.user.profile.ProfileService
import com.openrangelabs.services.user.profile.model.UserProfile
import com.openrangelabs.services.user.repository.PendingUser

import org.springframework.http.HttpStatus
import spock.lang.Specification

class UserServiceSpec extends Specification {
    BonitaUserAPIService bonitaUserService
    BonitaAuthenticateAPIService bonitaAuthenticateAPIService
    ProfileService profileService
    ErrorMessageService errorMessageService
    AuthenticateService authenticateService
    BonitaOrganizationAPIService bonitaOrganizationAPIService
    BonitaUserAPIService bonitaUserAPIService
    TicketService ticketService
    PendingUserBloxopsDAO pendingUserBloxopsDAO
    FusebillAPIService fusebillAPIService
    BonitaADUserAPIService bonitaADUserAPIService;

    UserService userService

    def setup() {
        bonitaUserService = Mock(BonitaUserAPIService)
        bonitaAuthenticateAPIService = Mock(BonitaAuthenticateAPIService)
        errorMessageService = Mock(ErrorMessageService)
        bonitaUserService = Mock(BonitaUserAPIService)
        bonitaAuthenticateAPIService = Mock(BonitaAuthenticateAPIService)
        profileService = Mock(ProfileService)
        errorMessageService = Mock(ErrorMessageService)
        authenticateService = Mock(AuthenticateService)
        bonitaOrganizationAPIService = Mock(BonitaOrganizationAPIService)
        bonitaUserAPIService = Mock(BonitaUserAPIService)
        ticketService = Mock(TicketService)
        pendingUserBloxopsDAO = Mock(PendingUserBloxopsDAO)
        fusebillAPIService = Mock(FusebillAPIService)
        bonitaADUserAPIService = Mock(BonitaADUserAPIService)

        userService = new UserService(bonitaUserService, errorMessageService, bonitaAuthenticateAPIService, profileService, bonitaUserAPIService,
                authenticateService, bonitaOrganizationAPIService, ticketService, pendingUserBloxopsDAO, fusebillAPIService, bonitaADUserAPIService)
        authenticateService.middlewareAdminKey = "1234"
    }

    def 'Update Password Successfuly'() {
        given:"A second factor auth code"
        UserIdentificationResponse user = new UserIdentificationResponse()
        PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest( passwordConfirm:"test", newPassword:"test")
        SessionInfo sessionInfo = new SessionInfo()
        BonitaPasswordChangeResponse passwordResponse = new BonitaPasswordChangeResponse(success: true)

        when:'updatePassword service method call'
        def result = userService.updatePassword(user, passwordChangeRequest, sessionInfo)

        then:'Expect service calls and response success'
        1 * bonitaUserService.updatePassword(new BonitaPasswordChangeRequest(user.getUserName(), passwordChangeRequest.getNewPassword()), sessionInfo) >> passwordResponse

        assert result.error == null
        assert result.success == true
    }

    def 'Update Password failure on passwords do not match'() {
        given:"A second factor auth code"
        UserIdentificationResponse user = new UserIdentificationResponse()
        PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest( passwordConfirm:"test", newPassword:"test111")
        SessionInfo sessionInfo = new SessionInfo()
        BonitaPasswordChangeResponse passwordResponse = new BonitaPasswordChangeResponse(success: true)

        when:'getAuthenticatedUser service method call'
        def result = userService.updatePassword(user, passwordChangeRequest, sessionInfo)

        then:'updatePassword service method call'
        assert result.error == "Unable to change password. New password and confirmation do not match."
        assert result.success == false
    }

    def 'Update Password Failure on password update with error response'() {
        given:"A second factor auth code"
        UserIdentificationResponse user = new UserIdentificationResponse()
        PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest( passwordConfirm:"test", newPassword:"test")
        SessionInfo sessionInfo = new SessionInfo()
        BonitaPasswordChangeResponse passwordResponse = new BonitaPasswordChangeResponse(success: false, error:"it went down")

        when:'updatePassword service method call'
        def result = userService.updatePassword(user, passwordChangeRequest, sessionInfo)

        then:'Expect service calls and response error message'
        1 * bonitaUserService.updatePassword(new BonitaPasswordChangeRequest(user.getUserName(), passwordChangeRequest.getNewPassword()), sessionInfo) >> passwordResponse

        assert result.error == "Unable to change password."
        assert result.success == false
    }

    def 'Update Password Failure on password update error thrown'() {
        given:"A second factor auth code"
        UserIdentificationResponse user = new UserIdentificationResponse()
        PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest( passwordConfirm:"test", newPassword:"test")
        SessionInfo sessionInfo = new SessionInfo()

        when:'updatePassword service method call'
        def result = userService.updatePassword(user, passwordChangeRequest, sessionInfo)

        then:'Expect service calls and response error message'
        1 * bonitaUserService.updatePassword(new BonitaPasswordChangeRequest(user.getUserName(), passwordChangeRequest.getNewPassword()), sessionInfo) >> { throw new Exception() }

        assert result.error == "Unable to change password."
        assert result.success == false
    }

    def 'Create User Successfully' () {
        given:'A logged in user and a create user request'
        UserIdentificationResponse user = new UserIdentificationResponse(userId: 111, companyId: 1212)
        UserCreate userCreate = new UserCreate( emailAddress: "fdas@dfsa.com", firstName: "Bob", lastName: "Cob")
        UserCreateRequest userCreateRequest = new UserCreateRequest(userDetails:userCreate)
        BonitaADUserCheck bonitaADUserCheck = new BonitaADUserCheck()
        FusebillCustomer customer = new FusebillCustomer(id:"1111")
        ProcessInitiationResponse response = new ProcessInitiationResponse(caseId: "1243")

        SessionInfo sessionInfo = new SessionInfo()
        PendingUser pendingUser = new PendingUser(id:111, firstName: "Bob", lastName: "Cob")
        when:'createUser service method call'
        def result = userService.createUser(user, userCreateRequest, sessionInfo)

        then:'Expect service calls and response error message'
        1 * fusebillAPIService.getCustomer(user.companyId) >> customer
        1 * bonitaADUserAPIService.checkIfEmailAndNameIsUsed(userCreate.getEmailAddress(), userCreate.getFirstName(), userCreate.getLastName(), sessionInfo) >> bonitaADUserCheck
        1 * pendingUserBloxopsDAO.save(_) >> 111
        1 * ticketService.createTicket(user, _, sessionInfo) >> response

        assert result.caseId == "1243"
    }

    def 'Create User Failure on used email' () {
        given:'A logged in user and a create user request'
        UserIdentificationResponse user = new UserIdentificationResponse(userId: 111, companyId: "222")
        UserCreate userCreate = new UserCreate()
        UserCreateRequest userCreateRequest = new UserCreateRequest(userDetails:userCreate)
        SessionInfo sessionInfo = new SessionInfo()
        FusebillCustomer customer = new FusebillCustomer(id:"1111")
        ProcessInitiationResponse response = new ProcessInitiationResponse(caseId: "1243")
        BonitaADUserCheck bonitaADUserCheck = new BonitaADUserCheck( clazz: "NAME_MISMATCH")

        when:'createUser service method call'
        def result = userService.createUser(user, userCreateRequest, sessionInfo)

        then:'Expect service calls until error thrown'
        1 * fusebillAPIService.getCustomer("222") >> customer
        1 * bonitaADUserAPIService.checkIfEmailAndNameIsUsed(userCreate.getEmailAddress(), userCreate.getFirstName(), userCreate.getLastName(), sessionInfo) >> bonitaADUserCheck

        assert result.caseId == null
        assert result.error == "Email already assigned to different user."
    }

    def 'Create User Failure saving user' () {
        given:'A logged in user and a create user request'
        UserIdentificationResponse user = new UserIdentificationResponse(userId: 111, companyId: "222")
        UserCreate userCreate = new UserCreate()
        UserCreateRequest userCreateRequest = new UserCreateRequest(userDetails:userCreate)
        SessionInfo sessionInfo = new SessionInfo()
        FusebillCustomer customer = new FusebillCustomer()
        ProcessInitiationResponse response = new ProcessInitiationResponse(caseId: "1243")
        BonitaADUserCheck bonitaADUserCheck = new BonitaADUserCheck()

        when:'createUser service method call'
        def result = userService.createUser(user, userCreateRequest, sessionInfo)

        then:'Expect service calls until error thrown'
        1 * fusebillAPIService.getCustomer("222") >> customer
        1 * bonitaADUserAPIService.checkIfEmailAndNameIsUsed(userCreate.getEmailAddress(), userCreate.getFirstName(), userCreate.getLastName(), sessionInfo) >> bonitaADUserCheck
        1 * pendingUserBloxopsDAO.save(_) >> { throw new Exception() }
        0 * ticketService.createTicket(user, _, sessionInfo) >> response

        assert result.caseId == null
        assert result.error != null
    }

    def 'Create User Failure creating ticket' () {
        given:'A logged in user and a create user request'
        UserIdentificationResponse user = new UserIdentificationResponse(userId: 111, companyId: "222")
        UserCreate userCreate = new UserCreate()
        UserCreateRequest userCreateRequest = new UserCreateRequest(userDetails:userCreate)
        SessionInfo sessionInfo = new SessionInfo()
        FusebillCustomer customer = new FusebillCustomer(id:"222")
        PendingUser pendingUser = new PendingUser()
        BonitaADUserCheck bonitaADUserCheck = new BonitaADUserCheck()

        when:'createUser service method call'
        def result = userService.createUser(user, userCreateRequest, sessionInfo)

        then:'Expect service calls until error thrown'
        1 * fusebillAPIService.getCustomer("222") >> customer
        1 * bonitaADUserAPIService.checkIfEmailAndNameIsUsed(userCreate.getEmailAddress(), userCreate.getFirstName(), userCreate.getLastName(), sessionInfo) >> bonitaADUserCheck
        1 * pendingUserBloxopsDAO.save(_) >> 111
        1 * ticketService.createTicket(user, _, sessionInfo) >> { throw new Exception() }

        assert result.caseId == null
        assert result.error != null
    }

    def 'Update User Success' () {
        given:'A logged in user and a create user request'
        UserCreateRequest userCreateRequest = new UserCreateRequest()
        userCreateRequest.setContactDetails(new BonitaUserContactDetails())
        userCreateRequest.setUserDetails(new UserCreate())
        userCreateRequest.getContactDetails().setEmail("don.won@gmail.com")
        UserIdentificationResponse user = new UserIdentificationResponse()
        BonitaUserDetails userDetailResponse = new BonitaUserDetails()
        userDetailResponse.setId("123")
        user.setUserId("123")
        SessionInfo sessionInfo = new SessionInfo()
        UserResponse userResponse = new UserResponse(user:userDetailResponse)

        when:'createUser service method call'
        def result = userService.updateUser(user, userCreateRequest, sessionInfo)

        then:'Expect service calls until error thrown'
        1 * bonitaUserService.updateUser(userCreateRequest.getUserDetails(), user.getUserId(), sessionInfo) >> HttpStatus.OK
        1 * bonitaUserService.updateProfessionalContactDetails(userCreateRequest.getContactDetails(), user.getUserId(), sessionInfo) >> userCreateRequest.getContactDetails()
        1 * bonitaUserService.updatePersonalContactDetails(userCreateRequest.getContactDetails(), user.getUserId(), sessionInfo) >> userCreateRequest.getContactDetails()
        1 * profileService.updateUserProfileEmail(_, userCreateRequest.getContactDetails().getEmail())
        1 * authenticateService.getAuthenticatedUser(user, sessionInfo) >> userResponse

        assert result.getUser() == userDetailResponse
        assert result.error == null
    }

    def 'Update User Failure detail contact and user details not sent' () {
        given:'A logged in user and a create user request'
        UserCreateRequest userCreateRequest = new UserCreateRequest()
        UserIdentificationResponse user = new UserIdentificationResponse()
        BonitaUserDetails userDetailResponse = new BonitaUserDetails()
        userDetailResponse.setId("123")
        user.setUserId("123")
        SessionInfo sessionInfo = new SessionInfo()
        UserProfile userProfile = new UserProfile()
        when:'createUser service method call'
        def result = userService.updateUser(user, userCreateRequest, sessionInfo)

        then:'Expect service calls until error thrown'
        1 * errorMessageService.getAuthenicateErrorMessage(1) >> "error"
        0 * bonitaUserService.updateUser(userCreateRequest.getUserDetails(), user.getUserId(), sessionInfo) >> HttpStatus.OK
        0 * bonitaUserService.updateProfessionalContactDetails(userCreateRequest.getContactDetails(), user.getUserId(), sessionInfo) >> userCreateRequest.getContactDetails()
        0 * bonitaUserService.updatePersonalContactDetails(userCreateRequest.getContactDetails(), user.getUserId(), sessionInfo) >> userCreateRequest.getContactDetails()
        0 * profileService.updateUserProfileEmail(_, _)
        0 * bonitaAuthenticateAPIService.getUserDetails(user.getUserId(), sessionInfo) >> userDetailResponse
        0 * bonitaAuthenticateAPIService.getUserProfessionalDetails(user.getUserId(), sessionInfo) >> userCreateRequest.getContactDetails()
        0 * profileService.retrieveUserProfile(Integer.parseInt(userDetailResponse.getId()), _) >> userProfile

        assert result.getUser() != null
        assert result.error == "error"
    }

    def 'Update User Failure updating user details' () {
        given:'A logged in user and a create user request'
        UserCreateRequest userCreateRequest = new UserCreateRequest()
        userCreateRequest.setContactDetails(new BonitaUserContactDetails())
        userCreateRequest.setUserDetails(new UserCreate())
        userCreateRequest.getContactDetails().setEmail("don.won@gmail.com")
        UserIdentificationResponse user = new UserIdentificationResponse()
        BonitaUserDetails userDetailResponse = new BonitaUserDetails()
        userDetailResponse.setErrorFields()
        userDetailResponse.setId("123")
        user.setUserId("123")
        SessionInfo sessionInfo = new SessionInfo()
        UserResponse userResponse = new UserResponse(user:userDetailResponse)

        when:'createUser service method call'
        def result = userService.updateUser(user, userCreateRequest, sessionInfo)

        then:'Expect service calls until error thrown'
        1 * bonitaUserService.updateUser(userCreateRequest.getUserDetails(), user.getUserId(), sessionInfo) >> { throw new Exception() }
        1 * errorMessageService.getAuthenicateErrorMessage(2) >> "error"
        1 * bonitaUserService.updateProfessionalContactDetails(userCreateRequest.getContactDetails(), user.getUserId(), sessionInfo) >> userCreateRequest.getContactDetails()
        1 * bonitaUserService.updatePersonalContactDetails(userCreateRequest.getContactDetails(), user.getUserId(), sessionInfo) >> userCreateRequest.getContactDetails()
        1 * profileService.updateUserProfileEmail(_, userCreateRequest.getContactDetails().getEmail())
        1 * authenticateService.getAuthenticatedUser(user, sessionInfo) >> userResponse

        userDetailResponse.setId("error")
        assert result.getUser() == userDetailResponse
        assert result.error == "error"
    }

    def 'Update User Failure on contact update' () {
        given:'A logged in user and a create user request'
        UserCreateRequest userCreateRequest = new UserCreateRequest()
        userCreateRequest.setContactDetails(new BonitaUserContactDetails())
        userCreateRequest.setUserDetails(new UserCreate())
        userCreateRequest.getContactDetails().setEmail("don.won@gmail.com")
        UserIdentificationResponse user = new UserIdentificationResponse()
        BonitaUserDetails userDetailResponse = new BonitaUserDetails()
        userDetailResponse.setId("123")
        user.setUserId("123")
        SessionInfo sessionInfo = new SessionInfo()
        UserResponse userResponse = new UserResponse(user:userDetailResponse)

        when:'createUser service method call'
        def result = userService.updateUser(user, userCreateRequest, sessionInfo)

        then:'Expect service calls until error thrown'
        1 * bonitaUserService.updateUser(userCreateRequest.getUserDetails(), user.getUserId(), sessionInfo) >> HttpStatus.OK
        1 * bonitaUserService.updateProfessionalContactDetails(userCreateRequest.getContactDetails(), user.getUserId(), sessionInfo) >> userCreateRequest.getContactDetails()
        1 * bonitaUserService.updatePersonalContactDetails(userCreateRequest.getContactDetails(), user.getUserId(), sessionInfo) >> { throw new Exception() }
        1 * errorMessageService.getAuthenicateErrorMessage(3) >> "error"
        1 * profileService.updateUserProfileEmail(_, userCreateRequest.getContactDetails().getEmail())
        1 * authenticateService.getAuthenticatedUser(user, sessionInfo) >> userResponse

        assert result.getUser() == userDetailResponse
        assert result.error == "error"
    }

    def 'Update User Failure on Profile Update' () {
        given:'A logged in user and a create user request'
        UserCreateRequest userCreateRequest = new UserCreateRequest()
        userCreateRequest.setContactDetails(new BonitaUserContactDetails())
        userCreateRequest.setUserDetails(new UserCreate())
        userCreateRequest.getContactDetails().setEmail("don.won@gmail.com")
        UserIdentificationResponse user = new UserIdentificationResponse()
        def userDetailResponse = new BonitaUserDetails(contactDetails: new BonitaUserContactDetails(email: "dfadf@fads.com"))
        userDetailResponse.setId("123")
        user.setUserId("123")
        SessionInfo sessionInfo = new SessionInfo()
        UserResponse userResponse = new UserResponse(user:userDetailResponse)

        when:'createUser service method call'
        def result = userService.updateUser(user, userCreateRequest, sessionInfo)

        then:'Expect service calls until error thrown'
        1 * bonitaUserService.updateUser(userCreateRequest.getUserDetails(), user.getUserId(), sessionInfo) >> HttpStatus.OK
        1 * bonitaUserService.updateProfessionalContactDetails(userCreateRequest.getContactDetails(), user.getUserId(), sessionInfo) >> userCreateRequest.getContactDetails()
        1 * bonitaUserService.updatePersonalContactDetails(userCreateRequest.getContactDetails(), user.getUserId(), sessionInfo) >> userCreateRequest.getContactDetails()
        1 * profileService.updateUserProfileEmail(_, userCreateRequest.getContactDetails().getEmail()) >> { throw new Exception() }
        1 * errorMessageService.getAuthenicateErrorMessage(1) >> "error"
        1 * authenticateService.getAuthenticatedUser(user, sessionInfo) >> userResponse

        assert result.getUser() == userDetailResponse
        assert result.error == "error"
    }
}
