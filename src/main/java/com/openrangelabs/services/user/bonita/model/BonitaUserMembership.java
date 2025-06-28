package com.openrangelabs.services.user.bonita.model;
import lombok.Data;

@Data
public class BonitaUserMembership {
    String assigned_date;
    String assigned_by_user_id;
    String group_id;
    String user_id;
    String role_id;
}
