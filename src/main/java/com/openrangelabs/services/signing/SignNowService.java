package com.openrangelabs.services.signing;

import com.amazonaws.util.IOUtils;
import com.openrangelabs.services.authenticate.bonita.BonitaAuthenticateAPIService;
import com.openrangelabs.services.authenticate.model.SessionInfo;
import com.openrangelabs.services.datacenter.bloxops.dao.mapper.DatacenterBloxopsDAO;
import com.openrangelabs.services.datacenter.entity.Datacenter;
import com.openrangelabs.services.documents.DocumentsService;
import com.openrangelabs.services.log.model.LogRecord;
import com.openrangelabs.services.microsoft.graph.files.FilesService;
import com.openrangelabs.services.microsoft.graph.files.entity.OneDriveItem;
import com.openrangelabs.services.microsoft.graph.files.model.GetDriveItemsResponse;
import com.openrangelabs.services.organization.bloxops.dao.BloxopsOrganizationDAO;
import com.openrangelabs.services.organization.model.Organization;
import com.openrangelabs.services.organization.model.OrganizationUser;
import com.openrangelabs.services.roster.RosterUserService;
import com.openrangelabs.services.roster.entity.RosterUserDatacenter;
import com.openrangelabs.services.roster.model.RosterUserCreateDatacenterRequest;
import com.openrangelabs.services.roster.model.RosterUserCreateRequest;
import com.openrangelabs.services.signing.dao.SigningBloxopsDAO;

import com.openrangelabs.services.signing.model.*;
import com.openrangelabs.services.signing.modelNew.*;
import com.openrangelabs.services.signing.model.*;
import com.openrangelabs.services.signing.modelNew.*;
import com.openrangelabs.services.ticket.model.*;
import com.openrangelabs.services.signing.modelNew.DocumentInvite;
import com.openrangelabs.services.ticket.model.TicketUpdateRequest;
import com.openrangelabs.services.tools.Commons;
import com.openrangelabs.services.user.entity.UserShort;
import com.openrangelabs.services.user.profile.dao.UserBloxopsDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.*;

@Slf4j
@Service
public class SignNowService {
    SigningBloxopsDAO signingBloxopsDAO;
    SignNowAPIService signNowAPIService;
    DocumentsService dcBloxDocumentService;
    RosterUserService rosterUserService;
    FilesService filesService;
    UserBloxopsDAO userBloxopsDAO;
    BloxopsOrganizationDAO organizationDao;
    DatacenterBloxopsDAO datacenterBloxopsDAO;
    BonitaAuthenticateAPIService bonitaAuthenticateAPIService;
    RabbitTemplate rabbitTemplate;
    String DocumentInviteError = "Document invite removed from Signnow but still in db";

    @Value("${bonitaAdminUser}")
    String bonitaAdminUser;

    @Value("${bonitaAdminPassword}")
    String bonitaAdminPassword;

    @Value("${app.environment}")
    String appEnvironment;

    @Autowired
    public SignNowService(SigningBloxopsDAO signingBloxopsDAO, DocumentsService dcBloxDocumentService,
                          RosterUserService rosterUserService, FilesService filesService,
                          SignNowAPIService signNowAPIService, UserBloxopsDAO userBloxopsDAO,
                          BloxopsOrganizationDAO organizationDao,
                          DatacenterBloxopsDAO datacenterBloxopsDAO , BonitaAuthenticateAPIService bonitaAuthenticateAPIService,RabbitTemplate rabbitTemplate) {
        this.signingBloxopsDAO = signingBloxopsDAO;
        this.dcBloxDocumentService = dcBloxDocumentService;
        this.rosterUserService = rosterUserService;
        this.filesService = filesService;
        this.signNowAPIService = signNowAPIService;
        this.userBloxopsDAO = userBloxopsDAO;
        this.organizationDao = organizationDao;
        this.datacenterBloxopsDAO = datacenterBloxopsDAO;
        this.bonitaAuthenticateAPIService = bonitaAuthenticateAPIService;
        this.rabbitTemplate = rabbitTemplate;
    }

    public List<DocumentShort> getDocuments(String folderNameToGet, String accessToken) {
        GetFoldersResponse getFoldersResponse = signNowAPIService.getFolders(accessToken);
        List<Folder> folders = getFoldersResponse.getFolders();
        String documentFolderID = null;
        for (Folder folder : folders) {
            String folderName = folder.getName();
            if (folderName.equals(folderNameToGet)) {
                documentFolderID = folder.getId();
            }
        }
        log.info("Getting Documents from folder id " + documentFolderID);
        List<DocumentShort> documentShorts = signNowAPIService.getFolder(accessToken, documentFolderID);
        log.info("Got Documents from folder id " + documentShorts);
        return documentShorts;

    }

