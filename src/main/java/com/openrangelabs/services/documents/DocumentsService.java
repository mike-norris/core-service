package com.openrangelabs.services.documents;

import com.amazonaws.services.s3.model.S3Object;
import com.openrangelabs.services.documents.dao.DocumentsBloxopsDAO;
import com.openrangelabs.services.documents.entity.Document;
import com.openrangelabs.services.documents.model.DocumentDeleteResponse;
import com.openrangelabs.services.documents.model.DocumentDetails;
import com.openrangelabs.services.documents.model.DocumentResponse;
import com.openrangelabs.services.documents.model.DocumentsUploadResponse;
import com.openrangelabs.services.log.model.LogRecord;
import com.openrangelabs.services.roster.bloxops.dao.RosterBloxopsDAO;
import com.openrangelabs.services.roster.entity.RosterUser;
import com.openrangelabs.services.ticket.storage.S3Service;
import com.openrangelabs.services.tools.Commons;
import com.openrangelabs.services.user.profile.dao.UserBloxopsDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class DocumentsService {
    String ticketAttachments = "Ticket_Attachments";
    String datacenterDocuments = "Datacenter_Documents";
    UserBloxopsDAO userBloxopsDAO;
    DocumentsBloxopsDAO documentsBloxopsDAO;
    S3Service s3Service;
    RosterBloxopsDAO rosterBloxopsDAO;
    RabbitTemplate rabbitTemplate;

    @Value("${storageFileLocation}")
    String storageFileLocation;


    @Autowired
    DocumentsService(RosterBloxopsDAO rosterBloxopsDAO, UserBloxopsDAO userBloxopsDAO,DocumentsBloxopsDAO documentsBloxopsDAO, S3Service s3Service , RabbitTemplate rabbitTemplate) {

        this.rosterBloxopsDAO = rosterBloxopsDAO;
        this.userBloxopsDAO = userBloxopsDAO;
        this.documentsBloxopsDAO = documentsBloxopsDAO;
        this.s3Service = s3Service;
        this.rabbitTemplate = rabbitTemplate;
    }

    public DocumentsUploadResponse saveDocument(String userId, MultipartFile file , String documentServiceType) {
        String container = getContainer(documentServiceType);
        RosterUser rosterUser ;

        try {
            log.info("Finding roster user by users ID.");
             rosterUser = rosterBloxopsDAO.findByUserId(Integer.parseInt(userId));
        }catch (Exception e) {
            log.error("Could not find roster user by their user Id.");
            return new DocumentsUploadResponse(false,"Could not find roster user by their user Id.");
        }

        try {
            byte[] fileBytes = file.getBytes();
            String fileName= formatFilename(file.getOriginalFilename(),rosterUser.getOrganizationId(),getFileExtension(file.getContentType()));
            DocumentDetails documentDetails = getDocumentDetails(fileBytes ,fileName);
            Long rosterId = rosterUser.getId();
            String mimeType = documentDetails.getMimetype();
            InputStream fileStream =documentDetails.getFileStream();
            int size =documentDetails.getSize();
            String username = rosterUser.getFirstName()+" " +rosterUser.getLastName();
            String key = saveDocumentS3(container , username, fileStream, mimeType, (long) size);
            String documentKey = UUID.randomUUID().toString();
            String s3Container = key;
            String documentLocation = container +"/"+fileName;
            log.info("Writing document to DB.");
            saveFileToFolder(fileBytes , fileName ,storageFileLocation);
            documentsBloxopsDAO.writeDocumentToDB( fileName ,documentKey,documentLocation,mimeType, true,false,rosterUser.getOrganizationId(), Integer.parseInt(userId),rosterId ,s3Container);
            sendLogRecord(userId , rosterUser.getOrganizationId()  , "Saving document - " + fileName);

        } catch (Exception e) {
            log.error("Could not save document.");
            return new DocumentsUploadResponse(false,"Could not save document.");
        }
        return new DocumentsUploadResponse(true,null);

    }

    public void sendLogRecord(String userId, long organizationId , String description){
        LogRecord logRecord = new LogRecord(Integer.valueOf(userId) ,Integer.valueOf(String.valueOf(organizationId)), description, "Document");
        rabbitTemplate.convertAndSend(Commons.LOGGING_EXCHANGE, "logs-user", logRecord);
        rabbitTemplate.convertAndSend(Commons.LOGGING_EXCHANGE, "logs-system", logRecord);
    }

    private String getFileExtension(String contentType) {
        String extension ;
        switch (contentType.toLowerCase())
        {
            case "application/msword":
                extension = ".doc";
                break;
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                extension = ".docx";
                break;
            case "application/vnd.ms-excel":
                extension = ".xls";
                break;
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
                extension = ".xlsx";
                break;
            case "image/jpeg":
                extension = ".jpg";
                break;
            case "image/png":
                extension = ".png";
                break;
            case "image/pdf":
                extension = ".pdf";
                break;
            default:
                extension = ".pdf";
                break;
        }
        return extension;
    }

    private void saveFileToFolder(byte[] fileBytes, String fileName, String location) throws IOException {
        File file = new File(location, fileName);
        try (OutputStream output = new FileOutputStream(file)) {
            output.write(fileBytes);
        }
    }


    public String formatFilename(String originalFilename , Long organizationId , String extension){
    String filename = originalFilename.replaceAll("\\s","");
    SimpleDateFormat dateTime = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return organizationId + "-"+filename +"-" + dateTime.format(new Date())+ extension;
    }

    public String getContainer(String documentServiceType){
        String type =null;
        if(documentServiceType.contains("roster")){
           type = "Datacenter_Documents";
        }else{
           type = "Ticket_Attachments";
        }
        return type;
    }

    public DocumentDetails getDocumentDetails(byte[] fileBytes, String fileName){
        InputStream fileStream =null;
        String mimeType ="";
        int size =0;
        try{
            log.info("Getting file details");
            fileStream = new ByteArrayInputStream(fileBytes);
            File targetFile = new File("/tmp/"+fileName);
            URLConnection connect = targetFile.toURI().toURL().openConnection();
            mimeType = connect.getContentType();
            size = fileBytes.length;


        } catch (Exception e) {
            log.error("Error Getting file details");
        }
        return new DocumentDetails(fileStream,mimeType,size);

    }
    public String saveDocumentS3(String container ,String username,InputStream fileStream,String mimeType, Long size ){
        String key ="";
        try{
            if(container.contains(datacenterDocuments)) {
                log.info("Saving data center document to s3.");

                key = s3Service.saveFile(username, fileStream, mimeType, size ,datacenterDocuments);
            }else{
                log.info("Saving ticket attachment to s3.");
                key = s3Service.saveFile(username, fileStream, mimeType, size ,ticketAttachments);
            }

        } catch (Exception e) {
            log.error("Error saving document to s3.");
        }
        return key;
    }

    public List<Document> getDocumentsByUserId(long userId) {
        List<Document> documents =null;
        try{
            log.error("Getting all documents for user id:" + userId);
            documents = documentsBloxopsDAO.findDocumentsByUserId(userId);
            sendLogRecord(String.valueOf(userId), 0  , "Getting all documents for user id:" + userId );
        } catch (Exception e) {
            log.error("Error getting documents by user id.");
        }
        return documents;
    }

    public File getDocumentByHashCode(String key) {
        log.info("Attempting to retrieve document."+key);
        String s3Key  = documentsBloxopsDAO.getS3Key(key);
        S3Object s3Object = s3Service.getFile(s3Key,datacenterDocuments);
        String extension = getFileExtension(s3Object.getObjectMetadata().getContentType());
        String tempLocation = System.getProperty("java.io.tmpdir");
        File targetFile = new File(tempLocation+"/output"+extension);

        try (OutputStream outStream = new FileOutputStream(targetFile);) {

            InputStream inputStream = s3Object.getObjectContent();

            DocumentResponse documentResponse = new DocumentResponse();
            documentResponse.inputStream = inputStream;

            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }

            return targetFile;
        } catch (Exception e) {
            log.error("Unable to retrieve document");
            DocumentResponse response2 = new DocumentResponse();
            response2.setError("Unable to retrieve document");
            return null;
        }
    }

    public DocumentDeleteResponse deleteDocument( Long documentId) {
        try {
            log.info("Attempting to delete document.");
            Date date = new Date();
            documentsBloxopsDAO.updateDocumentToDeleted(documentId ,date);
            LogRecord logRecord = new LogRecord(0 , 0, "Document delete requested - document ID " + documentId, "Document");
            rabbitTemplate.convertAndSend(Commons.LOGGING_EXCHANGE, "logs-system", logRecord);
            return new DocumentDeleteResponse(true, null);
        } catch (Exception e) {
            log.error("Unable to delete document");
            return new DocumentDeleteResponse(false, "Unable to delete document");
        }
    }
}
