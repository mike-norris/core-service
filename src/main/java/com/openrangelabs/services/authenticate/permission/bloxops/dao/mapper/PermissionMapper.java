package com.openrangelabs.services.authenticate.permission.bloxops.dao.mapper;

import com.openrangelabs.services.authenticate.permission.enitity.Permission;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PermissionMapper implements RowMapper<Permission> {
    @Override
    public Permission mapRow(ResultSet rs, int rowNum) throws SQLException {
        Permission permission = new Permission();
        permission.setUserId(rs.getLong("user_id"));
        permission.setOrgId(rs.getLong("organization_id"));
        permission.setEnabled(rs.getBoolean("enabled"));
        permission.setAccess(rs.getString("access"));
        return permission;
    }
}
