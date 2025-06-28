package com.openrangelabs.services.user.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.openrangelabs.services.user.bonita.model.BonitaUserContactDetails;

import com.openrangelabs.services.user.profile.model.UserProfile;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserCreateRequest {

    UserCreate userDetails;
    BonitaUserContactDetails contactDetails;
    UserProfile userProfile;
    String type;
}
