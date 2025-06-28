package com.openrangelabs.services.ticket.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Task {
    @JsonProperty("Description")
    String Description;
    @JsonProperty("ServiceRequestTask")
    int ServiceRequestTask;
}
