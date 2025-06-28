package com.openrangelabs.services.signing.model;

import lombok.Data;

@Data
public class ProcessDocumentRequest {
    String documentId;
    String userId;
}
