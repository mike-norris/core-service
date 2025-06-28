package com.openrangelabs.services.signing.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class MultipleTemplatesAndSigners {
    private List<String> documentIds = new ArrayList<>();
    private List<Signer> signers = new ArrayList<>();
    private String email;
    private String message;
    private Boolean badgeRequired;
    private String subject;
    private int ticketId;
    private long organizationId;
    private long companyId;
    private List<String> datacenters;

    /**
     * The companyId JSON parameter is still in the Bonita object to sign/invites
     * @param companyId
     */
    public void setCompanyId(Long companyId) {
        if (companyId.compareTo(0L) > 0) {
            this.setOrganizationId(companyId);
        }
    }

    public List<String> getDocumentIds() {
        return documentIds;
    }

    public void setDocumentIds(List<String> documentIds) {
        this.documentIds = documentIds;
    }

    public List<Signer> getSigners() {
        return signers;
    }

    public void setSigners(List<Signer> signers) {
        this.signers = signers;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
