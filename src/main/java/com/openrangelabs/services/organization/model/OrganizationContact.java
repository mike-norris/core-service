package com.openrangelabs.services.organization.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrganizationContact {

    private Long id;
    private Long organizationId;
    private String organizationTempName;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

}
