package com.openrangelabs.services.user.entity;
import com.openrangelabs.services.user.bonita.model.BonitaUserContactDetails;
import com.openrangelabs.services.user.profile.model.UserProfile;
import lombok.Data;

@Data
public class User {
    String firstname;
    String icon;
    String creation_date;
    String userName;
    String title;
    String created_by_user_id;
    String enabled;
    String lastname;
    String last_connection;
    String password;
    String manager_id;
    String id;
    String job_title;
    String last_update_date;
    UserProfile userProfile;
    BonitaUserContactDetails contactDetailsResponse;
    String lastLogin;
    String role;
}
