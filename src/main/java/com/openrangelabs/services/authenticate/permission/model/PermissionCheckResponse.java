package com.openrangelabs.services.authenticate.permission.model;

import lombok.Data;

@Data
public class PermissionCheckResponse {
    Boolean access;
    String error;

    public PermissionCheckResponse(boolean access, String error) {
        this.access = access;
        this.error = error;
    }
}
