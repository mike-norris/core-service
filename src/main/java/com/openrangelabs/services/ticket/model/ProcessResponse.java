package com.openrangelabs.services.ticket.model;
import lombok.Data;

@Data
public class ProcessResponse {
    private String id;
    private String displayDescription;
    private String deploymentDate;
    private String description;
    private String activationState;
    private String name;
    private String deployedBy;
    private String displayName;
    private String actorinitiatorid;
    private String last_update_date;
    private String configurationState;
    private String version;
}
