package com.openrangelabs.services.signing;

import com.openrangelabs.middleware.config.WebClientConfig;
import com.openrangelabs.services.signing.model.*;
import com.openrangelabs.services.signing.modelNew.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import com.openrangelabs.services.documents.DocumentsService;
import com.openrangelabs.services.microsoft.graph.files.FilesService;
import com.openrangelabs.services.roster.RosterUserService;
import com.openrangelabs.services.roster.entity.RosterUser;
import com.openrangelabs.services.signing.dao.SigningBloxopsDAO;
import com.openrangelabs.services.signing.model.*;
import com.openrangelabs.services.signing.modelNew.*;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;

@Slf4j
@Service
public class SignNowAPIService {

    private final WebClient webClient;
    
    @Value("${signNowApiUrl}")
    private String apiUrl;

    @Value("${signNowAuthKey}")
    private String signNowAuthKey;

    @Value("${signNowAdminEmail}")
    private String signNowAdminEmail;

    @Value("${signNowAdminPassword}")
    private String signNowAdminPassword;

    String contentType = "Content-Type";
    String authorization = "Authorization";
    String BEARER = "Bearer ";
    String documentURL = "/document/";
    String document_id = "document_id";
    String field_name = "field_name";
    String prefilled_text = "prefilled_text";
    String subject = "subject";
    String message = "message";
    String email = "email";
    String ORDER = "order";
    String EXPIRATION_DAYS = "expiration_days";
    String REMINDER = "reminder";
    String ACTION = "action";
    String ALLOW_REASSIGN = "allow_reassign";
    String ROLE_NAME = "role_name";
    String GET_FOLDERS_INFO = "Attempting to get user folders";
    String ACCEPT_HEADER = "application/json";
    String PREAPPROVED = "preapproved";

    SigningBloxopsDAO signingBloxopsDAO;
    DocumentsService dcBloxDocumentService;
    RosterUserService rosterUserService;
    FilesService filesService;

    @Autowired
    public SignNowAPIService(SigningBloxopsDAO signingBloxopsDAO, DocumentsService dcBloxDocumentService,
                             RosterUserService rosterUserService, FilesService filesService) {
        this.webClient = WebClientConfig.build(apiUrl);
        this.signingBloxopsDAO = signingBloxopsDAO;
        this.dcBloxDocumentService = dcBloxDocumentService;
        this.rosterUserService = rosterUserService;
        this.filesService = filesService;
    }

