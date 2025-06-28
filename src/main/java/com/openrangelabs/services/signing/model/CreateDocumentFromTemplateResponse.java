package com.openrangelabs.services.signing.model;

import lombok.Data;

@Data
public class CreateDocumentFromTemplateResponse {
    String documentId;
    String documentName;

    public CreateDocumentFromTemplateResponse(String documentName, String documentId) {
        this.documentId = documentId;
        this.documentName = documentName;
    }
}
