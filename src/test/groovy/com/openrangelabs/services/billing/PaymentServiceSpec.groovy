package com.openrangelabs.services.billing

import com.openrangelabs.services.authenticate.bonita.BonitaAuthenticateAPIService
import com.openrangelabs.services.authenticate.model.SessionInfo
import com.openrangelabs.services.billing.dao.TransactionBloxopsDAO
import com.openrangelabs.services.billing.fusebill.FusebillAPIService
import com.openrangelabs.services.billing.fusebill.model.FusebillTransaction
import com.openrangelabs.services.billing.fusebill.model.TransactionAllocation
import com.openrangelabs.services.billing.model.InvoiceRecord
import com.openrangelabs.services.billing.fusebill.FusebillInvoiceAPIService
import com.openrangelabs.services.billing.fusebill.model.FusebillInvoice
import com.openrangelabs.services.billing.fusebill.model.FusebillPaymentResponse
import com.openrangelabs.services.billing.fusebill.model.PaymentSchedule
import com.openrangelabs.services.billing.log.TransactionLogService
import com.openrangelabs.services.billing.model.BrainTreePaymentResponse
import com.openrangelabs.services.billing.model.InvoiceAllocation
import com.openrangelabs.services.billing.model.PaymentRequest
import com.openrangelabs.services.billing.dao.BillingBloxopsDAO;
import com.openrangelabs.services.message.ErrorMessageService
import com.openrangelabs.services.ticket.storage.S3Service
import com.openrangelabs.services.user.bonita.BonitaUserAPIService
import com.openrangelabs.services.user.bonita.model.BonitaGroup
import com.openrangelabs.services.user.bonita.model.BonitaUserMembership
import com.openrangelabs.services.user.model.UserIdentificationResponse
import org.springframework.http.ResponseEntity
import spock.lang.Specification

class PaymentServiceSpec extends Specification {
    PaymentService paymentService
    FusebillAPIService fusebillAPIService
    BonitaUserAPIService bonitaUserAPIService
    ErrorMessageService errorMessageService
    BonitaAuthenticateAPIService bonitaAuthenticateAPIService
    FusebillInvoiceAPIService fusebillInvoiceAPIService
    BrainTreePaymentService brainTreePaymentService
    TransactionLogService transactionLogService
    S3Service s3Service
    BillingBloxopsDAO billingBloxopsDAO
    TransactionBloxopsDAO transactionBloxopsDAO

    def setup() {
        fusebillAPIService = Mock(FusebillAPIService)
        bonitaUserAPIService = Mock(BonitaUserAPIService)
        errorMessageService = Mock(ErrorMessageService)
        bonitaAuthenticateAPIService = Mock(BonitaAuthenticateAPIService)
        fusebillInvoiceAPIService = Mock(FusebillInvoiceAPIService)
        brainTreePaymentService = Mock(BrainTreePaymentService)
        transactionLogService = Mock(TransactionLogService)
        s3Service = Mock(S3Service)
        billingBloxopsDAO = Mock(BillingBloxopsDAO)
        transactionBloxopsDAO = Mock(TransactionBloxopsDAO)

        paymentService = new PaymentService (fusebillAPIService, bonitaUserAPIService,
                errorMessageService, bonitaAuthenticateAPIService,
                fusebillInvoiceAPIService, s3Service, brainTreePaymentService,
                 transactionLogService, billingBloxopsDAO, transactionBloxopsDAO )
    }

    def 'Make payment successfully'() {
        given:'A Payment Request with correct parameters'
        List<InvoiceAllocation> paymentAllocations = [new InvoiceAllocation(amount: BigDecimal.valueOf(55)), new InvoiceAllocation(amount: BigDecimal.valueOf(55))]
        com.openrangelabs.services.billing.log.model.PublicIP publicIP = new com.openrangelabs.services.billing.log.model.PublicIP(ip: "1.1.1.1")
        PaymentRequest paymentRequest = new PaymentRequest(customerId: 111, amount: 110.00, cardholderName: "Ted Tedderson", nonce: "nonce",
                paymentAllocations:paymentAllocations, publicIpAddress:publicIP, privateIpAddress: "2.2.2.2", cardType: "Visa", cardLastFour: "1234" )
        SessionInfo sessionInfo = new SessionInfo()
        UserIdentificationResponse user = new UserIdentificationResponse(companyId: "111")
        ResponseEntity<UserIdentificationResponse> userResponse = ResponseEntity.ok(user)
        BrainTreePaymentResponse brainTreePaymentResponse = new BrainTreePaymentResponse(transactionId: "222")
        InvoiceRecord invoice1 = new InvoiceRecord(invoiceId:222)
        InvoiceRecord invoice2 = new InvoiceRecord(invoiceId:111)
        FusebillPaymentResponse fusebillPaymentResponse = new FusebillPaymentResponse(id:1234)
        ResponseEntity<FusebillPaymentResponse> paymentResponseEntity = ResponseEntity.ok(fusebillPaymentResponse)
        List<TransactionAllocation> allocations = [new TransactionAllocation(amount: BigDecimal.valueOf(55)), new TransactionAllocation(amount: BigDecimal.valueOf(55))]
        FusebillTransaction transaction = new FusebillTransaction(id: ""+fusebillPaymentResponse.getId(), invoiceAllocations: allocations)
        List<FusebillTransaction> transactions = [transaction]
        ResponseEntity<List<FusebillTransaction>> transactionResponse = ResponseEntity.ok(transactions)
        List<PaymentSchedule> paymentSchedules = [new PaymentSchedule(dueDateTimestamp:"2018-06-15T04:04:26")]
        FusebillInvoice fusebillInvoice = new FusebillInvoice(paymentSchedules:paymentSchedules, postedTimestamp: "2018-06-15T04:04:26", invoiceAmount: BigDecimal.valueOf(55), outstandingBalance: BigDecimal.valueOf(55))
        ResponseEntity<FusebillInvoice> invoiceResponse = ResponseEntity.ok(fusebillInvoice)

        when:'makePayment service method call'
        def response = paymentService.makePayment(paymentRequest, sessionInfo)

        then:'Expect service calls and full payment summary as result'
        1 * bonitaAuthenticateAPIService.getUserIdentificationResponse(sessionInfo) >> userResponse
        1 * transactionLogService.writeTransactionLog(_, userResponse.getStatusCode().toString(),
                "Authorized with Bonita", null, user, _,_)
        1 * brainTreePaymentService.makePayment(_, _, _) >> brainTreePaymentResponse
        1 * billingBloxopsDAO.getInvoiceById(111, allocations.get(0).invoiceId) >> invoice1
        1 * billingBloxopsDAO.getInvoiceById(111, allocations.get(1).invoiceId) >> invoice2
        2 * transactionLogService.writeTransactionLog(_, "Success",
                "Get invoice from Middleware to set to pending status", null, null, _, "")
        1 * billingBloxopsDAO.saveAllInvoices(_)
        1 * transactionLogService.writeTransactionLog(_, "Success",
                "Save invoice in Bloxops to set to pending status", null, null, _, "")
        1 * fusebillAPIService.makePayment(paymentRequest) >> paymentResponseEntity
        1 * transactionLogService.writeTransactionLog(_, paymentResponseEntity.getStatusCode().toString(),
                "Recorded Payment to Fusebill", _, null, _, _)
        1 * fusebillInvoiceAPIService.getTransaction(_, 111, _, _) >> transactionResponse
        1 * transactionLogService.writeTransactionLog(_, transactionResponse.getStatusCode().toString(),"Got transaction from fusebill transactionId: "+transactions.get(0).id, null, _, _, _)
        1 * transactionBloxopsDAO.insertTransaction(transaction)
        1 * transactionLogService.writeTransactionLog(_, "Success", "Recorded transaction in bloxops database table cust_prtl_transactions",
                null, null, _, "")
        1 * transactionBloxopsDAO.insertTransactionAllocationsBatch(allocations)
        1 * transactionLogService.writeTransactionLog(_, "Success",
                "Recorded transaction allocation in bloxops database table cust_prtl_transaction_allocation",
                null, null, _, "")
        2 * fusebillInvoiceAPIService.getInvoiceResponse(_) >> invoiceResponse
        1 * billingBloxopsDAO.saveAllInvoices(_)
        1 * transactionLogService.writeTransactionLog(_, "Success", "Recorded invoices in bloxops database table cust_prtl_invoices",
                null, null, _, "")
        1 * billingBloxopsDAO.getTransactions(111, _) >> transactions.get(0)
        1 * transactionLogService.writeTransactionLog(_, "Success", "Got Transaction from Bloxops DB", null, null, _, "")
        1 * billingBloxopsDAO.getTransactionAllocationsByTransaction(111, _) >> allocations
        1 * transactionLogService.writeTransactionLog(_, "Success",
                "Got Transaction Allocation from bloxops database",null, null, _, "")

        assert response.isSuccessful()
        assert response.error == null
        assert response.retryError == false
    }

