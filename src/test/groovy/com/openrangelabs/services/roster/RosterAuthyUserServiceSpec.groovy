package com.openrangelabs.services.roster

import com.openrangelabs.services.authenticate.model.SessionInfo
import com.openrangelabs.services.billing.fusebill.model.FusebillCustomer
import com.openrangelabs.services.organization.bloxops.dao.BloxopsOrganizationDAO
import com.openrangelabs.services.datacenter.entity.Datacenter
import com.openrangelabs.services.roster.entity.RosterUser
import com.openrangelabs.services.roster.model.RosterUserCreateRequest

import com.openrangelabs.services.bonita.model.ProcessInitiationResponse

import com.openrangelabs.services.roster.bloxops.dao.RosterBloxopsDAO
import com.openrangelabs.services.user.bonita.BonitaUserAPIService
import com.openrangelabs.services.user.bonita.model.BonitaGroup
import com.openrangelabs.services.user.model.UserIdentificationResponse
import com.openrangelabs.services.user.profile.dao.UserBloxopsDAO
import spock.lang.Specification

class RosterAuthyUserServiceSpec extends Specification {
    RosterBloxopsDAO rosterBloxopsDAO
    TicketService ticketService
    BonitaUserAPIService bonitaUserAPIService
    BloxopsOrganizationDAO bloxopsOrganizationDAO
    UserBloxopsDAO userBloxopsDAO

    RosterUserService rosterUserService

    def setup() {
        rosterBloxopsDAO = Mock(RosterBloxopsDAO)
        bloxopsOrganizationDAO = Mock(BloxopsOrganizationDAO)
        ticketService = Mock(TicketService)
        bonitaUserAPIService = Mock(BonitaUserAPIService)
        userBloxopsDAO = Mock(UserBloxopsDAO)

        rosterUserService = new RosterUserService(rosterBloxopsDAO,
                bonitaUserAPIService, bloxopsOrganizationDAO, userBloxopsDAO)
    }

    def 'Create Roster User successfully'() {
        given:'a logged in user and session info'
        UserIdentificationResponse user = new UserIdentificationResponse(companyId: "1244566")
        RosterUserCreateRequest rosterUserCreate = new RosterUserCreateRequest(companyId: 1244566, locationIds: [1L,2L],
                emailAddress: "bryce@gmail.com", firstName: "Bryce", lastName: "Stock", badgeRequired: true)
        SessionInfo sessionInfo = new SessionInfo()
        BonitaGroup group = new BonitaGroup(name:"1244566")
        FusebillCustomer customer = new FusebillCustomer()
        RosterUser rosterUser = new RosterUser()
        Datacenter datacenter1 = new Datacenter( id:1, city:"Chattanooga", name:"CHA", state:"TN" )
        Datacenter datacenter2 = new Datacenter( id:2, city:"Brimingham", name:"BHM", state:"AL" )
        Optional<Datacenter> optional1 = Optional.of((Datacenter) datacenter1)
        Optional<Datacenter> optional2 = Optional.of((Datacenter) datacenter2)
        ProcessInitiationResponse ticketResponse = new ProcessInitiationResponse(caseId: "1234")

        when:'Get account summary is called'
        def result = rosterUserService.createRosterUser(user, rosterUserCreate, sessionInfo)

        then:'return the balance and next billing date with no errors'
        1 * fusebillAPIService.getCustomer(rosterUserCreate.getOrganizationId()+"") >> customer
        1 * rosterBloxopsDAO.save(_) >> 111
        1 * bloxopsOrganizationDAO.getDatacenter(_) >> datacenter1
        1 * bloxopsOrganizationDAO.getDatacenter(_) >> datacenter2
        1 * rosterBloxopsDAO.saveAllUserAccess(_)
        1 * bonitaTicketAPIService.createTicket(user, _, sessionInfo) >> ticketResponse

        assert result.caseId == ticketResponse.caseId
        assert result.error == null
    }

    def 'Create Roster User Failure to get Fusebill Customer'() {
        given:'a logged in user and session info'
        UserIdentificationResponse user = new UserIdentificationResponse()
        RosterUserCreateRequest rosterUserCreate = new RosterUserCreateRequest(companyId: 1244566, locationIds: [1L,2L],
                emailAddress: "bryce@gmail.com", firstName: "Bryce", lastName: "Stock", badgeRequired: true)
        SessionInfo sessionInfo = new SessionInfo()
        BonitaGroup group = new BonitaGroup(name:"1244566")

        when:'Get account summary is called'
        def result = rosterUserService.createRosterUser(user, rosterUserCreate, sessionInfo)

        then:'return empty result with error when exception is thrown'
        1 * fusebillAPIService.getCustomer(rosterUserCreate.getOrganizationId()+"") >> {throw new Exception()}


        assert result.caseId == null
        assert result.error == "Unable to authorize user for this account."
    }

