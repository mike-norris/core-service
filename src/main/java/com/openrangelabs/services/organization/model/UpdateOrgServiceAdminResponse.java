package com.openrangelabs.services.organization.model;

import lombok.Data;

@Data
public class UpdateOrgServiceAdminResponse {
    boolean success;
    String error;
    public UpdateOrgServiceAdminResponse(boolean success, String error) {
        this.success = success;
        this.error = error;
    }
}
