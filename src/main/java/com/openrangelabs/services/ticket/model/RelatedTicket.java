package com.openrangelabs.services.ticket.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RelatedTicket {
    @JsonProperty("created_date_time")
    String createdDateTime;
    String title;
    String message;
    boolean internal;

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isInternal() {
        return internal;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }
}
