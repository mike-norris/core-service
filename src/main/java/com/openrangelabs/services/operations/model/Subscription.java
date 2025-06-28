package com.openrangelabs.services.operations.model;

import lombok.Data;

@Data
public class Subscription {
    String notes;
    String name;
    String expiration_dt;
    String created_dt;
    String username;
    String url;
    int id;
}
