package com.openrangelabs.services.datacenter.model.bloxops.dao;

import lombok.Data;

@Data
public class DatacenterAccessReportResponse {
    boolean sent;
    String emailAddress;
    String error;

    public DatacenterAccessReportResponse(boolean sent, String emailAddress, String error) {
        this.sent = sent;
        this.emailAddress = emailAddress;
        this.error = error;
    }
}
