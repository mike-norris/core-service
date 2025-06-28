package com.openrangelabs.services.ticket.model;

import lombok.Data;

@Data
public class GetTicketResponse {
    CGTicket ticket;
    String error;

    public GetTicketResponse(CGTicket ticket) {
        this.ticket = ticket;
    }

    public CGTicket getTicket() {
        return ticket;
    }

    public void setTicket(CGTicket ticket) {
        this.ticket = ticket;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public GetTicketResponse() {

    }
}
