package com.openrangelabs.services.ticket.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data
public class TicketUpdateDetails {
    @JsonProperty("eTag")
    int eTag;
    @JsonProperty("HistoryRecords")
    List<CGComment> HistoryRecords;
    @JsonProperty("AttachmentRecords")
    List<CGAttachment> AttachmentRecords;
    @JsonProperty("Tasks")
    List<Task> Tasks;
    @JsonProperty("Action")
    String Action;
    @JsonProperty("Description")
    String Description;
    @JsonProperty("ChangeType")
    String ChangeType;
    String ExternalItemID;
}
