package com.openrangelabs.services.user.model;

import lombok.Data;

@Data
public class PasswordUrlRequest {
    String userName;
    String middlewareAdminKey;

    public PasswordUrlRequest(String userName, String middlewareAdminKey) {
        this.userName = userName;
        this.middlewareAdminKey = middlewareAdminKey;
    }
}



