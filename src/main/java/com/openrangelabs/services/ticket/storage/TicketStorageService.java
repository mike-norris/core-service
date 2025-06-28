package com.openrangelabs.services.ticket.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Slf4j
@Service
public class TicketStorageService {

    S3Service s3Service;
    String TICKET_ATTACHMENT_BUCKET = "Ticket_Attachments";
    @Autowired
    public TicketStorageService(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    public String saveTicketAttachment(String userName, InputStream upload, String contentType, Long size) {
        try {
            return s3Service.saveFile(userName, upload, contentType, size,TICKET_ATTACHMENT_BUCKET);
        } catch (Exception e) {
            if (e.getMessage().contains("Unable")) {
                log.info(e.getMessage());
            }
            return "";
        }
    }
}