    def 'Make payment throws error on saving pending invoices but payment still made'() {
        given:'A Payment Request with correct parameters'
        List<InvoiceAllocation> paymentAllocations = [new InvoiceAllocation(amount: BigDecimal.valueOf(55)), new InvoiceAllocation(amount: BigDecimal.valueOf(55))]
        com.openrangelabs.services.billing.log.model.PublicIP publicIP = new com.openrangelabs.services.billing.log.model.PublicIP(ip: "1.1.1.1")
        PaymentRequest paymentRequest = new PaymentRequest(customerId: 111, amount: 110.00, cardholderName: "Ted Tedderson", nonce: "nonce",
                paymentAllocations:paymentAllocations, publicIpAddress:publicIP, privateIpAddress: "2.2.2.2", cardType: "Visa", cardLastFour: "1234" )
        SessionInfo sessionInfo = new SessionInfo()
        UserIdentificationResponse user = new UserIdentificationResponse()
        ResponseEntity<UserIdentificationResponse> userResponse = ResponseEntity.ok(user)
        List<BonitaUserMembership> membershipList = [new BonitaUserMembership(group_id: 111)]
        ResponseEntity<List<BonitaUserMembership>> membershipResponse = ResponseEntity.ok(membershipList)
        long companyId = 111
        BrainTreePaymentResponse brainTreePaymentResponse = new BrainTreePaymentResponse(transactionId: "222")
        InvoiceRecord invoice1 = new InvoiceRecord()
        InvoiceRecord invoice2 = new InvoiceRecord()
        FusebillPaymentResponse fusebillPaymentResponse = new FusebillPaymentResponse(id:1234)
        ResponseEntity<FusebillPaymentResponse> paymentResponseEntity = ResponseEntity.ok(fusebillPaymentResponse)
        List<TransactionAllocation> allocations = [new TransactionAllocation(amount: BigDecimal.valueOf(55)), new TransactionAllocation(amount: BigDecimal.valueOf(55))]
        FusebillTransaction transaction = new FusebillTransaction(id: ""+fusebillPaymentResponse.getId(), invoiceAllocations: allocations)
        List<FusebillTransaction> transactions = [transaction]
        ResponseEntity<List<FusebillTransaction>> transactionResponse = ResponseEntity.ok(transactions)
        List<PaymentSchedule> paymentSchedules = [new PaymentSchedule(dueDateTimestamp:"2018-06-15T04:04:26")]
        FusebillInvoice fusebillInvoice = new FusebillInvoice(paymentSchedules:paymentSchedules, postedTimestamp: "2018-06-15T04:04:26", invoiceAmount: BigDecimal.valueOf(55), outstandingBalance: BigDecimal.valueOf(55))
        ResponseEntity<FusebillInvoice> invoiceResponse = ResponseEntity.ok(fusebillInvoice)

        when:'makePayment service method call'
        def response = paymentService.makePayment(paymentRequest, sessionInfo)

        then:'Expect service calls and full payment summary as result'
        1 * bonitaAuthenticateAPIService.getUserIdentificationResponse(sessionInfo) >> userResponse
        1 * transactionLogService.writeTransactionLog(_, userResponse.getStatusCode().toString(),
                "Authorized with Bonita", null, user, _,_)
        1 * brainTreePaymentService.makePayment(_, _, _) >> brainTreePaymentResponse
        1 * billingBloxopsDAO.getInvoiceById(111,allocations.get(0).invoiceId) >> invoice1
        1 * billingBloxopsDAO.getInvoiceById(111,allocations.get(1).invoiceId) >> invoice2
        2 * transactionLogService.writeTransactionLog(_, "Success",
                "Get invoice from Middleware to set to pending status", null, null, _, "")
        1 * billingBloxopsDAO.saveAllInvoices(_) >> { throw new Exception() }
        1 * transactionLogService.writeTransactionLog(_, _,"Saving invoices as pending", null, null, _, "")
        1 * fusebillAPIService.makePayment(paymentRequest) >> paymentResponseEntity
        1 * transactionLogService.writeTransactionLog(_, paymentResponseEntity.getStatusCode().toString(),
                "Recorded Payment to Fusebill", _, null, _, _)
        1 * fusebillInvoiceAPIService.getTransaction(_, companyId, _, _) >> transactionResponse
        1 * transactionLogService.writeTransactionLog(_, transactionResponse.getStatusCode().toString(),"Got transaction from fusebill transactionId: "+transactions.get(0).id, null, _, _, _)
        1 * transactionBloxopsDAO.insertTransaction(transaction)
        1 * transactionLogService.writeTransactionLog(_, "Success", "Recorded transaction in bloxops database table cust_prtl_transactions",
                null, null, _, "")
        1 * transactionBloxopsDAO.insertTransactionAllocationsBatch(allocations)
        1 * transactionLogService.writeTransactionLog(_, "Success",
                "Recorded transaction allocation in bloxops database table cust_prtl_transaction_allocation",
                null, null, _, "")
        2 * fusebillInvoiceAPIService.getInvoiceResponse(_) >> invoiceResponse
        1 * billingBloxopsDAO.saveAllInvoices(_)
        1 * transactionLogService.writeTransactionLog(_, "Success", "Recorded invoices in bloxops database table cust_prtl_invoices",
                null, null, _, "")
        1 * billingBloxopsDAO.getTransactions(companyId, _) >> transactions.get(0)
        1 * transactionLogService.writeTransactionLog(_, "Success", "Got Transaction from Bloxops DB", null, null, _, "")
        1 * billingBloxopsDAO.getTransactionAllocationsByTransaction(companyId, _) >> allocations
        1 * transactionLogService.writeTransactionLog(_, "Success",
                "Got Transaction Allocation from bloxops database",null, null, _, "")

        assert response.isSuccessful()
        assert response.error == null
        assert response.retryError == false
    }

    def 'Make payment authentication failure'() {
        given:'A Payment Request with correct user details'
        List<InvoiceAllocation> paymentAllocations = [new InvoiceAllocation(amount: BigDecimal.valueOf(55)), new InvoiceAllocation(amount: BigDecimal.valueOf(55))]
        com.openrangelabs.services.billing.log.model.PublicIP publicIP = new com.openrangelabs.services.billing.log.model.PublicIP(ip: "1.1.1.1")
        PaymentRequest paymentRequest = new PaymentRequest(customerId: 111, amount: 110.00, cardholderName: "Ted Tedderson", nonce: "nonce",
                paymentAllocations:paymentAllocations, publicIpAddress:publicIP, privateIpAddress: "2.2.2.2", cardType: "Visa", cardLastFour: "1234" )
        SessionInfo sessionInfo = new SessionInfo()

        when:'makePayment service method call'
        def response = paymentService.makePayment(paymentRequest, sessionInfo)

        then:'Expect service call and returning an error from failed authenciation'
        1 * bonitaAuthenticateAPIService.getUserIdentificationResponse(sessionInfo) >> {throw new Exception()}
        1 * transactionLogService.writeTransactionLog(_, _, "Authorizing with Bonita", null, null, _, _)
        1 * errorMessageService.getAuthenicateErrorMessage(1) >> "error"
        0 * transactionLogService.writeTransactionLog(_, _, _, _, _, _, _)

        assert response.successful == false
        assert response.error == "error"
        assert response.retryError == false
    }

    def 'Make payment customerId validation failure'() {
        given:'A Payment Request with missing parameters'
        List<InvoiceAllocation> paymentAllocations = [new InvoiceAllocation(amount: BigDecimal.valueOf(55)), new InvoiceAllocation(amount: BigDecimal.valueOf(55))]
        com.openrangelabs.services.billing.log.model.PublicIP publicIP = new com.openrangelabs.services.billing.log.model.PublicIP(ip: "1.1.1.1")
        PaymentRequest paymentRequest = new PaymentRequest( amount: 110.00, cardholderName: "Ted Tedderson", nonce: "nonce",
                paymentAllocations:paymentAllocations, publicIpAddress:publicIP, privateIpAddress: "2.2.2.2", cardType: "Visa", cardLastFour: "1234" )

        SessionInfo sessionInfo = new SessionInfo()
        UserIdentificationResponse user = new UserIdentificationResponse()
        ResponseEntity<UserIdentificationResponse> userResponse = ResponseEntity.ok(user)

        when:'authenticateUserCredentials service method call'
        def response = paymentService.makePayment(paymentRequest, sessionInfo)

        then:'Expect service calls and error result from field verification'
        1 * bonitaAuthenticateAPIService.getUserIdentificationResponse(sessionInfo) >> userResponse
        1 * transactionLogService.writeTransactionLog(_, userResponse.getStatusCode().toString(),
                "Authorized with Bonita", null, user, _,_)
        1 * transactionLogService.writeTransactionLog(_, "Failure", "Validating Payment Request Body",
                null, null, _,"N/A")
        0 * transactionLogService.writeTransactionLog(_, _, _, _, _, _, _)

        assert response.successful == false
        assert response.error == "Request field validation failed"
        assert response.retryError == true
    }

    def 'Make payment amount validation failure'() {
        given:'A Payment Request with missing parameters'
        List<InvoiceAllocation> paymentAllocations = [new InvoiceAllocation(amount: BigDecimal.valueOf(55)), new InvoiceAllocation(amount: BigDecimal.valueOf(55))]
        com.openrangelabs.services.billing.log.model.PublicIP publicIP = new com.openrangelabs.services.billing.log.model.PublicIP(ip: "1.1.1.1")
        PaymentRequest paymentRequest = new PaymentRequest(customerId: 123, cardholderName: "Ted Tedderson", nonce: "nonce",
                paymentAllocations:paymentAllocations, publicIpAddress:publicIP, privateIpAddress: "2.2.2.2", cardType: "Visa", cardLastFour: "1234" )

        SessionInfo sessionInfo = new SessionInfo()
        UserIdentificationResponse user = new UserIdentificationResponse()
        ResponseEntity<UserIdentificationResponse> userResponse = ResponseEntity.ok(user)

        when:'authenticateUserCredentials service method call'
        def response = paymentService.makePayment(paymentRequest, sessionInfo)

        then:'Expect service calls and error result from field verification'
        1 * bonitaAuthenticateAPIService.getUserIdentificationResponse(sessionInfo) >> userResponse
        1 * transactionLogService.writeTransactionLog(_, userResponse.getStatusCode().toString(),
                "Authorized with Bonita", null, user, _,_)
        1 * transactionLogService.writeTransactionLog(_, "Failure", "Validating Payment Request Body",
                null, null, _,"N/A")
        0 * transactionLogService.writeTransactionLog(_, _, _, _, _, _, _)

        assert response.successful == false
        assert response.error == "Request field validation failed"
        assert response.retryError == true
    }

    def 'Make payment amount cardholderName failure'() {
        given:'A Payment Request with missing parameters'
        List<InvoiceAllocation> paymentAllocations = [new InvoiceAllocation(amount: BigDecimal.valueOf(55)), new InvoiceAllocation(amount: BigDecimal.valueOf(55))]
        com.openrangelabs.services.billing.log.model.PublicIP publicIP = new com.openrangelabs.services.billing.log.model.PublicIP(ip: "1.1.1.1")
        PaymentRequest paymentRequest = new PaymentRequest(customerId: 123, amount: BigDecimal.ONE, nonce: "nonce",
                paymentAllocations:paymentAllocations, publicIpAddress:publicIP, privateIpAddress: "2.2.2.2", cardType: "Visa", cardLastFour: "1234" )

        SessionInfo sessionInfo = new SessionInfo()
        UserIdentificationResponse user = new UserIdentificationResponse()
        ResponseEntity<UserIdentificationResponse> userResponse = ResponseEntity.ok(user)

        when:'authenticateUserCredentials service method call'
        def response = paymentService.makePayment(paymentRequest, sessionInfo)

        then:'Expect service calls and error result from field verification'
        1 * bonitaAuthenticateAPIService.getUserIdentificationResponse(sessionInfo) >> userResponse
        1 * transactionLogService.writeTransactionLog(_, userResponse.getStatusCode().toString(),
                "Authorized with Bonita", null, user, _,_)
        1 * transactionLogService.writeTransactionLog(_, "Failure", "Validating Payment Request Body",
                null, null, _,"N/A")
        0 * transactionLogService.writeTransactionLog(_, _, _, _, _, _, _)

        assert response.successful == false
        assert response.error == "Request field validation failed"
        assert response.retryError == true
    }

