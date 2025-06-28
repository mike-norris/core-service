package com.openrangelabs.services.ticket.model;

import java.util.List;
import lombok.Data;

@Data
public class TicketsResponse {
    List<Ticket> tickets;
    String error;

    public TicketsResponse() {
    }

    public TicketsResponse(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public TicketsResponse(String error) {
        this.error = error;
    }
}
