package com.openrangelabs.services.organization.model;

import lombok.Data;

import java.util.List;

@Data
public class OrganizationUsersResponse {
    public OrganizationUsersResponse(List<OrganizationUser> users) {
        this.users = users;
    }

    public OrganizationUsersResponse(String error) {
        this.error = error;
    }

    List<OrganizationUser> users;
    String error;
}
