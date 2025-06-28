package com.openrangelabs.services.ticket.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TicketRequestLog {
    @JsonProperty("user_id")
    long userId;
    @JsonProperty("assigned_date_time")
    String assignedDateTime;
    @JsonProperty("display_name")
    String displayName;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getAssignedDateTime() {
        return assignedDateTime;
    }

    public void setAssignedDateTime(String assignedDateTime) {
        this.assignedDateTime = assignedDateTime;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