    def 'Make payment amount nonce failure'() {
        given:'A Payment Request with missing parameters'
        List<InvoiceAllocation> paymentAllocations = [new InvoiceAllocation(amount: BigDecimal.valueOf(55)), new InvoiceAllocation(amount: BigDecimal.valueOf(55))]
        com.openrangelabs.services.billing.log.model.PublicIP publicIP = new com.openrangelabs.services.billing.log.model.PublicIP(ip: "1.1.1.1")
        PaymentRequest paymentRequest = new PaymentRequest(customerId: 123, amount: BigDecimal.ONE, cardholderName: "Ted Tedderson",
                paymentAllocations:paymentAllocations, publicIpAddress:publicIP, privateIpAddress: "2.2.2.2", cardType: "Visa", cardLastFour: "1234" )

        SessionInfo sessionInfo = new SessionInfo()
        UserIdentificationResponse user = new UserIdentificationResponse()
        ResponseEntity<UserIdentificationResponse> userResponse = ResponseEntity.ok(user)

        when:'authenticateUserCredentials service method call'
        def response = paymentService.makePayment(paymentRequest, sessionInfo)

        then:'Expect service calls and error result from field verification'
        1 * bonitaAuthenticateAPIService.getUserIdentificationResponse(sessionInfo) >> userResponse
        1 * transactionLogService.writeTransactionLog(_, userResponse.getStatusCode().toString(),
                "Authorized with Bonita", null, user, _,_)
        1 * transactionLogService.writeTransactionLog(_, "Failure", "Validating Payment Request Body",
                null, null, _,"N/A")
        0 * transactionLogService.writeTransactionLog(_, _, _, _, _, _, _)

        assert response.successful == false
        assert response.error == "Request field validation failed"
        assert response.retryError == true
    }

    def 'Make payment amount publicIP failure'() {
        given:'A Payment Request with missing parameters'
        List<InvoiceAllocation> paymentAllocations = [new InvoiceAllocation(amount: BigDecimal.valueOf(55)), new InvoiceAllocation(amount: BigDecimal.valueOf(55))]
        PaymentRequest paymentRequest = new PaymentRequest(customerId: 123, amount: BigDecimal.ONE, cardholderName: "Ted Tedderson", nonce:"abc",
                paymentAllocations:paymentAllocations, privateIpAddress: "2.2.2.2", cardType: "Visa", cardLastFour: "1234" )

        SessionInfo sessionInfo = new SessionInfo()
        UserIdentificationResponse user = new UserIdentificationResponse()
        ResponseEntity<UserIdentificationResponse> userResponse = ResponseEntity.ok(user)

        when:'authenticateUserCredentials service method call'
        def response = paymentService.makePayment(paymentRequest, sessionInfo)

        then:'Expect service calls and error result from field verification'
        1 * bonitaAuthenticateAPIService.getUserIdentificationResponse(sessionInfo) >> userResponse
        1 * transactionLogService.writeTransactionLog(_, userResponse.getStatusCode().toString(),
                "Authorized with Bonita", null, user, _,_)
        1 * transactionLogService.writeTransactionLog(_, "Failure", "Validating Payment Request Body",
                null, null, _,"N/A")
        0 * transactionLogService.writeTransactionLog(_, _, _, _, _, _, _)

        assert response.successful == false
        assert response.error == "Request field validation failed"
        assert response.retryError == true
    }

    def 'Make payment privateIP validation failure'() {
        given:'A Payment Request with missing parameters'
        List<InvoiceAllocation> paymentAllocations = [new InvoiceAllocation(amount: BigDecimal.valueOf(55)), new InvoiceAllocation(amount: BigDecimal.valueOf(55))]
        com.openrangelabs.services.billing.log.model.PublicIP publicIP = new com.openrangelabs.services.billing.log.model.PublicIP(ip: "1.1.1.1")
        PaymentRequest paymentRequest = new PaymentRequest(customerId: 123, amount: BigDecimal.ONE, cardholderName: "Ted Tedderson", nonce: "nonce",
                paymentAllocations:paymentAllocations, publicIpAddress:publicIP, cardType: "Visa", cardLastFour: "1234" )

        SessionInfo sessionInfo = new SessionInfo()
        UserIdentificationResponse user = new UserIdentificationResponse()
        ResponseEntity<UserIdentificationResponse> userResponse = ResponseEntity.ok(user)

        when:'authenticateUserCredentials service method call'
        def response = paymentService.makePayment(paymentRequest, sessionInfo)

        then:'Expect service calls and error result from field verification'
        1 * bonitaAuthenticateAPIService.getUserIdentificationResponse(sessionInfo) >> userResponse
        1 * transactionLogService.writeTransactionLog(_, userResponse.getStatusCode().toString(),
                "Authorized with Bonita", null, user, _,_)
        1 * transactionLogService.writeTransactionLog(_, "Failure", "Validating Payment Request Body",
                null, null, _,"N/A")
        0 * transactionLogService.writeTransactionLog(_, _, _, _, _, _, _)

        assert response.successful == false
        assert response.error == "Request field validation failed"
        assert response.retryError == true
    }

    def 'Make payment cardType validation failure'() {
        given:'A Payment Request with missing parameters'
        List<InvoiceAllocation> paymentAllocations = [new InvoiceAllocation(amount: BigDecimal.valueOf(55)), new InvoiceAllocation(amount: BigDecimal.valueOf(55))]
        com.openrangelabs.services.billing.log.model.PublicIP publicIP = new com.openrangelabs.services.billing.log.model.PublicIP(ip: "1.1.1.1")
        PaymentRequest paymentRequest = new PaymentRequest(customerId: 123, amount: BigDecimal.ONE, cardholderName: "Ted Tedderson", nonce: "nonce",
                paymentAllocations:paymentAllocations, publicIpAddress:publicIP, privateIpAddress: "2.2.2.2", cardLastFour: "1234" )

        SessionInfo sessionInfo = new SessionInfo()
        UserIdentificationResponse user = new UserIdentificationResponse()
        ResponseEntity<UserIdentificationResponse> userResponse = ResponseEntity.ok(user)

        when:'authenticateUserCredentials service method call'
        def response = paymentService.makePayment(paymentRequest, sessionInfo)

        then:'Expect service calls and error result from field verification'
        1 * bonitaAuthenticateAPIService.getUserIdentificationResponse(sessionInfo) >> userResponse
        1 * transactionLogService.writeTransactionLog(_, userResponse.getStatusCode().toString(),
                "Authorized with Bonita", null, user, _,_)
        1 * transactionLogService.writeTransactionLog(_, "Failure", "Validating Payment Request Body",
                null, null, _,"N/A")
        0 * transactionLogService.writeTransactionLog(_, _, _, _, _, _, _)

        assert response.successful == false
        assert response.error == "Request field validation failed"
        assert response.retryError == true
    }

    def 'Make payment cardLastFour validation failure'() {
        given:'A Payment Request with missing parameters'
        List<InvoiceAllocation> paymentAllocations = [new InvoiceAllocation(amount: BigDecimal.valueOf(55)), new InvoiceAllocation(amount: BigDecimal.valueOf(55))]
        com.openrangelabs.services.billing.log.model.PublicIP publicIP = new com.openrangelabs.services.billing.log.model.PublicIP(ip: "1.1.1.1")
        PaymentRequest paymentRequest = new PaymentRequest(customerId: 123, amount: BigDecimal.ONE, cardholderName: "Ted Tedderson", nonce: "nonce",
                paymentAllocations:paymentAllocations, publicIpAddress:publicIP, privateIpAddress: "2.2.2.2", cardType: "Visa" )

        SessionInfo sessionInfo = new SessionInfo()
        UserIdentificationResponse user = new UserIdentificationResponse()
        ResponseEntity<UserIdentificationResponse> userResponse = ResponseEntity.ok(user)

        when:'authenticateUserCredentials service method call'
        def response = paymentService.makePayment(paymentRequest, sessionInfo)

        then:'Expect service calls and error result from field verification'
        1 * bonitaAuthenticateAPIService.getUserIdentificationResponse(sessionInfo) >> userResponse
        1 * transactionLogService.writeTransactionLog(_, userResponse.getStatusCode().toString(),
                "Authorized with Bonita", null, user, _,_)
        1 * transactionLogService.writeTransactionLog(_, "Failure", "Validating Payment Request Body",
                null, null, _,"N/A")
        0 * transactionLogService.writeTransactionLog(_, _, _, _, _, _, _)

        assert response.successful == false
        assert response.error == "Request field validation failed"
        assert response.retryError == true
    }

    def 'Make payment failure on braintree trainsaction id'() {
        given:'A Payment Request with correct parameters'
        List<InvoiceAllocation> paymentAllocations = [new InvoiceAllocation(amount: BigDecimal.valueOf(55)), new InvoiceAllocation(amount: BigDecimal.valueOf(55))]
        com.openrangelabs.services.billing.log.model.PublicIP publicIP = new com.openrangelabs.services.billing.log.model.PublicIP(ip: "1.1.1.1")
        PaymentRequest paymentRequest = new PaymentRequest(customerId: 111, amount: 110.00, cardholderName: "Ted Tedderson", nonce: "nonce",
                paymentAllocations:paymentAllocations, publicIpAddress:publicIP, privateIpAddress: "2.2.2.2", cardType: "Visa", cardLastFour: "1234" )
        SessionInfo sessionInfo = new SessionInfo()
        UserIdentificationResponse user = new UserIdentificationResponse()
        ResponseEntity<UserIdentificationResponse> userResponse = ResponseEntity.ok(user)
        List<BonitaUserMembership> membershipList = [new BonitaUserMembership(group_id: 111)]
        ResponseEntity<List<BonitaUserMembership>> membershipResponse = ResponseEntity.ok(membershipList)
        long companyId = 4225645
        BonitaGroup group = new BonitaGroup(path:"/openrangelabs/customers/", name: companyId+"")
        ResponseEntity<BonitaGroup> groupResponse = ResponseEntity.ok(group)
        BrainTreePaymentResponse brainTreePaymentResponse = new BrainTreePaymentResponse(message: "error")

        when:'makePayment service method call'
        def response = paymentService.makePayment(paymentRequest, sessionInfo)

        then:'Expect service calls and error returned from Braintree'
        1 * bonitaAuthenticateAPIService.getUserIdentificationResponse(sessionInfo) >> userResponse
        1 * transactionLogService.writeTransactionLog(_, userResponse.getStatusCode().toString(),
                "Authorized with Bonita", null, user, _,_)
        1 * brainTreePaymentService.makePayment(_, _, _) >> brainTreePaymentResponse
        1 * transactionLogService.writeTransactionLog(_, "Failure", "Getting back Brain Tree Transaction Id", _, _, _, "")
        0 * transactionLogService.writeTransactionLog(_, _, _, _, _, _, _)

        assert response.successful == false
        assert response.error == brainTreePaymentResponse.getMessage()
        assert response.retryError == true
    }

