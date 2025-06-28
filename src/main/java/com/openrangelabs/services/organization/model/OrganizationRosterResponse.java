package com.openrangelabs.services.organization.model;

import com.openrangelabs.services.user.bonita.model.BonitaUserDetails;
import lombok.Data;

import java.util.List;

@Data
public class OrganizationRosterResponse {
    public OrganizationRosterResponse(List<BonitaUserDetails> users) {
        this.users = users;
    }

    public OrganizationRosterResponse(String error) {
        this.error = error;
    }

    public OrganizationRosterResponse() {
    }

    List<BonitaUserDetails> users;
    String error;
}
