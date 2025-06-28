package com.openrangelabs.services.authenticate.permission.model;

import lombok.Data;

import java.util.List;

@Data
public class Page {
    long id;
    String displayName;
    String permissions;
    List<ComponentPermission> components;
}
