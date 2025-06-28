package com.openrangelabs.services.authenticate.permission.model;

import lombok.Data;

@Data
public class ComponentPermission {
    String name;
    String displayName;
    String defaultAccess;
    String access;
    String identifier;
    String module;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDefaultAccess() {
        return defaultAccess;
    }

    public void setDefaultAccess(String defaultAccess) {
        this.defaultAccess = defaultAccess;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }
}
