package com.openrangelabs.services.organization.model;

import lombok.Data;

@Data
public class OrganizationStorageDetails {
    String name;
    String org_code;
    String status;
    String packagetype;
    String storagetype;
    String commitmentamount;
    boolean atl;
    boolean bmh;
    boolean cha;
    boolean hsv;
    boolean gsv;
    int organizationId;
}
