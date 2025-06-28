package com.openrangelabs.services.ticket.model;

import lombok.Data;

@Data
public class TicketCountResponse {
    int total;
    int open;
    int closed;
    String error;

    public TicketCountResponse(String error) {
        this.error = error;
    }

    public TicketCountResponse(int total, int open, int closed) {
        this.total = total;
        this.open = open;
        this.closed = closed;
    }
}
