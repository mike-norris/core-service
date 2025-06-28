package com.openrangelabs.services.release.model;

import lombok.Data;

@Data
public class ReleaseRecordResponse {

    boolean hasUpdated;
    String error;

    public ReleaseRecordResponse(String error) {
        this.error = error;
    }

    public ReleaseRecordResponse(boolean hasUpdated) {
        this.hasUpdated = hasUpdated;
    }

    public ReleaseRecordResponse() {
    }
}
