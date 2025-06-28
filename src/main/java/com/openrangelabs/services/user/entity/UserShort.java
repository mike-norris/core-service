package com.openrangelabs.services.user.entity;

import lombok.Data;

@Data
public class UserShort {
    boolean active;
    String email_address;
    String lastname;
    String firstname;
    String role;
    String organizationName;
    int organization_id;
    int user_id;
    String lastLogin;
}
