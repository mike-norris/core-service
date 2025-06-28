package com.openrangelabs.services.user.bonita.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BonitaUserContactDetails {

    @JsonProperty("country")
    String coutnry;
    @JsonProperty("website")
    String website;
    @JsonProperty("address")
    String address;
    @JsonProperty("city")
    String city;
    @JsonProperty("fax_number")
    String faxNumber;
    @JsonProperty("building")
    String building;
    @JsonProperty("room")
    String room;
    @JsonProperty("zipcode")
    String zipCode;
    @JsonProperty("phone_number")
    String phoneNumber;
    @JsonProperty("state")
    String state;
    @JsonProperty("id")
    String id;
    @JsonProperty("mobile_number")
    String mobileNumber;
    @JsonProperty("email")
    String email;

    public BonitaUserContactDetails(String id, String email) {
        this.id = id;
        this.email = email;
    }

    public void setMobileNumber(String mobileNumber) {
        if (null == mobileNumber) {
            this.mobileNumber = "";
        } else {
            if (mobileNumber.contains("_")) {
                this.mobileNumber = "";
            } else {
                this.mobileNumber = mobileNumber;
            }
        }
    }

    public BonitaUserContactDetails() {
    }

    public void setErrorFields() {
        String ERROR = "error";
         coutnry = ERROR;
         website = ERROR;
         address = ERROR;
         city = ERROR;
         faxNumber = ERROR;
         building = ERROR;
         room = ERROR;
         zipCode = ERROR;
         phoneNumber = ERROR;
         state = ERROR;
         id = ERROR;
         mobileNumber = ERROR;
         email = ERROR;
    }
}