    def 'Make payment failure on braintree payment'() {
        given:'A Payment Request with correct parameters'
        List<InvoiceAllocation> paymentAllocations = [new InvoiceAllocation(amount: BigDecimal.valueOf(55)), new InvoiceAllocation(amount: BigDecimal.valueOf(55))]
        com.openrangelabs.services.billing.log.model.PublicIP publicIP = new com.openrangelabs.services.billing.log.model.PublicIP(ip: "1.1.1.1")
        PaymentRequest paymentRequest = new PaymentRequest(customerId: 111, amount: 110.00, cardholderName: "Ted Tedderson", nonce: "nonce",
                paymentAllocations:paymentAllocations, publicIpAddress:publicIP, privateIpAddress: "2.2.2.2", cardType: "Visa", cardLastFour: "1234" )
        SessionInfo sessionInfo = new SessionInfo()
        UserIdentificationResponse user = new UserIdentificationResponse()
        ResponseEntity<UserIdentificationResponse> userResponse = ResponseEntity.ok(user)
        List<BonitaUserMembership> membershipList = [new BonitaUserMembership(group_id: 111)]
        ResponseEntity<List<BonitaUserMembership>> membershipResponse = ResponseEntity.ok(membershipList)
        long companyId = 4225645
        BonitaGroup group = new BonitaGroup(path:"/openrangelabs/customers/", name: companyId+"")
        ResponseEntity<BonitaGroup> groupResponse = ResponseEntity.ok(group)
        BrainTreePaymentResponse brainTreePaymentResponse = new BrainTreePaymentResponse(message: "error")

        when:'makePayment service method call'
        def response = paymentService.makePayment(paymentRequest, sessionInfo)

        then:'Expect service calls and error returned from Braintree'
        1 * bonitaAuthenticateAPIService.getUserIdentificationResponse(sessionInfo) >> userResponse
        1 * transactionLogService.writeTransactionLog(_, userResponse.getStatusCode().toString(),
                "Authorized with Bonita", null, user, _,_)
        1 * brainTreePaymentService.makePayment(_, _, _) >> { throw new Exception() }
        1 * transactionLogService.writeTransactionLog(_, "Failure", "Making payment with braintree", _, _, _, "")
        1 * errorMessageService.getAuthenicateErrorMessage(1) >> "error"
        0 * transactionLogService.writeTransactionLog(_, _, _, _, _, _, _)

        assert response.successful == false
        assert response.error == brainTreePaymentResponse.getMessage()
        assert response.retryError == true
    }

    def 'Make payment failure to find invoice'() {
        given:'A Payment Request with correct parameters'
        List<InvoiceAllocation> paymentAllocations = [new InvoiceAllocation(amount: BigDecimal.valueOf(55)), new InvoiceAllocation(amount: BigDecimal.valueOf(55))]
        com.openrangelabs.services.billing.log.model.PublicIP publicIP = new com.openrangelabs.services.billing.log.model.PublicIP(ip: "1.1.1.1")
        PaymentRequest paymentRequest = new PaymentRequest(customerId: 111, amount: 110.00, cardholderName: "Ted Tedderson", nonce: "nonce",
                paymentAllocations:paymentAllocations, publicIpAddress:publicIP, privateIpAddress: "2.2.2.2", cardType: "Visa", cardLastFour: "1234" )
        SessionInfo sessionInfo = new SessionInfo()
        UserIdentificationResponse user = new UserIdentificationResponse()
        ResponseEntity<UserIdentificationResponse> userResponse = ResponseEntity.ok(user)
        List<BonitaUserMembership> membershipList = [new BonitaUserMembership(group_id: 111)]
        ResponseEntity<List<BonitaUserMembership>> membershipResponse = ResponseEntity.ok(membershipList)
        long companyId = 4225645
        BonitaGroup group = new BonitaGroup(path:"/openrangelabs/customers/", name: companyId+"")
        ResponseEntity<BonitaGroup> groupResponse = ResponseEntity.ok(group)
        BrainTreePaymentResponse brainTreePaymentResponse = new BrainTreePaymentResponse(transactionId: "222")

        when:'makePayment service method call'
        def response = paymentService.makePayment(paymentRequest, sessionInfo)

        then:'Expect service calls and full payment summary as result'
        1 * bonitaAuthenticateAPIService.getUserIdentificationResponse(sessionInfo) >> userResponse
        1 * transactionLogService.writeTransactionLog(_, userResponse.getStatusCode().toString(),
                "Authorized with Bonita", null, user, _,_)
        1 * brainTreePaymentService.makePayment(_, _, _) >> brainTreePaymentResponse
        1 * billingBloxopsDAO.getInvoiceById(_,_) >> { throw new Exception() }
        1 * transactionLogService.writeTransactionLog(_, _,"Getting invoices for marking as pending",
                null, null, _, "")
        0 * transactionLogService.writeTransactionLog(_, _, _, _, _, _, _)

        assert response.successful == false
        assert response.error == "Could process payment with Braintree"
        assert response.retryError == true
    }

    def 'Make payment successfully but exception thrown during fusebill payment'() {
        given:'A Payment Request with correct parameters'
        List<InvoiceAllocation> paymentAllocations = [new InvoiceAllocation(amount: BigDecimal.valueOf(55)), new InvoiceAllocation(amount: BigDecimal.valueOf(55))]
        com.openrangelabs.services.billing.log.model.PublicIP publicIP = new com.openrangelabs.services.billing.log.model.PublicIP(ip: "1.1.1.1")
        PaymentRequest paymentRequest = new PaymentRequest(customerId: 111, amount: 110.00, cardholderName: "Ted Tedderson", nonce: "nonce",
                paymentAllocations:paymentAllocations, publicIpAddress:publicIP, privateIpAddress: "2.2.2.2", cardType: "Visa", cardLastFour: "1234" )
        SessionInfo sessionInfo = new SessionInfo()
        UserIdentificationResponse user = new UserIdentificationResponse(companyId: "4225645")
        ResponseEntity<UserIdentificationResponse> userResponse = ResponseEntity.ok(user)
        List<BonitaUserMembership> membershipList = [new BonitaUserMembership(group_id: 111)]
        ResponseEntity<List<BonitaUserMembership>> membershipResponse = ResponseEntity.ok(membershipList)
        long companyId = 4225645
        BonitaGroup group = new BonitaGroup(path:"/openrangelabs/customers/", name: companyId+"")
        ResponseEntity<BonitaGroup> groupResponse = ResponseEntity.ok(group)
        BrainTreePaymentResponse brainTreePaymentResponse = new BrainTreePaymentResponse(transactionId: "222")
        InvoiceRecord invoice1 = new InvoiceRecord()
        InvoiceRecord invoice2 = new InvoiceRecord()
        List<TransactionAllocation> allocations = [new TransactionAllocation(amount: BigDecimal.valueOf(55)),
                                                   new TransactionAllocation(amount: BigDecimal.valueOf(55))]

        when:'makePayment service method call'
        def response = paymentService.makePayment(paymentRequest, sessionInfo)

        then:'Expect service calls and full payment summary as result'
        1 * bonitaAuthenticateAPIService.getUserIdentificationResponse(sessionInfo) >> userResponse
        1 * transactionLogService.writeTransactionLog(_, userResponse.getStatusCode().toString(),
                "Authorized with Bonita", null, user, _,_)
        1 * brainTreePaymentService.makePayment(_, _, _) >> brainTreePaymentResponse
        1 * billingBloxopsDAO.getInvoiceById(_, allocations.get(0).invoiceId) >> invoice1
        1 * billingBloxopsDAO.getInvoiceById(_, allocations.get(1).invoiceId) >> invoice2
        2 * transactionLogService.writeTransactionLog(_, "Success",
                "Get invoice from Middleware to set to pending status", null, null, _, "")
        1 * billingBloxopsDAO.saveAllInvoices(_)
        1 * transactionLogService.writeTransactionLog(_, "Success",
                "Save invoice in Bloxops to set to pending status", null, null, _, "")
        1 * fusebillAPIService.makePayment(paymentRequest) >> { throw new Exception() }
        1 * transactionLogService.writeTransactionLog(_, "Failure", "Recording Payment with Fusebill.", _, null, _, _)
        0 * transactionLogService.writeTransactionLog(_, _, _, _, _, _, _)

        assert response.successful == true
        assert response.error == null
        assert response.retryError == false
    }

    def 'Make payment successfully but unable to retrieve fusebill transaction id'() {
        given:'A Payment Request with correct parameters'
        List<InvoiceAllocation> paymentAllocations = [new InvoiceAllocation(amount: BigDecimal.valueOf(55)), new InvoiceAllocation(amount: BigDecimal.valueOf(55))]
        com.openrangelabs.services.billing.log.model.PublicIP publicIP = new com.openrangelabs.services.billing.log.model.PublicIP(ip: "1.1.1.1")
        PaymentRequest paymentRequest = new PaymentRequest(customerId: 111, amount: 110.00, cardholderName: "Ted Tedderson", nonce: "nonce",
                paymentAllocations:paymentAllocations, publicIpAddress:publicIP, privateIpAddress: "2.2.2.2", cardType: "Visa", cardLastFour: "1234" )
        SessionInfo sessionInfo = new SessionInfo()
        UserIdentificationResponse user = new UserIdentificationResponse()
        ResponseEntity<UserIdentificationResponse> userResponse = ResponseEntity.ok(user)
        List<BonitaUserMembership> membershipList = [new BonitaUserMembership(group_id: 111)]
        ResponseEntity<List<BonitaUserMembership>> membershipResponse = ResponseEntity.ok(membershipList)
        long companyId = 4225645
        BonitaGroup group = new BonitaGroup(path:"/openrangelabs/customers/", name: companyId+"")
        ResponseEntity<BonitaGroup> groupResponse = ResponseEntity.ok(group)
        BrainTreePaymentResponse brainTreePaymentResponse = new BrainTreePaymentResponse(transactionId: "222")
        InvoiceRecord invoice1 = new InvoiceRecord()
        InvoiceRecord invoice2 = new InvoiceRecord()
        FusebillPaymentResponse fusebillPaymentResponse = new FusebillPaymentResponse()
        ResponseEntity<FusebillPaymentResponse> paymentResponseEntity = ResponseEntity.ok(fusebillPaymentResponse)
        List<TransactionAllocation> allocations = [new TransactionAllocation(amount: BigDecimal.valueOf(55)), new TransactionAllocation(amount: BigDecimal.valueOf(55))]

        when:'makePayment service method call'
        def response = paymentService.makePayment(paymentRequest, sessionInfo)

        then:'Expect service calls and full payment summary as result'
        1 * bonitaAuthenticateAPIService.getUserIdentificationResponse(sessionInfo) >> userResponse
        1 * transactionLogService.writeTransactionLog(_, userResponse.getStatusCode().toString(),
                "Authorized with Bonita", null, user, _,_)
        1 * brainTreePaymentService.makePayment(_, _, _) >> brainTreePaymentResponse
        1 * billingBloxopsDAO.getInvoiceById(_, allocations.get(0).invoiceId) >> invoice1
        1 * billingBloxopsDAO.getInvoiceById(_, allocations.get(1).invoiceId) >> invoice2
        2 * transactionLogService.writeTransactionLog(_, "Success",
                "Get invoice from Middleware to set to pending status", null, null, _, "")
        1 * billingBloxopsDAO.saveAllInvoices(_)
        1 * transactionLogService.writeTransactionLog(_, "Success",
                "Save invoice in Bloxops to set to pending status", null, null, _, "")
        1 * fusebillAPIService.makePayment(paymentRequest) >> paymentResponseEntity
        1 * transactionLogService.writeTransactionLog(_, paymentResponseEntity.getStatusCode().toString(),
                "Getting back Fusebill Transaction Id", paymentRequest, paymentResponseEntity.getBody(), _,_)
        0 * transactionLogService.writeTransactionLog(_, _, _, _, _, _, _)

        assert response.isSuccessful()
        assert response.error == null
        assert response.retryError == false
    }

