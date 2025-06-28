package com.openrangelabs.services.operations.model;

import lombok.Data;

import java.util.List;

@Data
public class Link {
    int id;
    String department;
    String url;
    String name;
    String description;
    boolean approved;
    List<String> tags;
}
