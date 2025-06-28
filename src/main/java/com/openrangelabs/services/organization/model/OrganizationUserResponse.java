package com.openrangelabs.services.organization.model;

import java.util.List;

public class OrganizationUserResponse {
    List<OrganizationUser> organizationUsers;
    String error;

    public OrganizationUserResponse() {
    }

    public OrganizationUserResponse(String error) {
        this.error = error;
    }

    public List<OrganizationUser> getOrganizationUsers() {
        return organizationUsers;
    }

    public void setOrganizationUsers(List<OrganizationUser> organizationUsers) {
        this.organizationUsers = organizationUsers;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
