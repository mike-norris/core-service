package com.openrangelabs.services.roster.model;

import lombok.Data;

@Data
public class RosterUserUpdateResponse {
    boolean success;
    String error;

    public RosterUserUpdateResponse(String error) {
        this.error = error;
    }

    public RosterUserUpdateResponse(Boolean success, String error) {
        this.success = success;
        this.error = error;
    }
}
