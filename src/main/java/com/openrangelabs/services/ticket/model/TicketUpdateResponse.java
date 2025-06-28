package com.openrangelabs.services.ticket.model;
import lombok.Data;

@Data
public class TicketUpdateResponse {
    String caseId;
    String error;

    public TicketUpdateResponse() {
    }

    public TicketUpdateResponse(String error) {
        this.error = error;
    }
}