    def 'Make payment successfully but throws error on getting transaction back from fusebill'() {
        given:'A Payment Request with correct parameters'
        List<InvoiceAllocation> paymentAllocations = [new InvoiceAllocation(amount: BigDecimal.valueOf(55)), new InvoiceAllocation(amount: BigDecimal.valueOf(55))]
        com.openrangelabs.services.billing.log.model.PublicIP publicIP = new com.openrangelabs.services.billing.log.model.PublicIP(ip: "1.1.1.1")
        PaymentRequest paymentRequest = new PaymentRequest(customerId: 111, amount: 110.00, cardholderName: "Ted Tedderson", nonce: "nonce",
                paymentAllocations:paymentAllocations, publicIpAddress:publicIP, privateIpAddress: "2.2.2.2", cardType: "Visa", cardLastFour: "1234" )
        SessionInfo sessionInfo = new SessionInfo()
        UserIdentificationResponse user = new UserIdentificationResponse( companyId: "4225645" )
        ResponseEntity<UserIdentificationResponse> userResponse = ResponseEntity.ok(user)
        List<BonitaUserMembership> membershipList = [new BonitaUserMembership(group_id: 111)]
        ResponseEntity<List<BonitaUserMembership>> membershipResponse = ResponseEntity.ok(membershipList)
        long companyId = 4225645
        BonitaGroup group = new BonitaGroup(path:"/openrangelabs/customers/", name: companyId+"")
        ResponseEntity<BonitaGroup> groupResponse = ResponseEntity.ok(group)
        BrainTreePaymentResponse brainTreePaymentResponse = new BrainTreePaymentResponse(transactionId: "222")
        InvoiceRecord invoice1 = new InvoiceRecord()
        InvoiceRecord invoice2 = new InvoiceRecord()
        FusebillPaymentResponse fusebillPaymentResponse = new FusebillPaymentResponse(id:1234)
        ResponseEntity<FusebillPaymentResponse> paymentResponseEntity = ResponseEntity.ok(fusebillPaymentResponse)
        List<TransactionAllocation> allocations = [new TransactionAllocation(amount: BigDecimal.valueOf(55)), new TransactionAllocation(amount: BigDecimal.valueOf(55))]
        FusebillTransaction transaction = new FusebillTransaction(id: ""+fusebillPaymentResponse.getId(), invoiceAllocations: allocations)
        List<FusebillTransaction> transactions = [transaction]

        when:'makePayment service method call'
        def response = paymentService.makePayment(paymentRequest, sessionInfo)

        then:'Expect service calls and full payment summary as result'
        1 * bonitaAuthenticateAPIService.getUserIdentificationResponse(sessionInfo) >> userResponse
        1 * transactionLogService.writeTransactionLog(_, userResponse.getStatusCode().toString(),
                "Authorized with Bonita", null, user, _,_)
        1 * brainTreePaymentService.makePayment(_, _, _) >> brainTreePaymentResponse
        1 * billingBloxopsDAO.getInvoiceById(_, allocations.get(0).invoiceId) >> invoice1
        1 * billingBloxopsDAO.getInvoiceById(_, allocations.get(1).invoiceId) >> invoice2
        2 * transactionLogService.writeTransactionLog(_, "Success",
                "Get invoice from Middleware to set to pending status", null, null, _, "")
        1 * billingBloxopsDAO.saveAllInvoices(_) >> { throw new Exception() }
        1 * transactionLogService.writeTransactionLog(_, _,"Saving invoices as pending", null, null, _, "")
        1 * fusebillAPIService.makePayment(paymentRequest) >> paymentResponseEntity
        1 * transactionLogService.writeTransactionLog(_, paymentResponseEntity.getStatusCode().toString(),
                "Recorded Payment to Fusebill", _, null, _, _)
        1 * fusebillInvoiceAPIService.getTransaction(_, _, _, _) >> { throw new Exception() }
        1 * transactionLogService.writeTransactionLog(_, "Failure", _, _, null, _, _)
        0 * transactionLogService.writeTransactionLog(_, _, _, _, _, _, _)

        assert response.isSuccessful()
        assert response.error == null
        assert response.retryError == false
    }

    def 'Make payment successfully but transaction returned from fusebill empty'() {
        given:'A Payment Request with correct parameters'
        List<InvoiceAllocation> paymentAllocations = [new InvoiceAllocation(amount: BigDecimal.valueOf(55)), new InvoiceAllocation(amount: BigDecimal.valueOf(55))]
        com.openrangelabs.services.billing.log.model.PublicIP publicIP = new com.openrangelabs.services.billing.log.model.PublicIP(ip: "1.1.1.1")
        PaymentRequest paymentRequest = new PaymentRequest(customerId: 111, amount: 110.00, cardholderName: "Ted Tedderson", nonce: "nonce",
                paymentAllocations:paymentAllocations, publicIpAddress:publicIP, privateIpAddress: "2.2.2.2", cardType: "Visa", cardLastFour: "1234" )
        SessionInfo sessionInfo = new SessionInfo()
        UserIdentificationResponse user = new UserIdentificationResponse(companyId: "111")
        ResponseEntity<UserIdentificationResponse> userResponse = ResponseEntity.ok(user)
        List<BonitaUserMembership> membershipList = [new BonitaUserMembership(group_id: 111)]
        ResponseEntity<List<BonitaUserMembership>> membershipResponse = ResponseEntity.ok(membershipList)
        long companyId = 111
        BonitaGroup group = new BonitaGroup(path:"/openrangelabs/customers/", name: companyId+"")
        ResponseEntity<BonitaGroup> groupResponse = ResponseEntity.ok(group)
        BrainTreePaymentResponse brainTreePaymentResponse = new BrainTreePaymentResponse(transactionId: "222")
        InvoiceRecord invoice1 = new InvoiceRecord()
        InvoiceRecord invoice2 = new InvoiceRecord()
        FusebillPaymentResponse fusebillPaymentResponse = new FusebillPaymentResponse(id:1234)
        ResponseEntity<FusebillPaymentResponse> paymentResponseEntity = ResponseEntity.ok(fusebillPaymentResponse)
        List<TransactionAllocation> allocations = [new TransactionAllocation(amount: BigDecimal.valueOf(55)), new TransactionAllocation(amount: BigDecimal.valueOf(55))]
        ResponseEntity<List<FusebillTransaction>> transactionResponse = ResponseEntity.ok([new FusebillTransaction(id:444)])
        List<PaymentSchedule> paymentSchedules = [new PaymentSchedule(dueDateTimestamp:"2018-06-15T04:04:26")]
        FusebillInvoice fusebillInvoice = new FusebillInvoice(paymentSchedules:paymentSchedules, postedTimestamp: "2018-06-15T04:04:26", invoiceAmount: BigDecimal.valueOf(55), outstandingBalance: BigDecimal.valueOf(55))
        ResponseEntity<FusebillInvoice> invoiceResponse = ResponseEntity.ok(fusebillInvoice)

        when:'makePayment service method call'
        def response = paymentService.makePayment(paymentRequest, sessionInfo)

        then:'Expect service calls and full payment summary as result'
        1 * bonitaAuthenticateAPIService.getUserIdentificationResponse(sessionInfo) >> userResponse
        1 * transactionLogService.writeTransactionLog(_, userResponse.getStatusCode().toString(),
                "Authorized with Bonita", null, user, _,_)
        1 * brainTreePaymentService.makePayment(_, _, _) >> brainTreePaymentResponse
        1 * billingBloxopsDAO.getInvoiceById(companyId, allocations.get(0).invoiceId) >> invoice1
        1 * billingBloxopsDAO.getInvoiceById(companyId, allocations.get(1).invoiceId) >> invoice2
        2 * transactionLogService.writeTransactionLog(_, "Success",
                "Get invoice from Middleware to set to pending status", null, null, _, "")
        1 * billingBloxopsDAO.saveAllInvoices(_)
        1 * transactionLogService.writeTransactionLog(_, "Success",
                "Save invoice in Bloxops to set to pending status", null, null, _, "")
        1 * fusebillAPIService.makePayment(paymentRequest) >> paymentResponseEntity
        1 * transactionLogService.writeTransactionLog(_, paymentResponseEntity.getStatusCode().toString(),
                "Recorded Payment to Fusebill", _, null, _, _)
        1 * fusebillInvoiceAPIService.getTransaction(_, companyId, _, _) >> transactionResponse
        1 * transactionLogService.writeTransactionLog(_, "Failure", _, _, null, _, _)
        0 * transactionLogService.writeTransactionLog(_, _, _, _, _, _, _)

        assert response.isSuccessful()
        assert response.error == null
        assert response.retryError == false
    }

