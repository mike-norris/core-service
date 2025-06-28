package com.openrangelabs.services.datacenter.model.bloxops.dao.shipments;

import lombok.Data;

@Data
public class AddUserResponse {
    boolean isSuccessful;
    String error;

    public AddUserResponse(boolean isSuccessful, String error) {
        this.isSuccessful = isSuccessful;
        this.error = error;
    }

    public AddUserResponse() {

    }
}
