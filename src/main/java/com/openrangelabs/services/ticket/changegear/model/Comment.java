package com.openrangelabs.services.ticket.changegear.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class Comment {
    @JsonProperty("Action")
    String Action;
    @JsonProperty("Comment")
    String Comment;
    @JsonProperty("CreatorName")
    String CreatorName;
    @JsonProperty("CreatedDateTime")
    String CreatedDateTime;
}
