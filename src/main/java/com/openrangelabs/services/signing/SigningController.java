package com.openrangelabs.services.signing;

import com.openrangelabs.services.log.LogResponseBodyService;

import com.openrangelabs.services.signing.model.*;
import com.openrangelabs.services.signing.model.*;
import com.openrangelabs.services.signing.modelNew.DocumentFull;
import com.openrangelabs.services.signing.modelNew.DocumentInvite;
import com.openrangelabs.services.signing.modelNew.DocumentShort;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sign")
public class SigningController {

    LogResponseBodyService logService;
    SignNowAPIService signNowAPIService;
    SignNowService signNowService;

    @Autowired
    public SigningController(LogResponseBodyService logService, SignNowAPIService signNowAPIService,SignNowService signNowService) {

        this.logService = logService;
        this.signNowAPIService = signNowAPIService;
        this.signNowService = signNowService;
    }

    @GetMapping(value = "/documents")
    public List<DocumentShort> getDocuments(HttpServletRequest request)  {
        String accessToken = signNowAPIService.loginUser();
        List<DocumentShort> documents = signNowService.getDocuments("Documents",accessToken);
        return documents;
    }

    @GetMapping(value = "/document/check/")
    public SigningResponse checkIfDocumentSigned(  HttpServletRequest request)  {
        String accessToken = signNowAPIService.loginUser();
        return signNowService.checkIfDocumentSigned(accessToken);
    }

    @GetMapping(value = "/document/{document_id}")
    public DocumentFull getDocument(@PathVariable String document_id, HttpServletRequest request) {
        String accessToken = signNowAPIService.loginUser();
        DocumentFull document = signNowAPIService.getDocument(accessToken,document_id);
        return document;
    }

    @GetMapping(value = "/invites/{emailAddress}")
    public List<DocumentInvite> getDocumentInvites(@PathVariable String emailAddress, HttpServletRequest request)  {
       return  signNowService.getDocumentInvites(emailAddress);
    }

    @PostMapping(value = "/create/document")
    public CreateDocumentFromTemplateResponse createDocumentFromTemplate(@RequestBody CreateDocumentFromTemplateRequest params, HttpServletRequest request)  {
        return  signNowService.createDocumentFromTemplate(params);
    }

    @PostMapping(value = "/invite/preapproved")
    public SigningResponse sendInvitePA(@RequestBody OneTemplateMultipleSigners oneTemplateMultipleSigners, HttpServletRequest request) {
        String accessToken = signNowAPIService.loginUser();
        return signNowService.sendInvite(oneTemplateMultipleSigners, accessToken,"preapproved");
    }

    @PostMapping(value = "/invites/preapproved")
    public SigningResponse sendMultipleInvitesPA(@RequestBody MultipleTemplatesAndSigners multipleTemplatesAndSigners, HttpServletRequest request)  {
        String accessToken = signNowAPIService.loginUser();
        return signNowService.sendMultipleInvites(multipleTemplatesAndSigners ,accessToken,"preapproved");
    }

    @PostMapping(value = "/invite")
    public SigningResponse sendInvite(@RequestBody OneTemplateMultipleSigners oneTemplateMultipleSigners, HttpServletRequest request) {
        String accessToken = signNowAPIService.loginUser();
        return signNowService.sendInvite(oneTemplateMultipleSigners, accessToken,"unapproved");
    }

    @GetMapping(value = "/invite/resend/{fieldId}")
    public SigningResponse resendInvite(@PathVariable String fieldId, HttpServletRequest request) {
        String accessToken = signNowAPIService.loginUser();
        return signNowService.resendInvite(fieldId, accessToken);
    }

    @PostMapping(value = "/invites")
    public SigningResponse sendMultipleInvites(@RequestBody MultipleTemplatesAndSigners multipleTemplatesAndSigners, HttpServletRequest request)  {
        String accessToken = signNowAPIService.loginUser();
        return signNowService.sendMultipleInvites(multipleTemplatesAndSigners ,accessToken,"unapproved");
    }

    @GetMapping(value = "/invites/resend/{documentGroupId}/{inviteId}")
    public SigningResponse resendMultipleInvites(@PathVariable String documentGroupId, @PathVariable String inviteId, HttpServletRequest request)  {
        String accessToken = signNowAPIService.loginUser();
        return signNowService.resendDocumentGroupInvite(documentGroupId , inviteId ,accessToken);
    }

    @PostMapping(value = "/document/delete/{document_id}")
    public SigningResponse deleteDocument(@PathVariable String document_id, HttpServletRequest request)  {
        String accessToken = signNowAPIService.loginUser();
        return signNowService.deleteDocument(document_id ,accessToken);
    }
    
    @GetMapping(value = "/download/{document_id}")
    public DownloadLinkResponse downloadDocument(@PathVariable String document_id, HttpServletRequest request) {
        String accessToken = signNowAPIService.loginUser();
        return signNowService.downloadDocument(document_id,accessToken);
    }

    @PostMapping(value = "/process")
    public SigningResponse downloadDocument(@RequestBody ProcessDocumentRequest processDocumentRequest , HttpServletRequest request)  {
        String accessToken = signNowAPIService.loginUser();
        return signNowService.processDocument(processDocumentRequest,accessToken);
    }

}
