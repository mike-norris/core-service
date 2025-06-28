package com.openrangelabs.services.documents;

import com.openrangelabs.services.documents.entity.Document;
import com.openrangelabs.services.documents.model.DocumentDeleteResponse;
import com.openrangelabs.services.documents.model.DocumentsUploadResponse;
import com.openrangelabs.services.log.LogResponseBodyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.URLConnection;
import java.util.List;

@RestController
@RequestMapping("/documents")

public class DocumentsController {
    @Value("${middlewareAdminKey}")
    String middlewareAdminKey;

    LogResponseBodyService logService;
    DocumentsService documentsService;

    @Autowired()
    public void documentsController(LogResponseBodyService logService,DocumentsService documentsService) {

        this.logService = logService;
        this.documentsService = documentsService;
    }

    
    @PostMapping(value = "/upload/{type}/{userId}")
    public DocumentsUploadResponse saveDocument(@RequestParam("file") MultipartFile file,@PathVariable("type") String documentServiceType,@PathVariable("userId") String userId) {

        return documentsService.saveDocument(userId, file, documentServiceType);
    }
    
    @DeleteMapping(value = "/delete/{document_id}")
    public DocumentDeleteResponse deleteDocument(@PathVariable("document_id") long documentId, HttpServletRequest request) {
        return documentsService.deleteDocument(documentId);
    }

    
    @GetMapping(value = "/admin/documents/{userId}")
    public List<Document> getDocumentsByUserId(@PathVariable("userId") long userId,
                                            HttpServletRequest request) {

        return (List<Document>) logService.logResponse(
                documentsService.getDocumentsByUserId(userId),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }
    
    @GetMapping(value = "/admin/{hashcode}")
    public void getDocumentAdmin(@PathVariable("hashcode") String hashcode,
                                         HttpServletRequest request,
                                         HttpServletResponse response) throws IOException {
        File file = documentsService.getDocumentByHashCode(hashcode);
        InputStream inputStream =null;
        if (file.exists()) {
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            if (mimeType == null) {
                //unknown mimetype so set the mimetype to application/octet-stream
                mimeType = "application/octet-stream";
            }
            response.setContentType(mimeType);

            response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));

            response.setContentLength((int) file.length());
            try(FileInputStream inputFile =new FileInputStream(file)){
                inputStream = new BufferedInputStream(inputFile);
                FileCopyUtils.copy(inputStream, response.getOutputStream());

                if(file.delete()) {
                    logService.logResponse(
                            ResponseEntity.ok(response.getStatus()),
                            request.getMethod(),
                            request.getPathInfo(),
                            ManagementFactory.getRuntimeMXBean().getName()
                    );
                }
            }finally{
                if(inputStream != null) {
                    inputStream.close();
                }
            }


        }
    }

    @GetMapping(value = "/document/{key}")
    public void getTicketAttachment(@PathVariable("key") String key,
                                    HttpServletRequest request,
                                    HttpServletResponse response) throws IOException {
        File file = documentsService.getDocumentByHashCode(key);
        InputStream inputStream =null;
        if (file.exists()) {
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            if (mimeType == null) {
                //unknown mimetype so set the mimetype to application/octet-stream
                mimeType = "application/octet-stream";
            }
            response.setContentType(mimeType);

            response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));

            response.setContentLength((int) file.length());
            try(FileInputStream inputFile =new FileInputStream(file)){
                inputStream = new BufferedInputStream(inputFile);
                FileCopyUtils.copy(inputStream, response.getOutputStream());
                if (file.delete()) {
                    logService.logResponse(
                            ResponseEntity.ok(response.getStatus()),
                            request.getMethod(),
                            request.getPathInfo(),
                            ManagementFactory.getRuntimeMXBean().getName()
                    );
                }
            }finally{
                if(inputStream != null) {
                    inputStream.close();
                }
            }

        }

    }

}
