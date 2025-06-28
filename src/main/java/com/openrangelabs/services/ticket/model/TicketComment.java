package com.openrangelabs.services.ticket.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TicketComment {
    @JsonProperty("user_id")
    long userId;
    @JsonProperty("created_date_time")
    long createdDateTime;
    @JsonProperty("public_comment")
    boolean publicComment;
    @JsonProperty("removed")
    boolean removed;
    @JsonProperty("comment")
    String comment;

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

    public boolean isPublicComment() {
        return publicComment;
    }

    public void setPublicComment(boolean publicComment) {
        this.publicComment = publicComment;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
