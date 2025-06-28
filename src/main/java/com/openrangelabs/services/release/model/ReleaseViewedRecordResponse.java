package com.openrangelabs.services.release.model;

import lombok.Data;

@Data
public class ReleaseViewedRecordResponse {

    boolean hasUpdated;
    String error;

    public ReleaseViewedRecordResponse(String error) {
        this.error = error;
    }

    public ReleaseViewedRecordResponse(boolean hasUpdated) {
        this.hasUpdated = hasUpdated;
    }

    public ReleaseViewedRecordResponse() {
    }
}
