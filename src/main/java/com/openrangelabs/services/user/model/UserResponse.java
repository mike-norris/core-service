package com.openrangelabs.services.user.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.openrangelabs.services.authenticate.permission.model.Organization;
import com.openrangelabs.services.eula.model.EulaUserDetails;
import com.openrangelabs.services.user.bonita.model.BonitaUserDetails;
import com.openrangelabs.services.authenticate.model.SessionInfo;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse {
    BonitaUserDetails user;
    SessionInfo sessionInfo;
    List<Organization> memberships;
    EulaUserDetails eulaUserDetails;
    String error;
    boolean mfa;

    public UserResponse() {
    }

    public UserResponse(BonitaUserDetails user, String error) {
        this.user = user;
        this.error = error;
    }
}