    def 'Make payment successfully but failed to save transaction to postgres'() {
        given:'A Payment Request with correct parameters'
        List<InvoiceAllocation> paymentAllocations = [new InvoiceAllocation(amount: BigDecimal.valueOf(55)), new InvoiceAllocation(amount: BigDecimal.valueOf(55))]
        com.openrangelabs.services.billing.log.model.PublicIP publicIP = new com.openrangelabs.services.billing.log.model.PublicIP(ip: "1.1.1.1")
        PaymentRequest paymentRequest = new PaymentRequest(customerId: 111, amount: 110.00, cardholderName: "Ted Tedderson", nonce: "nonce",
                paymentAllocations:paymentAllocations, publicIpAddress:publicIP, privateIpAddress: "2.2.2.2", cardType: "Visa", cardLastFour: "1234" )
        SessionInfo sessionInfo = new SessionInfo()
        UserIdentificationResponse user = new UserIdentificationResponse()
        ResponseEntity<UserIdentificationResponse> userResponse = ResponseEntity.ok(user)
        List<BonitaUserMembership> membershipList = [new BonitaUserMembership(group_id: 111)]
        ResponseEntity<List<BonitaUserMembership>> membershipResponse = ResponseEntity.ok(membershipList)
        long companyId = 4225645
        BonitaGroup group = new BonitaGroup(path:"/openrangelabs/customers/", name: companyId+"")
        ResponseEntity<BonitaGroup> groupResponse = ResponseEntity.ok(group)
        BrainTreePaymentResponse brainTreePaymentResponse = new BrainTreePaymentResponse(transactionId: "222")
        InvoiceRecord invoice1 = new InvoiceRecord()
        InvoiceRecord invoice2 = new InvoiceRecord()
        FusebillPaymentResponse fusebillPaymentResponse = new FusebillPaymentResponse(id:1234)
        ResponseEntity<FusebillPaymentResponse> paymentResponseEntity = ResponseEntity.ok(fusebillPaymentResponse)
        List<TransactionAllocation> allocations = [new TransactionAllocation(amount: BigDecimal.valueOf(55)), new TransactionAllocation(amount: BigDecimal.valueOf(55))]
        FusebillTransaction transaction = new FusebillTransaction(id: ""+fusebillPaymentResponse.getId(), invoiceAllocations: allocations)
        List<FusebillTransaction> transactions = [transaction]
        ResponseEntity<List<FusebillTransaction>> transactionResponse = ResponseEntity.ok(transactions)
        List<PaymentSchedule> paymentSchedules = [new PaymentSchedule(dueDateTimestamp:"2018-06-15T04:04:26")]
        FusebillInvoice fusebillInvoice = new FusebillInvoice(paymentSchedules:paymentSchedules, postedTimestamp: "2018-06-15T04:04:26", invoiceAmount: BigDecimal.valueOf(55), outstandingBalance: BigDecimal.valueOf(55))
        ResponseEntity<FusebillInvoice> invoiceResponse = ResponseEntity.ok(fusebillInvoice)

        when:'makePayment service method call'
        def response = paymentService.makePayment(paymentRequest, sessionInfo)

        then:'Expect service calls and full payment summary as result'
        1 * bonitaAuthenticateAPIService.getUserIdentificationResponse(sessionInfo) >> userResponse
        1 * transactionLogService.writeTransactionLog(_, userResponse.getStatusCode().toString(),
                "Authorized with Bonita", null, user, _,_)
        1 * brainTreePaymentService.makePayment(_, _, _) >> brainTreePaymentResponse
        1 * billingBloxopsDAO.getInvoiceById(_, allocations.get(0).invoiceId) >> invoice1
        1 * billingBloxopsDAO.getInvoiceById(_, allocations.get(1).invoiceId) >> invoice2
        2 * transactionLogService.writeTransactionLog(_, "Success",
                "Get invoice from Middleware to set to pending status", null, null, _, "")
        1 * billingBloxopsDAO.saveAllInvoices(_)
        1 * transactionLogService.writeTransactionLog(_, "Success",
                "Save invoice in Bloxops to set to pending status", null, null, _, "")
        1 * fusebillAPIService.makePayment(paymentRequest) >> paymentResponseEntity
        1 * transactionLogService.writeTransactionLog(_, paymentResponseEntity.getStatusCode().toString(),
                "Recorded Payment to Fusebill", _, null, _, _)
        1 * fusebillInvoiceAPIService.getTransaction(_, _, _, _) >> transactionResponse
        1 * transactionLogService.writeTransactionLog(_, transactionResponse.getStatusCode().toString(),"Got transaction from fusebill transactionId: "+transactions.get(0).id, null, _, _, _)
        1 * transactionBloxopsDAO.insertTransaction(transaction) >> {throw new Exception()}
        1 * transactionLogService.writeTransactionLog(_, _, "Record transaction",null, transaction, _, "")
        0 * transactionLogService.writeTransactionLog(_, _, _, _, _, _, _)

        assert response.isSuccessful()
        assert response.error == null
        assert response.retryError == false
    }

    def 'Make payment but failed to save transaction allocation to postgres'() {
        given:'A Payment Request with correct parameters'
        List<InvoiceAllocation> paymentAllocations = [new InvoiceAllocation(amount: BigDecimal.valueOf(55)), new InvoiceAllocation(amount: BigDecimal.valueOf(55))]
        com.openrangelabs.services.billing.log.model.PublicIP publicIP = new com.openrangelabs.services.billing.log.model.PublicIP(ip: "1.1.1.1")
        PaymentRequest paymentRequest = new PaymentRequest(customerId: 111, amount: 110.00, cardholderName: "Ted Tedderson", nonce: "nonce",
                paymentAllocations:paymentAllocations, publicIpAddress:publicIP, privateIpAddress: "2.2.2.2", cardType: "Visa", cardLastFour: "1234" )
        SessionInfo sessionInfo = new SessionInfo()
        UserIdentificationResponse user = new UserIdentificationResponse()
        ResponseEntity<UserIdentificationResponse> userResponse = ResponseEntity.ok(user)
        List<BonitaUserMembership> membershipList = [new BonitaUserMembership(group_id: 111)]
        ResponseEntity<List<BonitaUserMembership>> membershipResponse = ResponseEntity.ok(membershipList)
        long companyId = 4225645
        BonitaGroup group = new BonitaGroup(path:"/openrangelabs/customers/", name: companyId+"")
        ResponseEntity<BonitaGroup> groupResponse = ResponseEntity.ok(group)
        BrainTreePaymentResponse brainTreePaymentResponse = new BrainTreePaymentResponse(transactionId: "222")
        InvoiceRecord invoice1 = new InvoiceRecord()
        InvoiceRecord invoice2 = new InvoiceRecord()
        FusebillPaymentResponse fusebillPaymentResponse = new FusebillPaymentResponse(id:1234)
        ResponseEntity<FusebillPaymentResponse> paymentResponseEntity = ResponseEntity.ok(fusebillPaymentResponse)
        List<TransactionAllocation> allocations = [new TransactionAllocation(amount: BigDecimal.valueOf(55)), new TransactionAllocation(amount: BigDecimal.valueOf(55))]
        FusebillTransaction transaction = new FusebillTransaction(id: ""+fusebillPaymentResponse.getId(), invoiceAllocations: allocations)
        List<FusebillTransaction> transactions = [transaction]
        ResponseEntity<List<FusebillTransaction>> transactionResponse = ResponseEntity.ok(transactions)
        List<PaymentSchedule> paymentSchedules = [new PaymentSchedule(dueDateTimestamp:"2018-06-15T04:04:26")]
        FusebillInvoice fusebillInvoice = new FusebillInvoice(paymentSchedules:paymentSchedules, postedTimestamp: "2018-06-15T04:04:26", invoiceAmount: BigDecimal.valueOf(55), outstandingBalance: BigDecimal.valueOf(55))

        when:'makePayment service method call'
        def response = paymentService.makePayment(paymentRequest, sessionInfo)

        then:'Expect service calls and full payment summary as result'
        1 * bonitaAuthenticateAPIService.getUserIdentificationResponse(sessionInfo) >> userResponse
        1 * transactionLogService.writeTransactionLog(_, userResponse.getStatusCode().toString(),
                "Authorized with Bonita", null, user, _,_)
        1 * brainTreePaymentService.makePayment(_, _, _) >> brainTreePaymentResponse
        1 * billingBloxopsDAO.getInvoiceById(_, allocations.get(0).invoiceId) >> invoice1
        1 * billingBloxopsDAO.getInvoiceById(_, allocations.get(1).invoiceId) >> invoice2
        2 * transactionLogService.writeTransactionLog(_, "Success",
                "Get invoice from Middleware to set to pending status", null, null, _, "")
        1 * billingBloxopsDAO.saveAllInvoices(_)
        1 * transactionLogService.writeTransactionLog(_, "Success",
                "Save invoice in Bloxops to set to pending status", null, null, _, "")
        1 * fusebillAPIService.makePayment(paymentRequest) >> paymentResponseEntity
        1 * transactionLogService.writeTransactionLog(_, paymentResponseEntity.getStatusCode().toString(),
                "Recorded Payment to Fusebill", _, null, _, _)
        1 * fusebillInvoiceAPIService.getTransaction(_, _, _, _) >> transactionResponse
        1 * transactionLogService.writeTransactionLog(_, transactionResponse.getStatusCode().toString(),"Got transaction from fusebill transactionId: "+transactions.get(0).id, null, _, _, _)
        1 * transactionBloxopsDAO.insertTransaction(transaction)
        1 * transactionLogService.writeTransactionLog(_, "Success", "Recorded transaction in bloxops database table cust_prtl_transactions",
                null, null, _, "")
        1 * transactionBloxopsDAO.insertTransactionAllocationsBatch(allocations) >> {throw new Exception()}
        1 * transactionLogService.writeTransactionLog(_, _, "Record transaction allocation", null, null, _, "");
        0 * transactionLogService.writeTransactionLog(_, _, _, _, _, _, _)

        assert response.isSuccessful()
        assert response.error == null
        assert response.retryError == false
    }

