package com.openrangelabs.services.documents.model;

import lombok.Data;

@Data
public class DocumentsUploadResponse {
    Boolean uploaded;
    String error;

    public DocumentsUploadResponse(Boolean uploaded, String error) {
        this.uploaded = uploaded;
        this.error = error;
    }

    public DocumentsUploadResponse(String error) {
        this.error = error;
    }

    public DocumentsUploadResponse() {

    }

}
