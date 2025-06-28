package com.openrangelabs.services.ticket.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TicketAttachment {
    @JsonProperty("user_id")
    long userId;
    @JsonProperty("created_date_time")
    long createdDateTime;
    @JsonProperty("public_attachment")
    boolean publicAttachment;
    @JsonProperty("removed")
    boolean removed;
    @JsonProperty("attachment_location")
    String attachmentLocation;
    @JsonProperty("filename")
    String filename;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(long createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public boolean isPublicAttachment() {
        return publicAttachment;
    }

    public void setPublicAttachment(boolean publicAttachment) {
        this.publicAttachment = publicAttachment;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public String getAttachmentLocation() {
        return attachmentLocation;
    }

    public void setAttachmentLocation(String attachmentLocation) {
        this.attachmentLocation = attachmentLocation;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