    def 'Make payment but failed to retrieve invoice from fusebill'() {
        given:'A Payment Request with correct parameters'
        List<InvoiceAllocation> paymentAllocations = [new InvoiceAllocation(amount: BigDecimal.valueOf(55)), new InvoiceAllocation(amount: BigDecimal.valueOf(55))]
        com.openrangelabs.services.billing.log.model.PublicIP publicIP = new com.openrangelabs.services.billing.log.model.PublicIP(ip: "1.1.1.1")
        PaymentRequest paymentRequest = new PaymentRequest(customerId: 111, amount: 110.00, cardholderName: "Ted Tedderson", nonce: "nonce",
                paymentAllocations:paymentAllocations, publicIpAddress:publicIP, privateIpAddress: "2.2.2.2", cardType: "Visa", cardLastFour: "1234" )
        SessionInfo sessionInfo = new SessionInfo()
        UserIdentificationResponse user = new UserIdentificationResponse()
        ResponseEntity<UserIdentificationResponse> userResponse = ResponseEntity.ok(user)
        List<BonitaUserMembership> membershipList = [new BonitaUserMembership(group_id: 111)]
        ResponseEntity<List<BonitaUserMembership>> membershipResponse = ResponseEntity.ok(membershipList)
        long companyId = 4225645
        BonitaGroup group = new BonitaGroup(path:"/openrangelabs/customers/", name: companyId+"")
        ResponseEntity<BonitaGroup> groupResponse = ResponseEntity.ok(group)
        BrainTreePaymentResponse brainTreePaymentResponse = new BrainTreePaymentResponse(transactionId: "222")
        InvoiceRecord invoice1 = new InvoiceRecord()
        InvoiceRecord invoice2 = new InvoiceRecord()
        FusebillPaymentResponse fusebillPaymentResponse = new FusebillPaymentResponse(id:1234)
        ResponseEntity<FusebillPaymentResponse> paymentResponseEntity = ResponseEntity.ok(fusebillPaymentResponse)
        List<TransactionAllocation> allocations = [new TransactionAllocation(amount: BigDecimal.valueOf(55)), new TransactionAllocation(amount: BigDecimal.valueOf(55))]
        FusebillTransaction transaction = new FusebillTransaction(id: ""+fusebillPaymentResponse.getId(), invoiceAllocations: allocations)
        List<FusebillTransaction> transactions = [transaction]
        ResponseEntity<List<FusebillTransaction>> transactionResponse = ResponseEntity.ok(transactions)
        List<PaymentSchedule> paymentSchedules = [new PaymentSchedule(dueDateTimestamp:"2018-06-15T04:04:26")]
        FusebillInvoice fusebillInvoice = new FusebillInvoice(paymentSchedules:paymentSchedules, postedTimestamp: "2018-06-15T04:04:26", invoiceAmount: BigDecimal.valueOf(55), outstandingBalance: BigDecimal.valueOf(55))
        ResponseEntity<FusebillInvoice> invoiceResponse = ResponseEntity.ok(fusebillInvoice)

        when:'makePayment service method call'
        def response = paymentService.makePayment(paymentRequest, sessionInfo)

        then:'Expect service calls and full payment summary as result'
        1 * bonitaAuthenticateAPIService.getUserIdentificationResponse(sessionInfo) >> userResponse
        1 * transactionLogService.writeTransactionLog(_, userResponse.getStatusCode().toString(),
                "Authorized with Bonita", null, user, _,_)
        1 * brainTreePaymentService.makePayment(_, _, _) >> brainTreePaymentResponse
        1 * billingBloxopsDAO.getInvoiceById(_, allocations.get(0).invoiceId) >> invoice1
        1 * billingBloxopsDAO.getInvoiceById(_, allocations.get(1).invoiceId) >> invoice2
        2 * transactionLogService.writeTransactionLog(_, "Success",
                "Get invoice from Middleware to set to pending status", null, null, _, "")
        1 * billingBloxopsDAO.saveAllInvoices(_)
        1 * transactionLogService.writeTransactionLog(_, "Success",
                "Save invoice in Bloxops to set to pending status", null, null, _, "")
        1 * fusebillAPIService.makePayment(paymentRequest) >> paymentResponseEntity
        1 * transactionLogService.writeTransactionLog(_, paymentResponseEntity.getStatusCode().toString(),
                "Recorded Payment to Fusebill", _, null, _, _)
        1 * fusebillInvoiceAPIService.getTransaction(_, _, _, _) >> transactionResponse
        1 * transactionLogService.writeTransactionLog(_, transactionResponse.getStatusCode().toString(),"Got transaction from fusebill transactionId: "+transactions.get(0).id, null, _, _, _)
        1 * transactionBloxopsDAO.insertTransaction(transaction)
        1 * transactionLogService.writeTransactionLog(_, "Success", "Recorded transaction in bloxops database table cust_prtl_transactions",
                null, null, _, "")
        1 * transactionBloxopsDAO.insertTransactionAllocationsBatch(allocations)
        1 * transactionLogService.writeTransactionLog(_, "Success",
                "Recorded transaction allocation in bloxops database table cust_prtl_transaction_allocation",
                null, null, _, "")
        1 * fusebillInvoiceAPIService.getInvoiceResponse(_) >> { throw new Exception() }
        1 * transactionLogService.writeTransactionLog(_, "Failure", "Getting fusebill invoices", null, null, _, _)

        assert response.isSuccessful()
        assert response.error == null
        assert response.retryError == false
    }

    def 'Make payment but failed to save invoice'() {
        given:'A Payment Request with correct parameters'
        List<InvoiceAllocation> paymentAllocations = [new InvoiceAllocation(amount: BigDecimal.valueOf(55)), new InvoiceAllocation(amount: BigDecimal.valueOf(55))]
        com.openrangelabs.services.billing.log.model.PublicIP publicIP = new com.openrangelabs.services.billing.log.model.PublicIP(ip: "1.1.1.1")
        PaymentRequest paymentRequest = new PaymentRequest(customerId: 111, amount: 110.00, cardholderName: "Ted Tedderson", nonce: "nonce",
                paymentAllocations:paymentAllocations, publicIpAddress:publicIP, privateIpAddress: "2.2.2.2", cardType: "Visa", cardLastFour: "1234" )
        SessionInfo sessionInfo = new SessionInfo()
        UserIdentificationResponse user = new UserIdentificationResponse()
        ResponseEntity<UserIdentificationResponse> userResponse = ResponseEntity.ok(user)
        List<BonitaUserMembership> membershipList = [new BonitaUserMembership(group_id: 111)]
        ResponseEntity<List<BonitaUserMembership>> membershipResponse = ResponseEntity.ok(membershipList)
        long companyId = 4225645
        BonitaGroup group = new BonitaGroup(path:"/openrangelabs/customers/", name: companyId+"")
        ResponseEntity<BonitaGroup> groupResponse = ResponseEntity.ok(group)
        BrainTreePaymentResponse brainTreePaymentResponse = new BrainTreePaymentResponse(transactionId: "222")
        InvoiceRecord invoice1 = new InvoiceRecord()
        InvoiceRecord invoice2 = new InvoiceRecord()
        FusebillPaymentResponse fusebillPaymentResponse = new FusebillPaymentResponse(id:1234)
        ResponseEntity<FusebillPaymentResponse> paymentResponseEntity = ResponseEntity.ok(fusebillPaymentResponse)
        List<TransactionAllocation> allocations = [new TransactionAllocation(amount: BigDecimal.valueOf(55)), new TransactionAllocation(amount: BigDecimal.valueOf(55))]
        FusebillTransaction transaction = new FusebillTransaction(id: ""+fusebillPaymentResponse.getId(), invoiceAllocations: allocations)
        List<FusebillTransaction> transactions = [transaction]
        ResponseEntity<List<FusebillTransaction>> transactionResponse = ResponseEntity.ok(transactions)
        List<PaymentSchedule> paymentSchedules = [new PaymentSchedule(dueDateTimestamp:"2018-06-15T04:04:26")]
        FusebillInvoice fusebillInvoice = new FusebillInvoice(paymentSchedules:paymentSchedules, postedTimestamp: "2018-06-15T04:04:26", invoiceAmount: BigDecimal.valueOf(55), outstandingBalance: BigDecimal.valueOf(55))
        ResponseEntity<FusebillInvoice> invoiceResponse = ResponseEntity.ok(fusebillInvoice)

        when:'makePayment service method call'
        def response = paymentService.makePayment(paymentRequest, sessionInfo)

        then:'Expect service calls and full payment summary as result'
        1 * bonitaAuthenticateAPIService.getUserIdentificationResponse(sessionInfo) >> userResponse
        1 * transactionLogService.writeTransactionLog(_, userResponse.getStatusCode().toString(),
                "Authorized with Bonita", null, user, _,_)
        1 * brainTreePaymentService.makePayment(_, _, _) >> brainTreePaymentResponse
        1 * billingBloxopsDAO.getInvoiceById(_, allocations.get(0).invoiceId) >> invoice1
        1 * billingBloxopsDAO.getInvoiceById(_, allocations.get(1).invoiceId) >> invoice2
        2 * transactionLogService.writeTransactionLog(_, "Success",
                "Get invoice from Middleware to set to pending status", null, null, _, "")
        1 * billingBloxopsDAO.saveAllInvoices(_)
        1 * transactionLogService.writeTransactionLog(_, "Success",
                "Save invoice in Bloxops to set to pending status", null, null, _, "")
        1 * fusebillAPIService.makePayment(paymentRequest) >> paymentResponseEntity
        1 * transactionLogService.writeTransactionLog(_, paymentResponseEntity.getStatusCode().toString(),
                "Recorded Payment to Fusebill", _, null, _, _)
        1 * fusebillInvoiceAPIService.getTransaction(_, _, _, _) >> transactionResponse
        1 * transactionLogService.writeTransactionLog(_, transactionResponse.getStatusCode().toString(),"Got transaction from fusebill transactionId: "+transactions.get(0).id, null, _, _, _)
        1 * transactionBloxopsDAO.insertTransaction(transaction)
        1 * transactionLogService.writeTransactionLog(_, "Success", "Recorded transaction in bloxops database table cust_prtl_transactions",
                null, null, _, "")
        1 * transactionBloxopsDAO.insertTransactionAllocationsBatch(allocations)
        1 * transactionLogService.writeTransactionLog(_, "Success",
                "Recorded transaction allocation in bloxops database table cust_prtl_transaction_allocation",
                null, null, _, "")
        2 * fusebillInvoiceAPIService.getInvoiceResponse(_) >> invoiceResponse
        1 * billingBloxopsDAO.saveAllInvoices(_) >> { throw new Exception() }
        1 * transactionLogService.writeTransactionLog(_, _, "Recording fusebill invoices in bloxops database table cust_prtl_invoices", null, null, _, "");


        assert response.isSuccessful()
        assert response.error == null
        assert response.retryError == false
    }

