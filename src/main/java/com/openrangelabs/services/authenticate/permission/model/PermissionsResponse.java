package com.openrangelabs.services.authenticate.permission.model;

import lombok.Data;

import java.util.List;

@Data
public class PermissionsResponse {
    List<Page> pages;
    String error;
}
