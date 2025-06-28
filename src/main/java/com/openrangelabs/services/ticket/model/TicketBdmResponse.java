package com.openrangelabs.services.ticket.model;
import lombok.Data;

@Data
public class TicketBdmResponse {
    TicketDetail ticketDetails;
    String error;
    public TicketBdmResponse(TicketDetail ticketDetails) {
        this.ticketDetails = ticketDetails;
    }
    public TicketBdmResponse(String error) {
        this.error = error;
    }

    public TicketDetail getTicketDetails() {
        return ticketDetails;
    }

    public void setTicketDetails(TicketDetail ticketDetails) {
        this.ticketDetails = ticketDetails;
    }
}
