package com.openrangelabs.services.signing.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SendSingleDocumentRequest {
    private List<Signer> signers = new ArrayList<>();
    private String subject;
    private String message;
    private String templateName;
    private String type;
    private String documentName;
    private String senderEmail;

}
