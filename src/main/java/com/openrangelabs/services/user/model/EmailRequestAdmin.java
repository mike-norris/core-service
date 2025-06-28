package com.openrangelabs.services.user.model;

import lombok.Data;

@Data
public class EmailRequestAdmin {
    String userName;
    String email;
    String ticketId;
    String orgId;
    String emailType;
}
