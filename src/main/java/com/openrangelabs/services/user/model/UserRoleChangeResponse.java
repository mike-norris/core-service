package com.openrangelabs.services.user.model;

import lombok.Data;

@Data
public class UserRoleChangeResponse {
    Boolean success;
    String error;

    public void setSuccess(boolean success , String error) {
        this.success = success;
        this.error = error;
    }
}
