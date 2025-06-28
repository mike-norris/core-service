package com.openrangelabs.services.microsoft.graph.files.model;

import lombok.Data;

@Data
public class DownloadItemRequest {
    String itemId;
    String timezone;
    String browser;
    String ip_address;
    String email;
    int user_id;
    long org_id;
}
