package com.openrangelabs.services.signing.model;

import lombok.Data;

@Data
public class Signer {
    private String email;
    private String role;
    private String order;
    private String expiration_days;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
