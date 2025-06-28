package com.openrangelabs.services.documents.model;

import lombok.Data;

@Data
public class DocumentDeleteResponse {
    Boolean deleted;
    String error;

    public DocumentDeleteResponse(Boolean deleted, String error) {
        this.deleted = deleted;
        this.error = error;
    }

    public DocumentDeleteResponse(String error) {
        this.error = error;
    }

    public DocumentDeleteResponse() {
    }
}
