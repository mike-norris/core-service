package com.openrangelabs.services.authenticate.bonita.model;

import lombok.Data;

@Data
public class BonitaForgotPasswordRequest {
    String userAdUserName;
    String userEmailAddress;
    boolean userError = false;
    String userErrorMessage = "";
    String userUrl = "";
    String userFirstName;
    String userLastName;
    boolean userIsForgotPasswordRequest;

    public BonitaForgotPasswordRequest(String userAdUserName, String userEmailAddress, String userFirstName, String userLastName, boolean userIsForgotPasswordRequest) {
        this.userAdUserName = userAdUserName;
        this.userEmailAddress = userEmailAddress+"|reset";
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userIsForgotPasswordRequest = userIsForgotPasswordRequest;
    }
}
