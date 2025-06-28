package com.openrangelabs.services.user.repository;

import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class PendingUser {
    long id;
    String firstName;
    String lastName;
    String emailAddress;
    long organizationId;
    OffsetDateTime timeStamp;
    Boolean isProcessed;

    public long getId() {
        return id;
    }
    public void setExists(long id) {
        this.id = id;
    }

    public long getOrganizationId() {
        return organizationId;
    }
    public void setOrganizationId(long organizationId) {
        this.organizationId = organizationId;
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public OffsetDateTime getTimeStamp() { return timeStamp; }
    public void setTimeStamp(OffsetDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Boolean getIsProcessed() { return isProcessed; }
    public void setIsProcessed(Boolean isProcessed) {
        this.isProcessed = isProcessed;
    }
}
