package com.openrangelabs.services.user.model;

import com.openrangelabs.services.user.entity.UserShort;
import lombok.Data;

import java.util.List;

@Data
public class UserListResponse {
    List<UserShort> portalUsers;
    String error;

    public UserListResponse(String error) {
        this.error = error;
    }

    public UserListResponse(List<UserShort> portalUsers ,String error) {
        this.portalUsers = portalUsers;
        this.error = error;
    }
}