    def 'Create Roster User failure on customer is not authorized for this account'() {
        given:'a logged in user and session info'
        UserIdentificationResponse user = new UserIdentificationResponse(companyId: 222)
        RosterUserCreateRequest rosterUserCreate = new RosterUserCreateRequest(companyId: 1244566, locationIds: [1L,2L],
                emailAddress: "bryce@gmail.com", firstName: "Bryce", lastName: "Stock", badgeRequired: true)
        SessionInfo sessionInfo = new SessionInfo()
        BonitaGroup group = new BonitaGroup(name:"22222")
        FusebillCustomer customer = new FusebillCustomer()
        RosterUser rosterUser = new RosterUser()
        Datacenter datacenter1 = new Datacenter( id:1, city:"Chattanooga", name:"CHA", state:"TN" )
        Datacenter datacenter2 = new Datacenter( id:2, city:"Brimingham", name:"BHM", state:"AL" )
        Optional<Datacenter> optional1 = Optional.of((Datacenter) datacenter1)
        Optional<Datacenter> optional2 = Optional.of((Datacenter) datacenter2)
        ProcessInitiationResponse ticketResponse = new ProcessInitiationResponse(caseId: "1234")

        when:'Get account summary is called'
        def result = rosterUserService.createRosterUser(user, rosterUserCreate, sessionInfo)

        then:'return empty result with error when exception is thrown'
        1 * fusebillAPIService.getCustomer(rosterUserCreate.getOrganizationId()+"") >> customer

        assert result.caseId == null
        assert result.error == "User is not authorized to make new users for this account"
    }

    def 'Create Roster User Failure saving User to Database'() {
        given:'a logged in user and session info'
        UserIdentificationResponse user = new UserIdentificationResponse(companyId: 1244566)
        RosterUserCreateRequest rosterUserCreate = new RosterUserCreateRequest(companyId: 1244566, locationIds: [1L,2L],
                emailAddress: "bryce@gmail.com", firstName: "Bryce", lastName: "Stock", badgeRequired: true)
        SessionInfo sessionInfo = new SessionInfo()
        BonitaGroup group = new BonitaGroup(name:"1244566")
        FusebillCustomer customer = new FusebillCustomer()

        when:'Get account summary is called'
        def result = rosterUserService.createRosterUser(user, rosterUserCreate, sessionInfo)

        then:'return empty result with error when exception is thrown'
        1 * fusebillAPIService.getCustomer(rosterUserCreate.getOrganizationId()+"") >> customer
        1 * rosterBloxopsDAO.save(_) >> { throw new Exception() }

        assert result.caseId == null
        assert result.error == "Unable to save roster user"
    }

    def 'Create Roster User failure to create ticket'() {
        given:'a logged in user and session info'
        UserIdentificationResponse user = new UserIdentificationResponse(companyId: 1244566)
        RosterUserCreateRequest rosterUserCreate = new RosterUserCreateRequest(companyId: 1244566, locationIds: [1L,2L],
                emailAddress: "bryce@gmail.com", firstName: "Bryce", lastName: "Stock", badgeRequired: true)
        SessionInfo sessionInfo = new SessionInfo()
        BonitaGroup group = new BonitaGroup(name:"1244566")
        FusebillCustomer customer = new FusebillCustomer()
        RosterUser rosterUser = new RosterUser()
        Datacenter datacenter1 = new Datacenter( id:1, city:"Chattanooga", name:"CHA", state:"TN" )
        Datacenter datacenter2 = new Datacenter( id:2, city:"Brimingham", name:"BHM", state:"AL" )
        Optional<Datacenter> optional1 = Optional.of((Datacenter) datacenter1)
        Optional<Datacenter> optional2 = Optional.of((Datacenter) datacenter2)

        when:'Get account summary is called'
        def result = rosterUserService.createRosterUser(user, rosterUserCreate, sessionInfo)

        then:'return empty result with error when exception is thrown'
        1 * fusebillAPIService.getCustomer(rosterUserCreate.getOrganizationId()+"") >> customer
        1 * rosterBloxopsDAO.save(_) >> 111
        1 * bloxopsOrganizationDAO.getDatacenter(_) >> datacenter1
        1 * bloxopsOrganizationDAO.getDatacenter(_) >> datacenter2
        1 * rosterBloxopsDAO.saveAllUserAccess(_)

        1 * ticketService.createTicket(user, _, sessionInfo) >> { throw new Exception() }

        assert result.caseId == null
        assert result.error == "Unable to create roster user"
    }
}