    public List<DocumentInvite> getDocumentInvites(String emailAddress) {
        String accessToken = signNowAPIService.loginUser();
        List<DocumentInvite> documentInvites = null;
        try {
            log.info("Retrieving Document invites for " + emailAddress);
            documentInvites = signingBloxopsDAO.findDocumentInvites(emailAddress);
            for (DocumentInvite documentInvite : documentInvites) {
                try {
                    DocumentFull document = signNowAPIService.getDocument(accessToken, documentInvite.getDocumentId());
                    log.info("Got Document " + document);
                    documentInvite.setFieldInvites(document.getFieldInvites());
                    documentInvite.setFields(document.getFields());
                    documentInvite.setDocumentName(document.getDocument_name());
                } catch (Exception e) {
                    log.warn(DocumentInviteError);
                }
            }
        } catch (Exception e) {
            log.error("Error retrieving Document invites for " + emailAddress);
            log.error(e.getMessage());
        }
        return documentInvites;
    }

    public CreateDocumentFromTemplateResponse createDocumentFromTemplate(CreateDocumentFromTemplateRequest request) {
        String accessToken = signNowAPIService.loginUser();
        try {
            log.info("Creating a document from template. Template Id:" + request.getTemplateName());
            String documentName = request.getDocumentName();
            String templateName = request.getTemplateName();
            String templateId = null;
            log.info("Found Template folder getting templates");
            List<DocumentShort> documentShorts = getDocuments("Templates", accessToken);
            for (DocumentShort template : documentShorts) {
                String templateNameFound = template.getDocumentName();
                if (templateName.equals(templateNameFound)) {
                    templateId = template.getDocumentId();
                }
            }
            DocumentTemplate documentTemplate = new DocumentTemplate();
            if (templateName.contains("DATA_CENTER_ACCESS_PREAUTH")) {
                documentTemplate = signingBloxopsDAO.getTemplateIdByName(templateName);
                templateId = documentTemplate.getTemplateId();
            }
            log.info("Attempting to make document from template id" + templateId);
            String documentId = signNowAPIService.createDocument(accessToken, templateId, documentName);
            if (templateName.contains("DATA_CENTER_ACCESS_PREAUTH")) {
                DocumentTemplateText documentTemplateText = signingBloxopsDAO.getDocumentTemplateText(documentTemplate.getTemplateId());
                documentTemplateText = documentTextProcess(documentTemplateText, request);
                documentTemplate.setDocumentText(documentTemplateText);
                signNowAPIService.addText(documentId, request, documentTemplate, accessToken);
            }
            if(request.getOrganizationId() != null) {
                LogRecord logRecord = new LogRecord(0, Integer.parseInt(String.valueOf(request.getOrganizationId())), "Document Created - " + request.getDocumentName(), "Document Create");
                rabbitTemplate.convertAndSend(Commons.LOGGING_EXCHANGE, "logs-system", logRecord);
            }

            return new CreateDocumentFromTemplateResponse(documentName, documentId);
        } catch (Exception e) {
            log.error("Error Creating a document from template. Template Id:" + request.getTemplateName());
            log.error(e.getMessage());
            return new CreateDocumentFromTemplateResponse(null, null);
        }
    }

    protected DocumentTemplateText documentTextProcess(DocumentTemplateText docTexts, CreateDocumentFromTemplateRequest request) {
        if(request.getEmail() != null ){
            docTexts.setRosterUser(rosterUserService.getDatacenterRosterUser(request.getEmail(), request.getOrganizationId()));
        }else{
            docTexts.setRosterUser(rosterUserService.getDatacenterRosterUserById(request.getRosterId()));
        }

       // docTexts.setSigner(userBloxopsDAO.getUserProfile(Integer.valueOf(String.valueOf(docTexts.getRosterUser().getUserId()))));
        docTexts.setOrganization(organizationDao.getOrganizationByOrganizationId(request.getOrganizationId()));
        if (null == request.getRequestorId()) {
            List<OrganizationUser> owner = organizationDao.getOrganizationOwners(request.getOrganizationId());
            docTexts.setInitiator(userBloxopsDAO.getUserProfile(Integer.valueOf(String.valueOf(owner.get(0).getUserId()))));
        }else{
            docTexts.setInitiator(userBloxopsDAO.getUserProfile(Integer.valueOf(String.valueOf(request.getRequestorId()))));
        }
        RosterUserDatacenter rosterUserDatacenter = rosterUserService.getRosterUserDatacenterByRosterUserId(docTexts.getRosterUser().getId(), request.getDatacenterId());
        if (null != rosterUserDatacenter) {
            request.setEscort(rosterUserDatacenter.isEscortRequired());
        }
        Datacenter datacenter = datacenterBloxopsDAO.getDatacenter(request.getDatacenterId());
        DocumentTemplateText newDocTemplateTexts = new DocumentTemplateText();
        newDocTemplateTexts.setDocumentName(request.getDocumentName());
        List<DocumentTemplateTextItems> texts = getTextsList(datacenter , docTexts, request);

        newDocTemplateTexts.setTexts(texts);
        return newDocTemplateTexts;
    }

