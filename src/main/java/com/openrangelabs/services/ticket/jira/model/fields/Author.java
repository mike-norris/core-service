package com.openrangelabs.services.ticket.jira.model.fields;

import lombok.Data;

@Data
public class Author {
    String self;
    String name;
    String key;
    String emailAddress;
    Object avatarUrls;
    String displayName;
    Boolean active;
    String timeZone;

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }
    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }


}
