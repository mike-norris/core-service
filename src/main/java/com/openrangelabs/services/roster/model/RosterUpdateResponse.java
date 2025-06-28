package com.openrangelabs.services.roster.model;

import lombok.Data;

@Data
public class RosterUpdateResponse {
    Boolean updated;
    String message;
    String error;

    public RosterUpdateResponse(Boolean updated , String error) {
        this.updated = updated;
        this.error = error;
    }

    public RosterUpdateResponse(boolean updated, String error, String message) {
        this.updated = updated;
        this.error = error;
        this.message = message;
    }
}
