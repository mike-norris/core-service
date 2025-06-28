package com.openrangelabs.services.ticket.model;
import lombok.Data;

@Data
public class Ticket {
    String id;
    String name;
    String description;
    String startDate;
    String endDate;
    String stateId;
    String lastUpdate;
    String status;
    String type;
    String firstname;
    String lastname;
    String username;
    String priority;
}
