package com.openrangelabs.services.operations.model;

import com.openrangelabs.services.organization.model.OrganizationContact;
import lombok.Data;

import java.util.List;

@Data
public class GetContactsResponse {
    List<OrganizationContact> contacts;
    String error;

    public GetContactsResponse(List<OrganizationContact> contacts, String error) {
        this.contacts = contacts;
        this.error = error;
    }
}
