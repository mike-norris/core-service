package com.openrangelabs.services.ticket.model;

import lombok.Data;

@Data
public class CGTicketUpdateRequest {
    int ticketId;
    String ticketType;
    TicketUpdateDetails ticketUpdateDetails;
}
