package com.openrangelabs.services.user.bonita.model;
import lombok.Data;

@Data
public class BonitaGroup {
    String id;
    String creation_date;
    String created_by_user_id;
    String icon;
    String description;
    String name;
    String path;
    String displayName;
    String last_update_date;
    Object role;
}