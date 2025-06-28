package com.openrangelabs.services.authenticate.model;

import lombok.Data;

@Data
public class ForgotPasswordRequest {
    String userName;
    String ticketId;
}
