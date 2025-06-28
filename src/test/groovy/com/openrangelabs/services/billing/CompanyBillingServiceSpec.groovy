package com.openrangelabs.services.billing

import com.openrangelabs.services.authenticate.bonita.BonitaAuthenticateAPIService
import com.openrangelabs.services.authenticate.model.SessionInfo
import com.openrangelabs.services.billing.fusebill.FusebillAPIService
import com.openrangelabs.services.billing.fusebill.model.FusebillCustomer
import com.openrangelabs.services.user.bonita.BonitaUserAPIService
import com.openrangelabs.services.user.model.UserIdentificationResponse
import spock.lang.Specification

class CompanyBillingServiceSpec extends Specification {
    BonitaAuthenticateAPIService bonitaAuthenticateAPIService
    BonitaUserAPIService bonitaUserAPIService
    FusebillAPIService fusebillAPIService
    CompanyBillingService companyBillingService

    def setup() {
        bonitaAuthenticateAPIService = Mock(BonitaAuthenticateAPIService)
        bonitaUserAPIService = Mock(BonitaUserAPIService)
        fusebillAPIService = Mock(FusebillAPIService)

        companyBillingService = new CompanyBillingService(bonitaAuthenticateAPIService, fusebillAPIService, bonitaUserAPIService)
    }

    def 'Get company profile successfully'() {
        given:'a logged in user and session info'
        UserIdentificationResponse user = new UserIdentificationResponse(userId: "777", companyId: "2222")
        SessionInfo sessionInfo = new SessionInfo()
        FusebillCustomer customer = new FusebillCustomer(id:2222)

        when:'Get account summary is called'
        def result = companyBillingService.getCompanyProfile(user, sessionInfo)

        then:'return the balance and next billing date with no errors'
        1 * fusebillAPIService.getCustomer(user.companyId) >> customer

        assert result.customer == customer
        assert result.customer.id == customer.id
        assert result.error == null
    }

    def 'Get company profile failure'() {
        given:'a logged in user and session info'
        UserIdentificationResponse user = new UserIdentificationResponse(userId: "777", companyId: "2222")
        SessionInfo sessionInfo = new SessionInfo()

        when:'Get account summary is called'
        def result = companyBillingService.getCompanyProfile(user, sessionInfo)

        then:'return the balance and next billing date with no errors'
        1 * fusebillAPIService.getCustomer(user.companyId) >> {throw new Exception()}

        assert result.customer == null
        assert result.error == "Could not authorize"
    }
}
