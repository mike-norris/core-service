package com.openrangelabs.services.documents.model;

import lombok.Data;

import java.io.InputStream;

@Data
public class DocumentDetails {
    InputStream fileStream;
    String mimetype;
    int size;

    public DocumentDetails(InputStream fileStream,String mimetype, int size) {
        this.fileStream = fileStream;
        this.mimetype = mimetype;
        this.size = size;
    }

    public DocumentDetails() {

    }
}
