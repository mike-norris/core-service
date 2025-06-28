package com.openrangelabs.services.roster.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RosterUserSecurity {

    private long id;
    private long rosteruserId;
    private long actorId;
    private OffsetDateTime createdDateTime;
    private String eventType; 
    private String eventStatus;
    private String eventMessage;
}
