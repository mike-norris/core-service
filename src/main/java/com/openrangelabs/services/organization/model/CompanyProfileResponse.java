package com.openrangelabs.services.organization.model;

import lombok.Data;

@Data
public class CompanyProfileResponse {
    Organization organization;
    String fuseBillId;
    String error;

    public CompanyProfileResponse(Organization organization, String fuseBillId) {
        this.organization = organization;
        this.fuseBillId = fuseBillId;
    }

    public Organization getCustomer() {
        return organization;
    }

    public void setCustomer(Organization organization) {
        this.organization = organization;
    }

    public String getFuseBillId() {
        return fuseBillId;
    }

    public void setFuseBillId(String fuseBillId) {
        this.fuseBillId = fuseBillId;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public CompanyProfileResponse(String error) {
        this.error = error;
    }

    public CompanyProfileResponse() {
    }
}