    public List<DocumentTemplateTextItems> getTextsList(Datacenter datacenter, DocumentTemplateText docTexts, CreateDocumentFromTemplateRequest request){
        List<DocumentTemplateTextItems> texts = new ArrayList();
        for (DocumentTemplateTextItems dtti : docTexts.getTexts()) {
            try {
                texts.add(setData(dtti, datacenter, docTexts, request).get(0));
            } catch (Exception e) {
                log.warn("Tried to add document text but the line for "+dtti.getDataType()+" was not adjusted");
            }
        }
        return texts;
    }

    public List<DocumentTemplateTextItems> setData(DocumentTemplateTextItems dtti, Datacenter datacenter, DocumentTemplateText docTexts, CreateDocumentFromTemplateRequest request){
        List<DocumentTemplateTextItems> texts = new ArrayList();
        Organization organization = docTexts.getOrganization();

        if (dtti.getDataType().contains("signer_first_last_name")) {
            dtti.setData(docTexts.getRosterUser().getFirstName()+" "+docTexts.getRosterUser().getLastName());
            texts.add(dtti);
        }
        if (dtti.getDataType().contains("company_name")) {
            dtti.setData(docTexts.getOrganization().getName());
            texts.add(dtti);
        }
        if (dtti.getDataType().contains("position_title")) {
            dtti.setData("Data Center Technician");
            texts.add(dtti);
        }
        if (dtti.getDataType().contains("signer_email_address")) {
            String email = docTexts.getRosterUser().getEmailAddress();
            if (email.contains("@ATL") || email.contains("@BHM") || email.contains("@BMH") || email.contains("@CHA") ||
                    email.contains("@HSV") || email.contains("@GSC") || email.contains("@GSP")) {
                email = docTexts.getSigner().getEmailAddress();
            }
            dtti.setData(email);
            texts.add(dtti);
        }
        if (dtti.getDataType().contains("phone_number")) {
            String phoneNumber = " ";
            if(null != organization.getPhoneNumber()){
                phoneNumber = organization.getPhoneNumber();
            }
            dtti.setData(phoneNumber);
            texts.add(dtti);
        }
        if (dtti.getDataType().contains("company_phone_number")) {
            String companyPhoneNumber = " ";
            if(null != organization.getPhoneNumber()) {
                companyPhoneNumber = organization.getPhoneNumber();
            }
            dtti.setData(companyPhoneNumber);
            texts.add(dtti);
        }
        if (dtti.getDataType().contains("current_date")) {
            Date date = Calendar.getInstance(TimeZone.getTimeZone("America/New_York")).getTime();
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
            dtti.setData(dateFormat.format(date));
            texts.add(dtti);
        }
        if (dtti.getDataType().contains("created_date")) {
            Date date = Calendar.getInstance(TimeZone.getTimeZone("America/New_York")).getTime();
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
            dtti.setData(dateFormat.format(date));
            texts.add(dtti);
        }
        texts = setDatacenterData(dtti , datacenter ,texts);
        if (dtti.getDataType().contains("unescorted") && !request.getEscort()) {
            dtti.setData("X");
            texts.add(dtti);
        }
        if (dtti.getDataType().contains("escorted") && request.getEscort()) {
            dtti.setData("X");
            texts.add(dtti);
        }
        if (dtti.getDataType().contains("owner")) {
            UserShort initiatorUserShortObj = userBloxopsDAO.getPortalUserByEmailAddress(docTexts.getOrganization().getFusebillId(), docTexts.getInitiator().getEmailAddress());
            if (null != initiatorUserShortObj) {
                dtti.setData(initiatorUserShortObj.getFirstname()+" "+initiatorUserShortObj.getLastname());
                texts.add(dtti);
            }
        }
        if (dtti.getDataType().contains("pre_auth")) {
            dtti.setData("Authorized via www.openrangelabs.com");
            texts.add(dtti);
        }
        return texts;
    }

