package com.openrangelabs.services.organization.model;

import lombok.Data;

@Data
public class UpdateOrgAdminResponse {
    boolean success;
    String error;

    public UpdateOrgAdminResponse(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public UpdateOrgAdminResponse() {

    }
}
