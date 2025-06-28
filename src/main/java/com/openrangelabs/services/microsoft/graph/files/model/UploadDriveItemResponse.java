package com.openrangelabs.services.microsoft.graph.files.model;

import lombok.Data;

@Data
public class UploadDriveItemResponse {
    boolean uploaded;
    String error;

    public UploadDriveItemResponse(boolean uploaded, String error) {
        this.uploaded = uploaded;
        this.error = error;
    }
}