    public List<DocumentTemplateTextItems> setDatacenterData(DocumentTemplateTextItems dtti, Datacenter datacenter, List texts){
        if (dtti.getDataType().contains("atl") && datacenter.getName().toLowerCase().contains("atl")) {
            dtti.setData("Atlanta (ATL)");
            texts.add(dtti);
        }
        if (dtti.getDataType().contains("bhm") && datacenter.getName().toLowerCase().contains("bhm")) {
            dtti.setData("Birmingham (BMH)");
            texts.add(dtti);
        }
        if (dtti.getDataType().contains("cha") && datacenter.getName().toLowerCase().contains("cha")) {
            dtti.setData("Chattanooga (CHA)");
            texts.add(dtti);
        }
        if (dtti.getDataType().contains("hsv") && datacenter.getName().toLowerCase().contains("hsv")) {
            dtti.setData("Huntsville (HSV)");
            texts.add(dtti);
        }
        if (dtti.getDataType().contains("gsp") && datacenter.getName().toLowerCase().contains("gsp")) {
            dtti.setData("Greenville (GSP)");
            texts.add(dtti);
        }
        if (dtti.getDataType().contains("myr") && datacenter.getName().toLowerCase().contains("myr")) {
            dtti.setData("Myrtle Beach (MYR)");
            texts.add(dtti);
        }
        return texts;
    }

    public SigningResponse sendInvite(OneTemplateMultipleSigners oneTemplateMultipleSigners, String accessToken, String badgeDocumentType) {
        try {
            boolean success = signNowAPIService.sendInviteExecution(oneTemplateMultipleSigners, accessToken ,badgeDocumentType);
            if(success) {
                String documentType = oneTemplateMultipleSigners.getType() != null ? oneTemplateMultipleSigners.getType():"";
                List<Signer> signers = oneTemplateMultipleSigners.getSigners();
                Signer applicant = null;
                for (Signer signerItem : signers) {
                    if (signerItem.getRole().toLowerCase().contains("applicant")) {
                        applicant = signerItem;
                    }
                }
                saveDocumentInvite(documentType , applicant , oneTemplateMultipleSigners, signers, badgeDocumentType);
                return new SigningResponse(success, null);
            }else{
                return new SigningResponse(false, "Error executing document invite.");
            }
        } catch (Exception e) {
            log.error("Error Sending Document Signing invite.");
            log.error(e.getMessage());
            String message = String.format(
                    "Couldn't sent invitation request from %s to {%s, %s}. Document id: %s. Error: %s ",
                    oneTemplateMultipleSigners.getSenderEmail(),
                    oneTemplateMultipleSigners.getSigners().get(0).getEmail(),
                    oneTemplateMultipleSigners.getDocumentId(),
                    e.getMessage(),
                    e.getCause()
            );
            return new SigningResponse(false, message);
        }
    }

    public void saveDocumentInvite(String documentType, Signer applicant, OneTemplateMultipleSigners oneTemplateMultipleSigners, List<Signer> signers, String badgeDocumentType){
        if(documentType.equals("badge") && null != applicant) {
            signingBloxopsDAO.saveDocumentInvite(oneTemplateMultipleSigners.getDocumentId(),applicant.getEmail() , oneTemplateMultipleSigners.getTicketId());
        }else{
            if(documentType.equals("badge")) {
                //if preapproved only one signer will be sent
                if(badgeDocumentType.equals("preapproved")) {
                    signingBloxopsDAO.saveDocumentInvite(oneTemplateMultipleSigners.getDocumentId(), signers.get(0).getEmail(),oneTemplateMultipleSigners.getTicketId());
                }else{
                    signingBloxopsDAO.saveDocumentInvite(oneTemplateMultipleSigners.getDocumentId(), signers.get(1).getEmail(),oneTemplateMultipleSigners.getTicketId());
                }
            } else {
                signingBloxopsDAO.saveDocumentInvite(oneTemplateMultipleSigners.getDocumentId(), signers.get(0).getEmail(),oneTemplateMultipleSigners.getTicketId());
            }
        }
    }


    public SigningResponse sendMultipleInvites(MultipleTemplatesAndSigners multipleTemplatesAndSigners, String accessToken , String badgeDocumentType) {

        String documentGroupID = signNowAPIService.createDocumentGroup(accessToken, multipleTemplatesAndSigners);
        log.info("Creating document group ID: "+documentGroupID);
        Boolean success = signNowAPIService.sendGroupInvite(documentGroupID, multipleTemplatesAndSigners, accessToken, badgeDocumentType);
        if (success) {
            List<Signer> signers = multipleTemplatesAndSigners.getSigners();
            for (Signer signerItem : signers) {
                if (signerItem.getRole().toLowerCase().contains("applicant")) {
                    for (String docId : multipleTemplatesAndSigners.getDocumentIds()) {
                        signingBloxopsDAO.saveDocumentInvite(docId, signerItem.getEmail() , multipleTemplatesAndSigners.getTicketId());
                    }
                }
            }
            return new SigningResponse(true, null);
        } else {
            return new SigningResponse(false, "Error");
        }

    }

