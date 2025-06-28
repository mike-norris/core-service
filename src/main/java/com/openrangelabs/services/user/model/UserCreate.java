package com.openrangelabs.services.user.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserCreate implements Serializable {

    @JsonProperty("ticketType")
    String ticketType;

    @JsonProperty("createdBy")
    int createdBy;

    @JsonProperty("ticketID")
    int ticketID;

    @JsonProperty("firstname")
    String firstName;

    @JsonProperty("icon")
    String icon;

    @JsonProperty("username")
    String username;

    @JsonProperty("title")
    String title;

    @JsonProperty("lastname")
    String lastName;

    @JsonProperty("role")
    String role;

    @JsonProperty("enabled")
    String enabled;

    @JsonProperty("manager_id")
    String managerId;

    @JsonProperty("job_title")
    String jobTitle;

    @JsonProperty("modules")
    List<String> modules;

    int userId;

    String emailAddress;

    @JsonProperty("organizationId")
    String organizationId;

    @JsonProperty("adUsername")
    String adUsername;

    @JsonProperty("sendEmail")
    boolean sendEmail;

    public boolean isSendEmail() {
        try {
            if (this.sendEmail) {
                setSendEmail(this.sendEmail);
            }
        } catch (NullPointerException npe) {
            setSendEmail(true);
        } catch (Exception e) {
            setSendEmail(this.sendEmail);
        }
        return this.sendEmail;
    }
}
