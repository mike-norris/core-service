package com.openrangelabs.services.organization.model;

import lombok.Data;

import java.util.List;
@Data
public class OrganizationUsersList {
    public OrganizationUsersList(List<OrganizationUser> users) {
        this.users = users;
    }

    public OrganizationUsersList(String error) {
        this.error = error;
    }

    List<OrganizationUser> users;
    String error;
}