    public SigningResponse deleteDocument(String document_id, String accessToken) {

        try {
            log.info("Deleting Document from SignNow. Document ID: " + document_id);
            boolean success = signNowAPIService.deleteDocumentExecution(document_id, accessToken);
            if(success) {
                return new SigningResponse(true, null);
            }else{
                return new SigningResponse(false, "Error deleting document.");
            }
        } catch (Exception e) {
            log.error("Error Deleting Document from SignNow. Document ID: " + document_id);
            String message = String.format(
                    "Couldn't Delete document. Document id: %s. Error: %s ",
                    document_id,
                    e.getCause()
            );
            return new SigningResponse(false, message);

        }
    }


    public DownloadLinkResponse downloadDocument(String document_id, String accessToken) {
        try {
            log.info("Attempting to get Download link for Document ID: " + document_id);
            String link = signNowAPIService.requestDownloadLink(document_id, accessToken);

            log.info("Send log for document download");
            LogRecord logRecord = new LogRecord(0 , 0 ,"Downloaded Document from Signnow - " + document_id ,"Download Document");
            rabbitTemplate.convertAndSend(Commons.LOGGING_EXCHANGE, "logs-system",logRecord);

            return new DownloadLinkResponse(link, null);
        } catch (Exception e) {
            log.error("Error Attempting to get Download link for Document ID: " + document_id);
            String message = String.format(
                    "Couldn't retrieve document download link. Document id: %s. Error: %s ",
                    document_id,
                    e.getCause()
            );
            return new DownloadLinkResponse(null, message);

        }
    }


    public SigningResponse processDocument(ProcessDocumentRequest processDocumentRequest, String accessToken) {
        DownloadLinkResponse linkResponse = downloadDocument(processDocumentRequest.getDocumentId(), accessToken);
        try {
            log.info("Attempting to get file from url.");
            String url = linkResponse.getLink();
            byte[] b = IOUtils.toByteArray((new URL(url)).openStream());
            MultipartFile result = new MockMultipartFile("DataCenterDocument.pdf",
                    "DataCenterDocument.pdf", ".pdf", b);
            log.info("Saving file to the DB and S3.");
            dcBloxDocumentService.saveDocument(processDocumentRequest.getUserId(), result, "roster");
            Boolean archived = true;
            int saved = signingBloxopsDAO.updateDocumentInvite(processDocumentRequest.getDocumentId(), archived);
            if (saved == 1) {
                log.info("Document Invite Updated.");
            }
            log.info("Send log - process document");
            LogRecord logRecord = new LogRecord(Integer.parseInt(processDocumentRequest.getUserId()) , 0 ,"Document Processed saved and archived - " + processDocumentRequest.getDocumentId() ,"Process Document");
            rabbitTemplate.convertAndSend(Commons.LOGGING_EXCHANGE, "logs-system",logRecord);

            return new SigningResponse(true, null);
        } catch (IOException e) {
            log.error("Error processing the document and saving to DB and S3.");
            log.error(e.getMessage());
            return new SigningResponse(false, "Error downloading and saving document.");

        }
    }

    public SigningResponse checkIfDocumentSigned(String accessToken) {
        log.info("Checking document invites to see if fulfilled");

        try {
            List<DocumentInvite> documentInvites = signingBloxopsDAO.findAllDocumentInvites();
            for (DocumentInvite documentInvite : documentInvites) {
                try {
                    DocumentFull document = signNowAPIService.getDocument(accessToken, documentInvite.getDocumentId());
                    List<FieldInvite> fieldInvites = document.getFieldInvites();
                    String documentName = document.getDocument_name();

                    for (FieldInvite fieldInvite : fieldInvites) {
                        log.info("Checking if document is fulfilled and is an NDA " + documentName);
                        String fieldInviteStatus = fieldInvite.getStatus();
                        log.info(fieldInvite.toString());
                        if (fieldInviteStatus.equals("fulfilled") && documentName.contains("-NDA")) {
                           return addDocumentToOneDrive(documentName, accessToken, document);
                        }
                    }
                } catch (Exception e) {
                    signingBloxopsDAO.updateDocumentInvite(documentInvite.getDocumentId(), false);
                    log.warn(DocumentInviteError);
                }
            }
            return new SigningResponse(false, "No document Found");
        } catch (Exception e) {
            return new SigningResponse(null, "Error occurred.");
        }
    }

