package com.openrangelabs.services.microsoft.graph.files.model;

import lombok.Data;

@Data
public class SendFileRequest {
    String email;
    String documentId;
    String timezone;
    String browser;
    String ip_address;
    int user_id;
    long org_id;
}
