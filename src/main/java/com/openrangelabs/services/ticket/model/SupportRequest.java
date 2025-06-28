package com.openrangelabs.services.ticket.model;
import lombok.Data;

@Data
public class SupportRequest {
    private String requester;
    private String description;
    private String persistenceId;
    private String title;
    private String priority;
    private String type;
}