    def 'Make payment successfully but failed to retrieve transaction for summary'() {
        given:'A Payment Request with correct parameters'
        List<InvoiceAllocation> paymentAllocations = [new InvoiceAllocation(amount: BigDecimal.valueOf(55)), new InvoiceAllocation(amount: BigDecimal.valueOf(55))]
        com.openrangelabs.services.billing.log.model.PublicIP publicIP = new com.openrangelabs.services.billing.log.model.PublicIP(ip: "1.1.1.1")
        PaymentRequest paymentRequest = new PaymentRequest(customerId: 111, amount: 110.00, cardholderName: "Ted Tedderson", nonce: "nonce",
                paymentAllocations:paymentAllocations, publicIpAddress:publicIP, privateIpAddress: "2.2.2.2", cardType: "Visa", cardLastFour: "1234" )
        SessionInfo sessionInfo = new SessionInfo()
        UserIdentificationResponse user = new UserIdentificationResponse(companyId: 111)
        ResponseEntity<UserIdentificationResponse> userResponse = ResponseEntity.ok(user)
        List<BonitaUserMembership> membershipList = [new BonitaUserMembership(group_id: 111)]
        ResponseEntity<List<BonitaUserMembership>> membershipResponse = ResponseEntity.ok(membershipList)
        long companyId = 4225645
        BonitaGroup group = new BonitaGroup(path:"/openrangelabs/customers/", name: companyId+"")
        ResponseEntity<BonitaGroup> groupResponse = ResponseEntity.ok(group)
        BrainTreePaymentResponse brainTreePaymentResponse = new BrainTreePaymentResponse(transactionId: "222")
        InvoiceRecord invoice1 = new InvoiceRecord()
        InvoiceRecord invoice2 = new InvoiceRecord()
        FusebillPaymentResponse fusebillPaymentResponse = new FusebillPaymentResponse(id:1234)
        ResponseEntity<FusebillPaymentResponse> paymentResponseEntity = ResponseEntity.ok(fusebillPaymentResponse)
        List<TransactionAllocation> allocations = [new TransactionAllocation(amount: BigDecimal.valueOf(55)), new TransactionAllocation(amount: BigDecimal.valueOf(55))]
        FusebillTransaction transaction = new FusebillTransaction(id: ""+fusebillPaymentResponse.getId(), invoiceAllocations: allocations)
        List<FusebillTransaction> transactions = [transaction]
        ResponseEntity<List<FusebillTransaction>> transactionResponse = ResponseEntity.ok(transactions)
        List<PaymentSchedule> paymentSchedules = [new PaymentSchedule(dueDateTimestamp:"2018-06-15T04:04:26")]
        FusebillInvoice fusebillInvoice = new FusebillInvoice(paymentSchedules:paymentSchedules, postedTimestamp: "2018-06-15T04:04:26", invoiceAmount: BigDecimal.valueOf(55), outstandingBalance: BigDecimal.valueOf(55))
        ResponseEntity<FusebillInvoice> invoiceResponse = ResponseEntity.ok(fusebillInvoice)

        when:'makePayment service method call'
        def response = paymentService.makePayment(paymentRequest, sessionInfo)

        then:'Expect service calls and full payment summary as result'
        1 * bonitaAuthenticateAPIService.getUserIdentificationResponse(sessionInfo) >> userResponse
        1 * transactionLogService.writeTransactionLog(_, userResponse.getStatusCode().toString(),
                "Authorized with Bonita", null, user, _,_)
        1 * brainTreePaymentService.makePayment(_, _, _) >> brainTreePaymentResponse
        1 * billingBloxopsDAO.getInvoiceById(_, allocations.get(0).invoiceId) >> invoice1
        1 * billingBloxopsDAO.getInvoiceById(_, allocations.get(1).invoiceId) >> invoice2
        2 * transactionLogService.writeTransactionLog(_, "Success",
                "Get invoice from Middleware to set to pending status", null, null, _, "")
        1 * billingBloxopsDAO.saveAllInvoices(_)
        1 * transactionLogService.writeTransactionLog(_, "Success",
                "Save invoice in Bloxops to set to pending status", null, null, _, "")
        1 * fusebillAPIService.makePayment(paymentRequest) >> paymentResponseEntity
        1 * transactionLogService.writeTransactionLog(_, paymentResponseEntity.getStatusCode().toString(),
                "Recorded Payment to Fusebill", _, null, _, _)
        1 * fusebillInvoiceAPIService.getTransaction(_, _, _, _) >> transactionResponse
        1 * transactionLogService.writeTransactionLog(_, transactionResponse.getStatusCode().toString(),"Got transaction from fusebill transactionId: "+transactions.get(0).id, null, _, _, _)
        1 * transactionBloxopsDAO.insertTransaction(transaction)
        1 * transactionLogService.writeTransactionLog(_, "Success", "Recorded transaction in bloxops database table cust_prtl_transactions",
                null, null, _, "")
        1 * transactionBloxopsDAO.insertTransactionAllocationsBatch(allocations)
        1 * transactionLogService.writeTransactionLog(_, "Success",
                "Recorded transaction allocation in bloxops database table cust_prtl_transaction_allocation",
                null, null, _, "")
        2 * fusebillInvoiceAPIService.getInvoiceResponse(_) >> invoiceResponse
        1 * billingBloxopsDAO.saveAllInvoices(_)
        1 * transactionLogService.writeTransactionLog(_, "Success", "Recorded invoices in bloxops database table cust_prtl_invoices",
                null, null, _, "")
        1 * billingBloxopsDAO.getTransactions(111, _) >> { throw new Exception() }
        1 * transactionLogService.writeTransactionLog(_, _, "Getting Transaction from Bloxops DB",null, null, _, "");

        assert response.isSuccessful()
        assert response.error == "Could not pull payment summary"
        assert response.retryError == false
    }

    def 'Make payment successfully but failed to retrieve transaction allocations for summary'() {
        given:'A Payment Request with correct parameters'
        List<InvoiceAllocation> paymentAllocations = [new InvoiceAllocation(amount: BigDecimal.valueOf(55)), new InvoiceAllocation(amount: BigDecimal.valueOf(55))]
        com.openrangelabs.services.billing.log.model.PublicIP publicIP = new com.openrangelabs.services.billing.log.model.PublicIP(ip: "1.1.1.1")
        PaymentRequest paymentRequest = new PaymentRequest(customerId: 111, amount: 110.00, cardholderName: "Ted Tedderson", nonce: "nonce",
                paymentAllocations:paymentAllocations, publicIpAddress:publicIP, privateIpAddress: "2.2.2.2", cardType: "Visa", cardLastFour: "1234" )
        SessionInfo sessionInfo = new SessionInfo()
        UserIdentificationResponse user = new UserIdentificationResponse()
        ResponseEntity<UserIdentificationResponse> userResponse = ResponseEntity.ok(user)
        List<BonitaUserMembership> membershipList = [new BonitaUserMembership(group_id: 111)]
        ResponseEntity<List<BonitaUserMembership>> membershipResponse = ResponseEntity.ok(membershipList)
        long companyId = 4225645
        BrainTreePaymentResponse brainTreePaymentResponse = new BrainTreePaymentResponse(transactionId: "222")
        InvoiceRecord invoice1 = new InvoiceRecord()
        InvoiceRecord invoice2 = new InvoiceRecord()
        FusebillPaymentResponse fusebillPaymentResponse = new FusebillPaymentResponse(id:1234)
        ResponseEntity<FusebillPaymentResponse> paymentResponseEntity = ResponseEntity.ok(fusebillPaymentResponse)
        List<TransactionAllocation> allocations = [new TransactionAllocation(amount: BigDecimal.valueOf(55)), new TransactionAllocation(amount: BigDecimal.valueOf(55))]
        FusebillTransaction transaction = new FusebillTransaction(id: ""+fusebillPaymentResponse.getId(), invoiceAllocations: allocations)
        List<FusebillTransaction> transactions = [transaction]
        ResponseEntity<List<FusebillTransaction>> transactionResponse = ResponseEntity.ok(transactions)
        List<PaymentSchedule> paymentSchedules = [new PaymentSchedule(dueDateTimestamp:"2018-06-15T04:04:26")]
        FusebillInvoice fusebillInvoice = new FusebillInvoice(paymentSchedules:paymentSchedules, postedTimestamp: "2018-06-15T04:04:26", invoiceAmount: BigDecimal.valueOf(55), outstandingBalance: BigDecimal.valueOf(55))
        ResponseEntity<FusebillInvoice> invoiceResponse = ResponseEntity.ok(fusebillInvoice)

        when:'makePayment service method call'
        def response = paymentService.makePayment(paymentRequest, sessionInfo)

        then:'Expect service calls and full payment summary as result'
        1 * bonitaAuthenticateAPIService.getUserIdentificationResponse(sessionInfo) >> userResponse
        1 * transactionLogService.writeTransactionLog(_, userResponse.getStatusCode().toString(),
                "Authorized with Bonita", null, user, _,_)
        1 * brainTreePaymentService.makePayment(_, _, _) >> brainTreePaymentResponse
        1 * billingBloxopsDAO.getInvoiceById(_, allocations.get(0).invoiceId) >> invoice1
        1 * billingBloxopsDAO.getInvoiceById(_, allocations.get(1).invoiceId) >> invoice2
        2 * transactionLogService.writeTransactionLog(_, "Success",
                "Get invoice from Middleware to set to pending status", null, null, _, "")
        1 * billingBloxopsDAO.saveAllInvoices(_)
        1 * transactionLogService.writeTransactionLog(_, "Success",
                "Save invoice in Bloxops to set to pending status", null, null, _, "")
        1 * fusebillAPIService.makePayment(paymentRequest) >> paymentResponseEntity
        1 * transactionLogService.writeTransactionLog(_, paymentResponseEntity.getStatusCode().toString(),
                "Recorded Payment to Fusebill", _, null, _, _)
        1 * fusebillInvoiceAPIService.getTransaction(_, _, _, _) >> transactionResponse
        1 * transactionLogService.writeTransactionLog(_, transactionResponse.getStatusCode().toString(),"Got transaction from fusebill transactionId: "+transactions.get(0).id, null, _, _, _)
        1 * transactionBloxopsDAO.insertTransaction(transaction)
        1 * transactionLogService.writeTransactionLog(_, "Success", "Recorded transaction in bloxops database table cust_prtl_transactions",
                null, null, _, "")
        1 * transactionBloxopsDAO.insertTransactionAllocationsBatch(allocations)
        1 * transactionLogService.writeTransactionLog(_, "Success",
                "Recorded transaction allocation in bloxops database table cust_prtl_transaction_allocation",
                null, null, _, "")
        2 * fusebillInvoiceAPIService.getInvoiceResponse(_) >> invoiceResponse
        1 * billingBloxopsDAO.saveAllInvoices(_)
        1 * transactionLogService.writeTransactionLog(_, "Success", "Recorded invoices in bloxops database table cust_prtl_invoices",
                null, null, _, "")
        1 * billingBloxopsDAO.getTransactions(111, _) >> transactions.get(0)
        1 * transactionLogService.writeTransactionLog(_, "Success", "Got Transaction from Bloxops DB", null, null, _, "")
        1 * billingBloxopsDAO.getTransactionAllocationsByTransaction(111, _) >> { throw new Exception()}
        1 * transactionLogService.writeTransactionLog(_, _,"Getting Transaction Allocation from bloxops database", null, null, _, "");
        1 * errorMessageService.getAuthenicateErrorMessage(1) >> "error"

        assert response.isSuccessful()
        assert response.error == "error"
        assert response.retryError == false
    }
}