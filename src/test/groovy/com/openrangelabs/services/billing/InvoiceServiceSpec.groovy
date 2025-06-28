package com.openrangelabs.services.billing


import com.openrangelabs.services.authenticate.bonita.BonitaAuthenticateAPIService
import com.openrangelabs.services.authenticate.model.SessionInfo
import com.openrangelabs.services.billing.dao.BillingBloxopsDAO
import com.openrangelabs.services.billing.fusebill.FusebillAPIService
import com.openrangelabs.services.billing.fusebill.model.TransactionAllocation
import com.openrangelabs.services.billing.model.InvoiceRecord
import com.openrangelabs.services.billing.model.InvoiceRequest

import com.openrangelabs.services.billing.storage.S3BillingService
import com.openrangelabs.services.message.ErrorMessageService
import com.openrangelabs.services.user.bonita.BonitaUserAPIService
import com.openrangelabs.services.user.bonita.model.BonitaGroup
import com.openrangelabs.services.user.model.UserIdentificationResponse
import spock.lang.Specification

import java.time.OffsetDateTime

class InvoiceServiceSpec extends Specification {
    BillingBloxopsDAO billingBloxopsDAO
    BonitaAuthenticateAPIService bonitaAuthenticateAPIService
    BonitaUserAPIService bonitaUserAPIService
    FusebillAPIService fusebillAPIService
    S3BillingService s3BillingService
    ErrorMessageService errorMessageService

    InvoiceService invoiceService

    def setup() {
        billingBloxopsDAO = Mock(BillingBloxopsDAO)
        bonitaAuthenticateAPIService = Mock(BonitaAuthenticateAPIService)
        bonitaUserAPIService = Mock(BonitaUserAPIService)
        fusebillAPIService = Mock(FusebillAPIService)
        s3BillingService = Mock(S3BillingService)
        errorMessageService = Mock(ErrorMessageService)

        invoiceService = new InvoiceService(billingBloxopsDAO, bonitaAuthenticateAPIService, s3BillingService,
                bonitaUserAPIService, fusebillAPIService, errorMessageService)
    }

    def 'Get customer invoices successfully'() {
        given:'a logged in user and session info'
        UserIdentificationResponse user = new UserIdentificationResponse( companyId: 1234)
        InvoiceRequest invoiceRequest = new InvoiceRequest()
        SessionInfo sessionInfo = new SessionInfo()
        List<InvoiceRecord> invoiceRecords = new ArrayList<>()
        InvoiceRecord invoice = new InvoiceRecord(invoiceId: 123456);
        invoiceRecords.add(invoice)
        List<TransactionAllocation> transactions = new ArrayList<>()

        when:'Get Customer Invoices without status filters'
        def result = invoiceService.getCustomerInvoices(user, invoiceRequest, sessionInfo)

        then:'expect returned invoices without errors'
        1 * billingBloxopsDAO.getCustomerInvoiceData(1234) >> invoiceRecords
        1 * billingBloxopsDAO.getTransactionAllocationsForInvoice(1234, invoice.getInvoiceId()) >> transactions

        assert result.invoices == invoiceRecords
        assert result.error == null
    }

    def 'Get customer invoices paid successfully'() {
        given:'a logged in user and session info'
        UserIdentificationResponse user = new UserIdentificationResponse(companyId: 1234)
        InvoiceRequest invoiceRequest = new InvoiceRequest(statusFilters: ["Paid"])
        SessionInfo sessionInfo = new SessionInfo()
        List<InvoiceRecord> invoiceRecords = new ArrayList<>()
        InvoiceRecord invoice = new InvoiceRecord(invoiceId: 123456);
        invoiceRecords.add(invoice)
        List<TransactionAllocation> transactions = new ArrayList<>()

        when:'expect returned invoices without errors'
        def result = invoiceService.getCustomerInvoices(user, invoiceRequest, sessionInfo)

        then:'return the balance and next billing date with no errors'
        1 * billingBloxopsDAO.getCustomerInvoiceDataFiltered(1234, invoiceRequest.getStatusFilters()) >> invoiceRecords
        1 * billingBloxopsDAO.getTransactionAllocationsForInvoice(1234, invoice.getInvoiceId()) >> transactions

        assert result.invoices == invoiceRecords
        assert result.error == null
    }

    def 'Get customer invoices unpaid successfully'() {
        given:'a logged in user and session info'
        UserIdentificationResponse user = new UserIdentificationResponse(companyId: 1234)
        InvoiceRequest invoiceRequest = new InvoiceRequest(statusFilters: ["Due", "Overdue"])
        SessionInfo sessionInfo = new SessionInfo()
        BonitaGroup group = new BonitaGroup(name:"1234")
        List<InvoiceRecord> invoiceRecords = new ArrayList<>()
        InvoiceRecord invoice = new InvoiceRecord(invoiceId: 123456)
        invoiceRecords.add(invoice)
        List<TransactionAllocation> transactions = new ArrayList<>()

        when:'expect returned invoices without errors'
        def result = invoiceService.getCustomerInvoices(user, invoiceRequest, sessionInfo)

        then:'return the balance and next billing date with no errors'
        1 * billingBloxopsDAO.getCustomerInvoiceDataFiltered(1234, invoiceRequest.getStatusFilters()) >> invoiceRecords
        1 * billingBloxopsDAO.getTransactionAllocationsForInvoice(1234, invoice.getInvoiceId()) >> transactions

        assert result.invoices == invoiceRecords
        assert result.error == null
    }

    def 'Get invoice file successfully'() {
        given:'a logged in user and session info and invoice id'
        UserIdentificationResponse user = new UserIdentificationResponse(companyId: 1234)
        SessionInfo sessionInfo = new SessionInfo()
        InvoiceRecord invoice = new InvoiceRecord(invoiceId: 123456, filename: "abc.pdf", billcycle: OffsetDateTime.now())

        when:'expect returned invoice without errors'
        def result = invoiceService.getInvoiceFile(user, invoice.invoiceId+"", sessionInfo)

        then:'returned file was not null and invoice id matched'
        1 * billingBloxopsDAO.getInvoiceById(_,_) >> invoice
        1 * s3BillingService.getFile(invoice.filename) >> new ByteArrayInputStream("Some PDF!!!".getBytes())

        assert result.invoiceNumber == invoice.invoiceId
        assert result.file != null
        assert result.error == null
    }

    def 'Get invoice file failure on get file/pdf'() {
        given:'a logged in user and session info'
        UserIdentificationResponse user = new UserIdentificationResponse(companyId: 1234)
        SessionInfo sessionInfo = new SessionInfo()
        InvoiceRecord invoice = new InvoiceRecord(invoiceId: 123456, filename: "abc.pdf", billcycle: OffsetDateTime.now())

        when:'expect return with errors'
        def result = invoiceService.getInvoiceFile(user, invoice.invoiceId+"", sessionInfo)

        then:'return error and no file'
        1 * billingBloxopsDAO.getInvoiceById(_,_) >> invoice
        1 * s3BillingService.getFile(invoice.filename) >> {throw new IOException()}
        1 * errorMessageService.getAuthenicateErrorMessage(1) >> "error"

        assert result.invoiceNumber == 0
        assert result.file == null
        assert result.error == "error"
    }
}
