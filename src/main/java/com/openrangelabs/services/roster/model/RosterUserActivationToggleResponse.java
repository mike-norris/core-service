package com.openrangelabs.services.roster.model;

import lombok.Data;

@Data
public class RosterUserActivationToggleResponse {
    boolean hasUpdated;
    String error;

    public RosterUserActivationToggleResponse(String error) {
        this.error = error;
    }

    public RosterUserActivationToggleResponse(boolean hasUpdated) {
        this.hasUpdated = hasUpdated;
    }

    public RosterUserActivationToggleResponse() {
    }
}
