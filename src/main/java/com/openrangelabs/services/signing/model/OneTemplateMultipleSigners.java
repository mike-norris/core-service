package com.openrangelabs.services.signing.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OneTemplateMultipleSigners {
    private String senderEmail;
    private String documentId;
    private String type;
    private List<String> datacenters;
    private Boolean badgeRequired;
    private String subject;
    private String message;
    private long organizationId;
    private int ticketId;
    private List<Signer> signers = new ArrayList<>();

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Signer> getSigners() {
        return signers;
    }

    public void setSigners(List<Signer> signers) {
        this.signers = signers;
    }
}
