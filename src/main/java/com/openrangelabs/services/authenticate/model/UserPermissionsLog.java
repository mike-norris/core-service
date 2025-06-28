package com.openrangelabs.services.authenticate.model;
import lombok.Data;

@Data
public class UserPermissionsLog {
    private Long id;
    private String name;
    private String module;
    private String description;
}

