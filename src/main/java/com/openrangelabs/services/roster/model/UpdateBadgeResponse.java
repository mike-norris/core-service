package com.openrangelabs.services.roster.model;

import lombok.Data;

@Data
public class UpdateBadgeResponse {
    Boolean updated;
    String error;

    public UpdateBadgeResponse(Boolean updated) {
        this.updated = updated;
    }
    public UpdateBadgeResponse(String error) {
        this.error = error;
    }
}
