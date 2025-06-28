package com.openrangelabs.services.authenticate.model;
import lombok.Data;

@Data
public class UserPermissions {
    private Long id;
    private String name;
    private String module;
    private String description;
}
