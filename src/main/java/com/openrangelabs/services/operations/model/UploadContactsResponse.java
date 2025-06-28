package com.openrangelabs.services.operations.model;

import lombok.Data;

@Data
public class UploadContactsResponse {
    boolean success;
    String error;

    public UploadContactsResponse(boolean success, String error) {
        this.success = success;
        this.error = error;

    }
}
