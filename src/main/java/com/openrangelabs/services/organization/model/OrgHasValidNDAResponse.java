package com.openrangelabs.services.organization.model;

import lombok.Data;

@Data
public class OrgHasValidNDAResponse {
    boolean hasValidNDA;
    String date;
    String error;

    public OrgHasValidNDAResponse(boolean hasValidNDA, String date, String error) {
        this.hasValidNDA = hasValidNDA;
        this.date = date;
        this.error = error;
    }
}
