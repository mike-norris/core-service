package com.openrangelabs.services.user.bonita.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BonitaADAddUserResponse {
    // success fields
    int id;
    boolean adUserCreated;
    @JsonProperty("email_address")
    String emailAddress;
    String firstname;
    String lastname;
    String error;

    @JsonProperty("user_id")
    int userId;

    // failure fields
    String code;
    String contactEmail;
    String description;
    String homeRef;
    String reasonPhrase;
    String uri;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setId(int id) { this.id = id; }

    public int getId() { return this.id; }

    public boolean isAdUserCreated() {
        return this.adUserCreated;
    }

    public void setAdUserCreated(boolean adUserCreated) {
        this.adUserCreated = adUserCreated;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return this.lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getError() {
        return this.error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getContactEmail() {
        return this.contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHomeRef() {
        return this.homeRef;
    }

    public void setHomeRef(String homeRef) {
        this.homeRef = homeRef;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

}
