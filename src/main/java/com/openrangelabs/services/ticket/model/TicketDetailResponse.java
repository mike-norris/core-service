package com.openrangelabs.services.ticket.model;
import lombok.Data;

@Data
public class TicketDetailResponse {
    CaseDetail ticketDetails;
    String error;

    public TicketDetailResponse(CaseDetail ticketDetails) {
        this.ticketDetails = ticketDetails;
    }

    public TicketDetailResponse(String error) {
        this.error = error;
    }

    public TicketDetailResponse() {
    }
}
