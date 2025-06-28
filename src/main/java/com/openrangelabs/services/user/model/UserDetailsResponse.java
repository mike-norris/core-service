package com.openrangelabs.services.user.model;

import com.openrangelabs.services.user.entity.User;
import lombok.Data;

@Data
public class UserDetailsResponse {

    User user;
    String error;

    public UserDetailsResponse(User user) {
        this.user = user;
    }

    public UserDetailsResponse(String error) {
        this.error = error;
    }
}
