package com.openrangelabs.services.billing

import com.openrangelabs.services.authenticate.bonita.BonitaAuthenticateAPIService
import com.openrangelabs.services.authenticate.model.SessionInfo
import com.openrangelabs.services.billing.dao.BillingBloxopsDAO
import com.openrangelabs.services.billing.fusebill.FusebillAPIService
import com.openrangelabs.services.billing.fusebill.model.FusebillCustomerOverview
import com.openrangelabs.services.user.bonita.BonitaUserAPIService
import com.openrangelabs.services.user.bonita.model.BonitaGroup
import com.openrangelabs.services.user.model.UserIdentificationResponse
import spock.lang.Specification

class BillingServiceSpec extends Specification {
    BillingBloxopsDAO billingBloxopsDAO
    BonitaAuthenticateAPIService bonitaAuthenticateAPIService
    BonitaUserAPIService bonitaUserAPIService
    FusebillAPIService fusebillAPIService
    CompanyBillingService companyBillingService

    BillingService billingService

    def setup() {
        billingBloxopsDAO = Mock(BillingBloxopsDAO)
        bonitaAuthenticateAPIService = Mock(BonitaAuthenticateAPIService)
        bonitaUserAPIService = Mock(BonitaUserAPIService)
        fusebillAPIService = Mock(FusebillAPIService)
        companyBillingService = Mock(CompanyBillingService)

        billingService = new BillingService(bonitaAuthenticateAPIService, fusebillAPIService,
                billingBloxopsDAO, bonitaUserAPIService, companyBillingService)
    }

    def 'Get account summary successfully'() {
        given:'a logged in user and session info'
        UserIdentificationResponse user = new UserIdentificationResponse(userId: "777", companyId: "222")
        SessionInfo sessionInfo = new SessionInfo()
        FusebillCustomerOverview customerOverview = new FusebillCustomerOverview(arBalance:"20.50", nextBillingDate:"2018-07-01 08:53:55" )

        when:'Get account summary is called'
        def result = billingService.getAccountSummary(user, sessionInfo)

        then:'return the balance and next billing date with no errors'
        1 * fusebillAPIService.getCustomerOverview(user.companyId) >> customerOverview

        assert result.nextBillingDate == "2018-07-01 08:53:55"
        assert result.balance == "20.50"
        assert result.error == null
    }

    def 'Get account summary failure to get fusebill customer overview'() {
        given:'a logged in user and session info'
        UserIdentificationResponse user = new UserIdentificationResponse(userId: "777", companyId: "222")
        SessionInfo sessionInfo = new SessionInfo()
        BonitaGroup group = new BonitaGroup(name: "123456")
        FusebillCustomerOverview customerOverview = new FusebillCustomerOverview(arBalance:"20.50", nextBillingDate:"2018-07-01 08:53:55" )

        when:'Get account summary is called'
        def result = billingService.getAccountSummary(user, sessionInfo)

        then:'return the balance and next billing date with no errors'
        1 * fusebillAPIService.getCustomerOverview(user.companyId) >> {throw new Exception()}

        assert result.nextBillingDate == null
        assert result.balance == null
        assert result.error == "Could not authorize"
    }
}
