package com.openrangelabs.services.user.bonita.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.openrangelabs.services.tools.Commons;

public class BonitaADAddUserRequest {
    @JsonProperty("email_address")
    String emailAddress;

    String firstname;

    String lastname;

    String password;

    public BonitaADAddUserRequest(String emailAddress, String firstname, String lastname , String password) {
        this.emailAddress = emailAddress;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress.toLowerCase();
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) { this.firstname = Commons.capitalizeFirst(firstname); }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = Commons.capitalizeFirst(lastname);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
