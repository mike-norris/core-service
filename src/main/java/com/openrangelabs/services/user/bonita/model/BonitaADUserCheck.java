package com.openrangelabs.services.user.bonita.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BonitaADUserCheck {
    boolean exists;
    @JsonProperty("class")
    String clazz;
    @JsonProperty("email_address")
    String emailAddress;
    String lastname;
    String firstname;

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
}
