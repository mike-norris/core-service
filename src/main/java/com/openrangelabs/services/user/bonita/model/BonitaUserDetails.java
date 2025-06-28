package com.openrangelabs.services.user.bonita.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.openrangelabs.services.authenticate.model.SecondFactorAuthType;
import com.openrangelabs.services.user.profile.model.UserProfile;

import java.util.List;
import java.util.Objects;

import lombok.Data;

@Data
public class BonitaUserDetails {
    @JsonProperty("firstname")
    String firstName;
    @JsonProperty("icon")
    String icon;
    @JsonProperty("creation_date")
    String creationDate;
    @JsonProperty("userName")
    String userName;
    @JsonProperty("title")
    String title;
    @JsonProperty("created_by_user_id")
    String createdByUserId;
    @JsonProperty("enabled")
    String enabled;
    @JsonProperty("lastname")
    String lastName;
    @JsonProperty("last_connection")
    String lastConnection;
    @JsonProperty("password")
    String password;
    @JsonProperty("manager_id")
    String managerId;
    @JsonProperty("id")
    String id;
    @JsonProperty("job_title")
    String jobTitle;
    @JsonProperty("last_update_date")
    String lastUpdateDate;
    String mobilePhonePartial;
    String emailPartial;
    String role;
    String lastLogin;
    boolean isPending;
    boolean isSharedUser;

    BonitaUserContactDetails contactDetails;
    UserProfile userProfile;
    List<BonitaGroup> userGroups;
    List<SecondFactorAuthType> authTypes;

    private static final String ERROR = "error";

    public void setErrorFields() {
         firstName = ERROR;
         icon = ERROR;
         creationDate = ERROR;
         userName = ERROR;
         title = ERROR;
         createdByUserId = ERROR;
         enabled = ERROR;
         lastName = ERROR;
         lastConnection = ERROR;
         password = ERROR;
         managerId = ERROR;
         id = ERROR;
         jobTitle = ERROR;
         lastUpdateDate = ERROR;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BonitaUserDetails that = (BonitaUserDetails) o;
        return isPending == that.isPending &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(icon, that.icon) &&
                Objects.equals(creationDate, that.creationDate) &&
                Objects.equals(userName, that.userName) &&
                Objects.equals(title, that.title) &&
                Objects.equals(createdByUserId, that.createdByUserId) &&
                Objects.equals(enabled, that.enabled) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(lastConnection, that.lastConnection) &&
                Objects.equals(password, that.password) &&
                Objects.equals(managerId, that.managerId) &&
                Objects.equals(id, that.id) &&
                Objects.equals(jobTitle, that.jobTitle) &&
                Objects.equals(lastUpdateDate, that.lastUpdateDate) &&
                Objects.equals(mobilePhonePartial, that.mobilePhonePartial) &&
                Objects.equals(emailPartial, that.emailPartial) &&
                Objects.equals(contactDetails, that.contactDetails) &&
                Objects.equals(userProfile, that.userProfile) &&
                Objects.equals(userGroups, that.userGroups) &&
                Objects.equals(role, that.role) &&
                Objects.equals(lastLogin, that.lastLogin) &&
                Objects.equals(authTypes, that.authTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, icon, creationDate, userName, title, createdByUserId, enabled, lastName, lastConnection, password, managerId, id, jobTitle, lastUpdateDate, mobilePhonePartial, emailPartial, isPending, contactDetails, userProfile, userGroups, authTypes, role, lastLogin);
    }
}