    private SigningResponse addDocumentToOneDrive(String documentName, String accessToken, DocumentFull document) {
        String folderName = documentName.substring(0, 3);
        log.info("Checking if document is in OneDrive");
        log.info("Getting company folder");
        GetDriveItemsResponse driveItemsResponse = filesService.getCompanyFolder(folderName);
        List<OneDriveItem> driveItemList = driveItemsResponse.getDriveItemList();
        boolean documentFound = false;

        for (OneDriveItem driveItem : driveItemList) {
            if (driveItem.getName().contains(documentName) || driveItem.getName().contains(documentName + ".pdf")) {
                log.info("Document Found.");
                documentFound = true;
            }
        }
        if (!documentFound) {
            log.info("Adding Document to One Drive.");
            DownloadLinkResponse downloadLinkResponse = downloadDocument(document.getId(), accessToken);
            filesService.uploadDriveItem(folderName, documentName, downloadLinkResponse.getLink());
            return new SigningResponse(true, "Document added to one drive.");
        } else {
            return new SigningResponse(true, null);
        }

    }

    @Scheduled(fixedRate = 1800000)
    public void checkCompletedForms(){
        log.info("Checking if document is signed and attach to linked ticket");
        String accessToken = signNowAPIService.loginUser();
        List<DocumentInvite> documentInvites = signingBloxopsDAO.findAllDocumentInvitesWithTickets();
        SessionInfo sessionInfo = bonitaAuthenticateAPIService.loginUser(bonitaAdminUser, bonitaAdminPassword);

        for (DocumentInvite documentInvite : documentInvites) {
            try {
                DocumentFull document = signNowAPIService.getDocument(accessToken, documentInvite.getDocumentId());
                List<FieldInvite> fieldInvites = document.getFieldInvites();
                for (FieldInvite fieldInvite : fieldInvites) {
                    log.info("Checking if document is fulfilled for ticket " + documentInvite.getTicketId());
                    String fieldInviteStatus = fieldInvite.getStatus();
                    log.error(fieldInvite.toString());
                    if (fieldInviteStatus.equals("fulfilled")) {
                        log.info("Document is fulfilled");
                        try {
                            updateTicket(documentInvite, accessToken, sessionInfo, document);
                        } catch (Exception e) {
                            log.error("Unable to update ticket with forms.");
                        }
                    }
                }
            } catch (Exception e) {
                log.warn(DocumentInviteError);
            }
        }

    }

    public void updateTicket(DocumentInvite documentInvite, String accessToken, SessionInfo sessionInfo, DocumentFull document) throws MalformedURLException {
        DownloadLinkResponse downloadLinkResponse = downloadDocument(documentInvite.getDocumentId() , accessToken);
        log.info("Getting ticket details");
        //TODO get ticket details from jira
        log.info("Update Ticket.");
        //TODO send update request to add documents to jira ticket
        log.info("Update document_invite to archived.");
        signingBloxopsDAO.updateDocumentInvite(document.getId(), true);

    }

    private String convertUrlToBase64(String urlString) throws MalformedURLException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String base64 ="";
        URL url = new URL(urlString);
        try (InputStream in = new BufferedInputStream(url.openStream())) {
            in.transferTo(Base64.getEncoder().wrap(out));
             base64 = out.toString(StandardCharsets.US_ASCII);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return base64;
    }

    public SigningResponse resendInvite(String fieldId, String accessToken) {
    SigningResponse signingResponse = new SigningResponse();
        try  {
            signingResponse.setSuccess(signNowAPIService.resendDocumentInvite(fieldId , accessToken));
        } catch (Exception e) {
            signingResponse.setSuccess(false);
            signingResponse.setError(e.getMessage());
            log.error(e.getMessage());
        }
        return signingResponse;
    }

    public SigningResponse resendDocumentGroupInvite(String documentGroupId,String inviteId , String accessToken) {
        SigningResponse signingResponse = new SigningResponse();
        try  {
            signingResponse.setSuccess(signNowAPIService.resendDocumentGroupInvite(documentGroupId ,inviteId , accessToken));
        } catch (Exception e) {
            signingResponse.setSuccess(false);
            signingResponse.setError(e.getMessage());
            log.error(e.getMessage());
        }
        return signingResponse;
    }

