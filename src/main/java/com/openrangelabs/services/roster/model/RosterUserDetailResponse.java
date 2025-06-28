package com.openrangelabs.services.roster.model;

import com.openrangelabs.services.roster.entity.RosterUserDetails;

public class RosterUserDetailResponse {
    RosterUserDetails details;
    String error;

    public void setDetails(RosterUserDetails details) {
        this.details = details;
    }

    public RosterUserDetails getDetails() { return this.details; }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public RosterUserDetailResponse() { }

    public RosterUserDetailResponse(RosterUserDetails details, String error) {
        this.details = details;
        this.error = error;
    }
}
