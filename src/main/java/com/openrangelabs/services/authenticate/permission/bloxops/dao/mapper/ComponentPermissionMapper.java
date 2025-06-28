package com.openrangelabs.services.authenticate.permission.bloxops.dao.mapper;

import com.openrangelabs.services.authenticate.permission.model.ComponentPermission;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ComponentPermissionMapper implements RowMapper<ComponentPermission> {

    @Override
    public ComponentPermission mapRow(ResultSet rs, int rowNum) throws SQLException {
        ComponentPermission permission = new ComponentPermission();
        permission.setAccess(formatPermission(rs.getString("access")));
        permission.setDefaultAccess(formatPermission(rs.getString("default_access")));
        permission.setName(rs.getString("cname"));
        permission.setDisplayName(rs.getString("display_name"));
        permission.setIdentifier(rs.getString("identifier"));
        permission.setModule(rs.getString("module"));
       return permission;
    }

    String formatPermission(String permission) {
        while(permission.length() < 4) {
            permission="0"+permission;
        }
        return permission;
    }
}
