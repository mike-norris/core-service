package com.openrangelabs.services.operations.model;

import lombok.Data;

@Data
public class LinksUpdateRequest {
    String department;
    String name;
    String url;
    String description;
    int id;
    String token;
}
