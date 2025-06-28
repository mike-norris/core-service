package com.openrangelabs.services.user.model;

import lombok.Data;

@Data
public class EmailResponseAdmin {
    Boolean success;
    String error;

    public EmailResponseAdmin(boolean success, String error) {
        this.success = success;
        this.error = error;
    }
}