    @RabbitListener(queues = {Commons.ROSTER_DOCUMENTS_QUEUE}, containerFactory = "ListenerContainerFactory")
    public void sendRosterDocuments(RosterUserCreateRequest rosterUserCreateRequest) {
        log.info("Roster user documents received. " + rosterUserCreateRequest.toString());
        String BadgeTemplateName = "DATA_CENTER_ACCESS_PREAUTH";
        String AccessTemplateName = "ORL_Data_Center_Rules_and_Regulations";
        String accessToken = signNowAPIService.loginUser();
        try{
            if(rosterUserCreateRequest.getBadgeRequired()){
                log.info("Badge required - sending access and badge documents.");
                CreateDocumentFromTemplateRequest docTemplateAccess = createDocumentRequest(rosterUserCreateRequest , "access" , AccessTemplateName);
                CreateDocumentFromTemplateRequest docTemplateBadge = createDocumentRequest(rosterUserCreateRequest , "badge" , BadgeTemplateName);
                CreateDocumentFromTemplateResponse accessDoc = createDocumentFromTemplate(docTemplateAccess);
                CreateDocumentFromTemplateResponse badgeDoc = createDocumentFromTemplate(docTemplateBadge);
                MultipleTemplatesAndSigners multipleTemplatesAndSigners = createMultipleTemplate(rosterUserCreateRequest, accessDoc, badgeDoc);

                SigningResponse signingResponse  = sendMultipleInvites(multipleTemplatesAndSigners ,accessToken,"preapproved");
                if(signingResponse.getSuccess()){
                    sendTicketUpdate(rosterUserCreateRequest);
                }

            }else{
                log.info("No Badge required - sending access documents.");
                CreateDocumentFromTemplateRequest docTemplate = createDocumentRequest(rosterUserCreateRequest , "access" , AccessTemplateName);
                CreateDocumentFromTemplateResponse createDocumentFromTemplateResponse = createDocumentFromTemplate(docTemplate);
                OneTemplateMultipleSigners oneTemplateMultipleSigners = createSigningRequest(createDocumentFromTemplateResponse , rosterUserCreateRequest,"access");
                SigningResponse signingResponse = sendInvite(oneTemplateMultipleSigners, accessToken,"preapproved");
                if(signingResponse.getSuccess()){
                    sendTicketUpdate(rosterUserCreateRequest);
                }

            }
            log.info("Send log record.");
            LogRecord logRecord = new LogRecord(0 , Integer.parseInt(String.valueOf(rosterUserCreateRequest.getOrganizationId())) ,"Roster Documents Sent to - " + rosterUserCreateRequest.getEmailAddress() ,"Roster Documents");
            rabbitTemplate.convertAndSend(Commons.LOGGING_EXCHANGE, "logs-system",logRecord);

        }catch(Exception e){
            log.error(e.getMessage());
            rabbitTemplate.convertAndSend(Commons.SIGNNOW_DLX_EXCHANGE, "roster-user-documents-dlq",rosterUserCreateRequest);
        }
    }

    private MultipleTemplatesAndSigners createMultipleTemplate(RosterUserCreateRequest rosterUserCreateRequest, CreateDocumentFromTemplateResponse accessDoc, CreateDocumentFromTemplateResponse badgeDoc) {
        MultipleTemplatesAndSigners multipleTemplatesAndSigners = new MultipleTemplatesAndSigners();
        Set<RosterUserCreateDatacenterRequest> datacenters = rosterUserCreateRequest.getDatacenters();
        RosterUserCreateDatacenterRequest datacenter = datacenters.iterator().next();
        List<String> documentIds = new ArrayList<>();
        documentIds.add(accessDoc.getDocumentId());
        documentIds.add(badgeDoc.getDocumentId());
        List<String> datacenterList = new ArrayList<>();
        datacenterList.add(datacenter.getDatacenter());
        List<Signer> signers = new ArrayList<>();
        Signer signer = new Signer();
        signer.setEmail(rosterUserCreateRequest.getEmailAddress());
        signer.setOrder("1");
        signer.setExpiration_days("15");
        signer.setRole("Signer 1");

        Signer applicant = new Signer();
        applicant.setEmail(rosterUserCreateRequest.getEmailAddress());
        applicant.setOrder("1");
        applicant.setExpiration_days("15");
        applicant.setRole("Applicant");

        signers.add(signer);
        signers.add(applicant);

        String subject = appEnvironment.contains("production") ? datacenter.getDatacenter() + " - Datacenter Documents - "  + rosterUserCreateRequest.getEmailAddress()  :datacenter.getDatacenter() +" - DEV LAB Datacenter Documents - " + rosterUserCreateRequest.getEmailAddress();
        multipleTemplatesAndSigners.setEmail("itdev.openrangelabs@gmail.com");
        multipleTemplatesAndSigners.setSubject(subject);
        multipleTemplatesAndSigners.setMessage("Please fill in these forms.");
        multipleTemplatesAndSigners.setDocumentIds(documentIds);
        multipleTemplatesAndSigners.setDatacenters(datacenterList);
        multipleTemplatesAndSigners.setOrganizationId(rosterUserCreateRequest.getOrganizationId());
        multipleTemplatesAndSigners.setBadgeRequired(rosterUserCreateRequest.getBadgeRequired());
        multipleTemplatesAndSigners.setSigners(signers);
        multipleTemplatesAndSigners.setTicketId(Integer.parseInt(rosterUserCreateRequest.getTicketID()));

        return multipleTemplatesAndSigners;
    }

