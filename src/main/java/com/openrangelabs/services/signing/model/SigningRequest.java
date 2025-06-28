package com.openrangelabs.services.signing.model;

import lombok.Data;

@Data
public class SigningRequest {
    String sendersEmail;
    String signersEmail;
}
