package com.openrangelabs.services.operations.model;

import lombok.Data;

@Data
public class SignatureEmailRequest {
    String htmlContent;
    String emailTo;
}
