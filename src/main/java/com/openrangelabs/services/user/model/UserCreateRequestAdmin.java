package com.openrangelabs.services.user.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.openrangelabs.services.user.bonita.model.BonitaUserContactDetails;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserCreateRequestAdmin {
    UserCreate userDetails;
    BonitaUserContactDetails contactDetails;
    String createdBy;
}
