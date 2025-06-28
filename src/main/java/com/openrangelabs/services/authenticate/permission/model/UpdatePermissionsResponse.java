package com.openrangelabs.services.authenticate.permission.model;

import lombok.Data;

@Data
public class UpdatePermissionsResponse {
    Boolean updated;
    String error;

    public UpdatePermissionsResponse(boolean updated, String error) {
        this.updated = updated;
        this.error = error;
    }
}
