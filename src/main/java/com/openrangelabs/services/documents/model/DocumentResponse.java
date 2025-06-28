package com.openrangelabs.services.documents.model;

import lombok.Data;

import java.io.InputStream;

@Data
public class DocumentResponse {
    byte[] document;
    String documentName;
    String error;
    String mimeType;
    public static InputStream inputStream;

    public DocumentResponse(byte[] document) {
        this.document = document;
    }

    public DocumentResponse() {
    }

}
