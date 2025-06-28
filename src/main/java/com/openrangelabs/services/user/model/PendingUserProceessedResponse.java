package com.openrangelabs.services.user.model;

import lombok.Data;

@Data
public class PendingUserProceessedResponse {
    boolean hasUpdated;
    String error;

    public PendingUserProceessedResponse(String error) {
        this.error = error;
    }

    public PendingUserProceessedResponse(boolean hasUpdated) {
        this.hasUpdated = hasUpdated;
    }

    public PendingUserProceessedResponse() {
    }
}
