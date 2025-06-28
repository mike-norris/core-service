package com.openrangelabs.services.ticket.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CGComment {

    @JsonProperty("Action")
    String Action;
    @JsonProperty("Comment")
    String Comment;
    @JsonProperty("CreatorName")
    String CreatorName;

    public String getAction() {
        return Action;
    }

    public void setAction(String action) {
        Action = action;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getCreatorName() {
        return CreatorName;
    }

    public void setCreatorName(String creatorName) {
        CreatorName = creatorName;
    }
}
