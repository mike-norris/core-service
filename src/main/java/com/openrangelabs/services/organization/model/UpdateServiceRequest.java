package com.openrangelabs.services.organization.model;


import com.openrangelabs.services.authenticate.permission.model.OrganizationService;
import lombok.Data;

@Data
public class UpdateServiceRequest {
    String ticketId;
    OrganizationService service;
}
