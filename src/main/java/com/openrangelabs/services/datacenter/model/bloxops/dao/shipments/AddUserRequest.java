package com.openrangelabs.services.datacenter.model.bloxops.dao.shipments;

import lombok.Data;

@Data
public class AddUserRequest {
    String fullName;
    String email;
    String phoneNumber;
    String assistantsEmail;
    int datacenterID;
}