    private void sendTicketUpdate(RosterUserCreateRequest rosterUserCreateRequest) {
        TicketUpdateRequest ticketUpdateRequest = new TicketUpdateRequest();
        ticketUpdateRequest.setCaseid(String.valueOf(rosterUserCreateRequest.getTicketID()));
        ticketUpdateRequest.setComment("User created successfully.");
        ticketUpdateRequest.setStatus("New");
        ticketUpdateRequest.setOrganizationId(String.valueOf(rosterUserCreateRequest.getOrganizationId()));
        rabbitTemplate.convertAndSend(Commons.SUPPORT_EXCHANGE, "support-update", ticketUpdateRequest);
    }

    private OneTemplateMultipleSigners createSigningRequest(CreateDocumentFromTemplateResponse document, RosterUserCreateRequest rosterUserCreateRequest,String documentType ) {

        OneTemplateMultipleSigners oneTemplateMultipleSigners = new OneTemplateMultipleSigners();
        Set<RosterUserCreateDatacenterRequest> datacenters = rosterUserCreateRequest.getDatacenters();
        RosterUserCreateDatacenterRequest datacenter = datacenters.iterator().next();
        List<Signer> signers = new ArrayList<>();
        List<String> datacenterList = new ArrayList<>();
        datacenterList.add(datacenter.getDatacenter());
        Signer signer = new Signer();
        signer.setEmail(rosterUserCreateRequest.getEmailAddress());
        signer.setOrder("1");
        signer.setExpiration_days("15");
        if (documentType.contains("access")) {
            signer.setRole("Signer 1");
        } else {
            signer.setRole("Applicant");
        }
        signers.add(signer);
        oneTemplateMultipleSigners.setSigners(signers);
        oneTemplateMultipleSigners.setDocumentId(document.getDocumentId());
        oneTemplateMultipleSigners.setType(documentType);
        oneTemplateMultipleSigners.setSenderEmail("itdev.openrangelabs@gmail.com");
        oneTemplateMultipleSigners.setSubject(document.getDocumentName());
        oneTemplateMultipleSigners.setMessage("Please fill in this form.");
        oneTemplateMultipleSigners.setDatacenters(datacenterList);
        oneTemplateMultipleSigners.setBadgeRequired(rosterUserCreateRequest.getBadgeRequired());
        oneTemplateMultipleSigners.setTicketId(Integer.parseInt(rosterUserCreateRequest.getTicketID()));

        return oneTemplateMultipleSigners;
    }

    private CreateDocumentFromTemplateRequest createDocumentRequest(RosterUserCreateRequest rosterUserCreateRequest , String documentType ,String templateName) {
        String documentName = "";
        CreateDocumentFromTemplateRequest docTemplate = new CreateDocumentFromTemplateRequest();
        Set<RosterUserCreateDatacenterRequest> datacenters = rosterUserCreateRequest.getDatacenters();
        RosterUserCreateDatacenterRequest datacenter = datacenters.iterator().next();
        Datacenter dc = datacenterBloxopsDAO.getDatacenterByName(datacenter.getDatacenter());

        if(documentType.contains("access")){
            documentName = appEnvironment.contains("production") ? datacenter.getDatacenter() + " - Regulations - "  + rosterUserCreateRequest.getEmailAddress()  :datacenter.getDatacenter() +" - DEV LAB - Regulations - " + rosterUserCreateRequest.getEmailAddress();
        }else{
            documentName = appEnvironment.contains("production") ?datacenter.getDatacenter() + " - Badge Request - "  + rosterUserCreateRequest.getEmailAddress() :datacenter.getDatacenter() +" - DEV LAB - Badge Request - " + rosterUserCreateRequest.getEmailAddress();
        }

        docTemplate.setTemplateName(templateName);
        docTemplate.setDocumentName(documentName);
        docTemplate.setEmail(rosterUserCreateRequest.getEmailAddress());
        docTemplate.setOrganizationId(rosterUserCreateRequest.getOrganizationId());
        docTemplate.setDatacenterId(dc.getId());

        return docTemplate;
    }

}
