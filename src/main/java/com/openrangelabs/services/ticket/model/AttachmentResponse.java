package com.openrangelabs.services.ticket.model;

import java.io.InputStream;

public class AttachmentResponse {
    byte[] file;
    String filename;
    String error;
    String mimeType;
    public InputStream inputStream;

    public AttachmentResponse(byte[] file) {
        this.file = file;
    }

    public AttachmentResponse() {
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