    public String loginUser() {
        log.info("Attempting to log user into sign now.");
        MultiValueMap<String, String> payload = new LinkedMultiValueMap<String, String>();
        payload.add("username", signNowAdminEmail);
        payload.add("password", signNowAdminPassword);
        payload.add("grant_type", "password");
        payload.add("scope", "*");

        try {
            ResponseEntity<String> response = webClient.post()
                    .uri(apiUrl + "/oauth2/token")
                    .header(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.MULTIPART_FORM_DATA))
                    .header(HttpHeaders.AUTHORIZATION, "Basic "+signNowAuthKey)
                    .bodyValue(payload)
                    .retrieve()
                    .toEntity(String.class)
                    .block();
            assert response != null;
            if (null != response.getBody()) {
                return extractToken(response.getBody());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public String extractToken(String response) {
        String s = StringUtils.substringBetween(response, "access_token", "refresh_token");
        s = StringUtils.substringBetween(s, ":", ",");
        s = StringUtils.replaceChars(s, "\"", "");
        return s;
    }

    public GetFoldersResponse getFolders(String accessToken) {
        log.info(GET_FOLDERS_INFO);
        try {
            ResponseEntity<GetFoldersResponse> response = webClient.get()
                    .uri(apiUrl + "/user/folder")
                    .header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                    .retrieve()
                    .toEntity(GetFoldersResponse.class)
                    .block();
            assert response != null;
            if (null != response.getBody()) {
                return response.getBody();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public List<DocumentShort> getFolder(String accessToken, String folderId) {
        log.info(GET_FOLDERS_INFO);
        try {
            ResponseEntity<GetFoldersResponse> response = webClient.get()
                    .uri(apiUrl + "/user/folder/" + folderId + "?exclude_documents_relations=true")
                    .header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                    .retrieve()
                    .toEntity(GetFoldersResponse.class)
                    .block();
            assert response != null;
            if (null != response.getBody()) {
                return response.getBody().getDocuments();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    public String addText(String documentId, CreateDocumentFromTemplateRequest request, DocumentTemplate documentTemplate, String accessToken) {
        log.info("Add text to document.");
        log.info(GET_FOLDERS_INFO);
        Map<String, Object> payload = new HashMap<>();
        payload.put("document_name", request.getDocumentName());
        try {
            payload.put("texts", documentTemplate.getDocumentText().getTexts());
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        try {
            ResponseEntity<CreateDocumentTextsResponse> response = webClient.put()
                    .uri(apiUrl + documentURL + documentId)
                    .header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                    .header(HttpHeaders.ACCEPT, ACCEPT_HEADER)
                    .header(HttpHeaders.CONTENT_TYPE, ACCEPT_HEADER)
                    .bodyValue(payload)
                    .retrieve()
                    .toEntity(CreateDocumentTextsResponse.class)
                    .block();
            assert response != null;
            if (null != response.getBody()) {
                CreateDocumentTextsResponse createDocumentTextsResponse = response.getBody();
                if(null != createDocumentTextsResponse) {
                    return createDocumentTextsResponse.getId();
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "";
    }

    public String createDocument(String accessToken, String templateId, String documentName) {
        log.info("Creating Document from template.");
        Map<String, Object> payload = new HashMap<>();
        payload.put("document_name", documentName);
        try {
            ResponseEntity<CreateDocumentResponse> response = webClient.post()
                    .uri(apiUrl + "/template/" + templateId + "/copy")
                    .header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                    .header(HttpHeaders.ACCEPT, ACCEPT_HEADER)
                    .header(HttpHeaders.CONTENT_TYPE, ACCEPT_HEADER)
                    .bodyValue(payload)
                    .retrieve()
                    .toEntity(CreateDocumentResponse.class)
                    .block();
            assert response != null;
            if (null != response.getBody()) {
                CreateDocumentResponse createDocumentResponse = response.getBody();
                if(null != createDocumentResponse) {
                    return createDocumentResponse.getId();
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "";
    }

    public String createDocumentGroup(String accessToken, MultipleTemplatesAndSigners multipleTemplatesAndSigners) {
        log.info("Creating Document Group from documents.");

        List<String> documentIdsSent = multipleTemplatesAndSigners.getDocumentIds();
        Map<String, Object> payload = new HashMap<>();
        payload.put("document_ids", new ArrayList<>(documentIdsSent));
        payload.put("group_name", multipleTemplatesAndSigners.getSubject());

        try {
            ResponseEntity<CreateDocumentResponse> response = webClient.post()
                    .uri(apiUrl + "/documentgroup")
                    .header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                    .header(HttpHeaders.ACCEPT, ACCEPT_HEADER)
                    .header(HttpHeaders.CONTENT_TYPE, ACCEPT_HEADER)
                    .bodyValue(payload)
                    .retrieve()
                    .toEntity(CreateDocumentResponse.class)
                    .block();
            assert response != null;
            if (null != response.getBody().getErrors()) {
                log.warn(response.getBody().getErrors().get(0).getMessage());
                DocumentWithGroup documentWithGroup = this.getGroupFromDocument(accessToken, documentIdsSent.get(0));
                if (null != documentWithGroup.getDocumentGroupInfo()) {
                    log.info("We found a group for the documents with the id: "+documentWithGroup.getDocumentGroupInfo().getGroupId());
                    return documentWithGroup.getDocumentGroupInfo().getGroupId();
                }
            }
            if( null != response.getBody()) {
                CreateDocumentResponse createDocumentResponse = response.getBody();
                if (null != createDocumentResponse) {
                    return createDocumentResponse.getId();
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            DocumentWithGroup documentWithGroup = this.getGroupFromDocument(accessToken, documentIdsSent.get(0));
            if (null != documentWithGroup.getDocumentGroupInfo()) {
                log.info("We found a group for the documents with the id: "+documentWithGroup.getDocumentGroupInfo().getGroupId());
                return documentWithGroup.getDocumentGroupInfo().getGroupId();
            }
        }
        return "";
    }

    public DocumentFull getDocument(String accessToken, String documentID) {

        ParameterizedTypeReference<DocumentFull> responseType = new ParameterizedTypeReference<>() {};

        try {
            return webClient.get()
                    .uri(apiUrl + documentURL + documentID)
                    .header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                    .header(HttpHeaders.ACCEPT, ACCEPT_HEADER)
                    .header(HttpHeaders.CONTENT_TYPE, ACCEPT_HEADER)
                    .retrieve()
                    .toEntity(responseType)
                    .block().getBody();
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("Error Getting document details from SignNow by "+ document_id +":" + documentID);
        }
        return new DocumentFull();
    }

    public DocumentWithGroup getGroupFromDocument(String accessToken, String documentID) {
        log.info("Getting document details from SignNow by "+ document_id +":" + documentID);
        log.info(GET_FOLDERS_INFO);
        ParameterizedTypeReference<DocumentWithGroup> responseType = new ParameterizedTypeReference<>() {};

        try {
            return webClient.get()
                    .uri(apiUrl + documentURL + documentID)
                    .header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                    .header(HttpHeaders.ACCEPT, ACCEPT_HEADER)
                    .header(HttpHeaders.CONTENT_TYPE, ACCEPT_HEADER)
                    .retrieve()
                    .toEntity(responseType)
                    .block().getBody();
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("Error Getting document details from SignNow by "+ document_id +":" + documentID);
        }
        return new DocumentWithGroup();
    }

    public boolean sendInviteExecution(OneTemplateMultipleSigners oneTemplateMultipleSigners, String accessToken , String badgeDocumentType) {
        log.info("Attempting to send document invite.");

        JSONObject sendObject = new JSONObject();
        List<Signer> signers = oneTemplateMultipleSigners.getSigners();
        Signer applicant = null;
        for (Signer signerItem : signers) {
            if (signerItem.getRole().toLowerCase().contains("applicant")) {
                applicant = signerItem;
            }
        }

        String documentType = oneTemplateMultipleSigners.getType();
        if(documentType.equals("badge") && null != applicant){
            try {
                log.info("attempting to pre fill fields for badge document");
                 setPreFilledFields(oneTemplateMultipleSigners.getDocumentId(), applicant.getEmail(), oneTemplateMultipleSigners.getOrganizationId(), oneTemplateMultipleSigners.getDatacenters(),accessToken ,oneTemplateMultipleSigners.getBadgeRequired());
            }catch(Exception e){
                log.error("Error could not pre fill fields for badge document.");
                log.error(e.getMessage());
            }
        }

        sendObject.put(document_id, oneTemplateMultipleSigners.getDocumentId());
        sendObject.put(subject, oneTemplateMultipleSigners.getSubject());
        sendObject.put(message, oneTemplateMultipleSigners.getMessage());
        sendObject.put("from", oneTemplateMultipleSigners.getSenderEmail());

        ArrayList signersToAdd = new ArrayList();
        for (Signer signer : signers) {
            JSONObject newSigner = new JSONObject();
            newSigner.put(email, signer.getEmail());
            newSigner.put("role", signer.getRole());
            newSigner.put(ORDER, signer.getOrder());
            //if badge form and already preapproved do not add approver
            if(badgeDocumentType.equals(PREAPPROVED) && signer.getRole().equals("Approver")){
                //dont add signer in this case
            }else {
                signersToAdd.add(newSigner);
            }
        }
        sendObject.put("to", signersToAdd);

        ParameterizedTypeReference<DocumentInviteResponse> responseType = new ParameterizedTypeReference<DocumentInviteResponse>() {};
        ResponseEntity<DocumentInviteResponse> response = webClient.post()
                .uri(apiUrl + documentURL + oneTemplateMultipleSigners.getDocumentId() + "/invite")
                .header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, ACCEPT_HEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(sendObject)
                .retrieve()
                .toEntity(responseType)
                .block();
        if (null != response.getBody() && null != response.getBody().getStatus() && response.getBody().getStatus().equals("success")){
            return true;
        }else{
            return false;
        }
    }

    private void setPreFilledFields(String documentId, String email, long companyId, List <String> datacenters, String accessToken ,Boolean badgeRequired) {
        log.info("Starting to prefill fields for sign now");
        ArrayList fieldList = new ArrayList<>();
        try {
            RosterUser rosterUser = rosterUserService.getDatacenterRosterUser(email, companyId);
            String name = rosterUser.getFirstName() + " " + rosterUser.getLastName();
            JSONObject applicantName = new JSONObject();
            try {
                applicantName.put(field_name,"Name");
                applicantName.put(prefilled_text,name);
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            String company = "";
            JSONObject companyName = new JSONObject();
            try {
                if(rosterUser.getCompanyName() != null){
                    company = rosterUser.getCompanyName();
                }
                companyName.put(field_name,"Company");
                companyName.put(prefilled_text,company);
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            String position = "Employee";
            JSONObject positionName = new JSONObject();
            try {
                if (rosterUser.getPositionTitle() != null) {
                    position = rosterUser.getPositionTitle();
                }
                positionName.put(field_name, "Position");
                positionName.put(prefilled_text, position);
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            JSONObject applicantEmail = new JSONObject();
            try {
                applicantEmail.put(field_name,"Email");
                applicantEmail.put(prefilled_text,email);
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            JSONObject Atlanta = new JSONObject();
            try {
                Atlanta.put(field_name,"Atlanta");
                Atlanta.put(prefilled_text,datacenters.contains("ATL")? "X":"" );
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            JSONObject Birmingham = new JSONObject();
            try {
                Birmingham.put(field_name,"Birmingham");
                Birmingham.put(prefilled_text,datacenters.contains("BHM")? "X":"" );
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            JSONObject Huntsville = new JSONObject();
            try {
                Huntsville.put(field_name,"Huntsville");
                Huntsville.put(prefilled_text,datacenters.contains("HSV")? "X":"" );
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            JSONObject Chattanooga = new JSONObject();
            try {
                Chattanooga.put(field_name,"Chattanooga");
                Chattanooga.put(prefilled_text,datacenters.contains("CHA")? "X":"" );
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            JSONObject Greenville = new JSONObject();
            try {
                Greenville.put(field_name,"Greenville");
                Greenville.put(prefilled_text,datacenters.contains("GSV")? "X":"" );
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            JSONObject Unescorted = new JSONObject();
            try {
                Unescorted.put(field_name,"Unescorted");
                Unescorted.put(prefilled_text,badgeRequired? "X":"" );
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            JSONObject Escorted = new JSONObject();
            try {
                Escorted.put(field_name,"Escorted");
                Escorted.put(prefilled_text,!badgeRequired? "":"X" );
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            log.info("Attempting to pre fill fields on document.");
            HttpHeaders header = new HttpHeaders();
            header.add(authorization, BEARER + accessToken);
            JSONObject sendObject = new JSONObject();
            try {
                fieldList.add(applicantName);
                fieldList.add(companyName);
                fieldList.add(positionName);
                fieldList.add(applicantEmail);
                fieldList.add(Atlanta);
                fieldList.add(Birmingham);
                fieldList.add(Huntsville);
                fieldList.add(Chattanooga);
                fieldList.add(Greenville);
                fieldList.add(Unescorted);
                fieldList.add(Escorted);
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            sendObject.put("fields", fieldList);

            ResponseEntity<String> responseString = webClient.put()
                    .uri(apiUrl + "/v2/documents/" + documentId + "/prefill-texts")
                    .header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                    .header(HttpHeaders.CONTENT_TYPE, ACCEPT_HEADER)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(sendObject)
                    .retrieve()
                    .toEntity(String.class)
                    .block();
            log.info(responseString.getBody());
        } catch (Exception e1) {
            log.error(e1.getMessage());
        }
    }

    public boolean sendGroupInvite(String groupId, MultipleTemplatesAndSigners multipleTemplatesAndSigners, String accessToken, String badgeDocumentType) {
        // Set this temporarily
        badgeDocumentType = PREAPPROVED;

        log.info("Attempting to send document invite.");

        // Use specific types for ArrayLists
        List<JSONObject> inviteStepsAll = new ArrayList<>();
        JSONObject sendObject = new JSONObject();
        JSONObject inviteStepsOne = new JSONObject();
        JSONObject inviteStepsTwo = new JSONObject();
        List<Signer> signers = multipleTemplatesAndSigners.getSigners();

        // Initializing inviteEmailsStep1 with proper type
        List<JSONObject> inviteEmailsStep1 = new ArrayList<>();

        Signer signer1 = signers.get(0);
        Signer signer2 = signers.get(1);
        Signer signer3 = null;

        // Attempt to pre-fill fields for badge document
        try {
            log.info("Attempting to pre-fill fields for badge document");
            List<String> datacenters = multipleTemplatesAndSigners.getDatacenters();
            setPreFilledFields(
                    multipleTemplatesAndSigners.getDocumentIds().get(1),
                    signer2.getEmail(),
                    multipleTemplatesAndSigners.getOrganizationId(),
                    datacenters,
                    accessToken,
                    multipleTemplatesAndSigners.getBadgeRequired()
            );
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error: Could not pre-fill fields for badge document.");
            log.error(e.getMessage());
        }

        // Create JSON for signer1 and signer2 emails
        JSONObject signer1Email = createSignerEmail(signer1, multipleTemplatesAndSigners);
        JSONObject signer2Email = createSignerEmail(signer2, multipleTemplatesAndSigners);

        inviteEmailsStep1.add(signer1Email);
        inviteEmailsStep1.add(signer2Email);

        // Create actions for step 1
        List<JSONObject> inviteActionsStep1 = new ArrayList<>();
        inviteActionsStep1.add(createSignerAction(signer1, multipleTemplatesAndSigners.getDocumentIds().get(0), "sign"));
        inviteActionsStep1.add(createSignerAction(signer2, multipleTemplatesAndSigners.getDocumentIds().get(1), "sign"));

        inviteStepsOne.put(ORDER, 1);
        inviteStepsOne.put("invite_emails", inviteEmailsStep1);
        inviteStepsOne.put("invite_actions", inviteActionsStep1);

        // Step 2
        List<JSONObject> inviteEmailsStep2 = new ArrayList<>();
        if (!badgeDocumentType.equals(PREAPPROVED) && signer3 != null) {
            inviteEmailsStep2.add(createSignerEmail(signer3, multipleTemplatesAndSigners));
        }

        List<JSONObject> inviteActionsStep2 = new ArrayList<>();
        if (!badgeDocumentType.equals(PREAPPROVED) && signer3 != null) {
            inviteActionsStep2.add(createSignerAction(signer3, multipleTemplatesAndSigners.getDocumentIds().get(1), "sign"));
        }

        inviteStepsTwo.put(ORDER, 2);
        inviteStepsTwo.put("invite_emails", inviteEmailsStep2);
        inviteStepsTwo.put("invite_actions", inviteActionsStep2);

        // Completion emails
        List<JSONObject> completionEmails = new ArrayList<>();
        for (Signer signer : signers) {
            JSONObject newSigner = new JSONObject();
            newSigner.put(email, signer.getEmail());
            newSigner.put(message, multipleTemplatesAndSigners.getMessage());
            newSigner.put(subject, multipleTemplatesAndSigners.getSubject());
            completionEmails.add(newSigner);
        }

        inviteStepsAll.add(inviteStepsOne);
        if (!badgeDocumentType.equals(PREAPPROVED)) {
            inviteStepsAll.add(inviteStepsTwo);
        }

        sendObject.put("invite_steps", inviteStepsAll);
        sendObject.put("completion_emails", completionEmails);

        // Make the request
        ResponseEntity<InviteDocumentGroupResponse> response = webClient.post()
                .uri(apiUrl + "/documentgroup/" + groupId + "/groupinvite")
                .header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, ACCEPT_HEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(sendObject)
                .retrieve()
                .toEntity(InviteDocumentGroupResponse.class)
                .block();

        if (response.getBody() != null) {
            InviteDocumentGroupResponse inviteDocumentGroupResponse = response.getBody();
            return inviteDocumentGroupResponse.getId() != null;
        }
        return false;
    }

    // Helper method to create signer email JSON
    private JSONObject createSignerEmail(Signer signer, MultipleTemplatesAndSigners multipleTemplatesAndSigners) {
        JSONObject signerEmail = new JSONObject();
        signerEmail.put(email, signer.getEmail());
        signerEmail.put(message, multipleTemplatesAndSigners.getMessage());
        signerEmail.put(EXPIRATION_DAYS, signer.getExpiration_days());
        signerEmail.put(REMINDER, 3);
        signerEmail.put(subject, multipleTemplatesAndSigners.getSubject());
        return signerEmail;
    }

    // Helper method to create signer action JSON
    private JSONObject createSignerAction(Signer signer, String documentId, String action) {
        JSONObject signerAction = new JSONObject();
        signerAction.put(email, signer.getEmail());
        signerAction.put(document_id, documentId);
        signerAction.put(ACTION, action);
        signerAction.put(ALLOW_REASSIGN, 0);
        signerAction.put(ROLE_NAME, signer.getRole());
        signerAction.put(subject, "subject role 1");
        return signerAction;
    }


    public boolean deleteDocumentExecution(String document_id, String accessToken) {
        log.info("Deleting Document :" + document_id);

        ResponseEntity<DeleteDocumentResponse> responseString = webClient.delete()
                .uri(apiUrl + documentURL + document_id)
                .header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, ACCEPT_HEADER)
                .retrieve()
                .toEntity(DeleteDocumentResponse.class)
                .block();
        DeleteDocumentResponse deleteDocumentResponse = responseString.getBody();

        if(null!= deleteDocumentResponse && deleteDocumentResponse.getResult() != null) {
            return deleteDocumentResponse.getResult().equals("success");
        }else {
            return false;
        }
    }
    public String requestDownloadLink(String document_id, String accessToken) {
        log.info("Getting Document download link document id :" + document_id);
        ResponseEntity<DownloadLink> responseString = webClient.post()
                .uri(apiUrl + documentURL + document_id + "/download/link")
                .header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, ACCEPT_HEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(DownloadLink.class)
                .block();
        DownloadLink link = responseString.getBody();
        if(link != null){
            return link.getLink();
        }else{
            return "";
        }
    }

    public boolean resendDocumentInvite(String fieldId, String accessToken) {
        log.info("Resending document Invite - field id :" + fieldId);

        ResponseEntity<DeleteDocumentResponse> responseString = webClient.put()
                .uri(apiUrl + "/fieldinvite/" +fieldId+ "/resend")
                .header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, ACCEPT_HEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(DeleteDocumentResponse.class)
                .block();
        DeleteDocumentResponse documentInviteResponse = responseString.getBody();
        if(null != documentInviteResponse && null != documentInviteResponse.getResult() && documentInviteResponse.getResult().equals("success")){
            return true;
        }else{
            return false;
        }

    }

    public boolean resendDocumentGroupInvite(String documentGroupId,String inviteId, String accessToken) {
        log.info("Resending document group Invite - id :" + documentGroupId);

        ResponseEntity<DeleteDocumentResponse> responseString = webClient.post()
                .uri(apiUrl + "/documentgroup/" + documentGroupId + "/groupinvite/"+ inviteId + "/resendinvites")
                .header(HttpHeaders.AUTHORIZATION, BEARER + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, ACCEPT_HEADER)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new Object())
                .retrieve()
                .toEntity(DeleteDocumentResponse.class)
                .block();

        DeleteDocumentResponse documentInviteResponse = responseString.getBody();
        if(null != documentInviteResponse && null != documentInviteResponse.getResult() && documentInviteResponse.getResult().equals("success")){
            return true;
        }else{
            return false;
        }

    }
}
