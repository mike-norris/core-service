package com.openrangelabs.services.authenticate.permission.bloxops.dao.mapper;

import com.openrangelabs.services.authenticate.permission.enitity.PermissionShort;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class PermissionShortMapper implements RowMapper<PermissionShort> {

    @Override
    public PermissionShort mapRow(ResultSet rs, int rowNum) throws SQLException {
        PermissionShort permissionShort = new PermissionShort();
        permissionShort.setModule(rs.getString("module"));
        permissionShort.setDescription(rs.getString("description"));
        permissionShort.setName(rs.getString("name"));
        permissionShort.setPage(rs.getString("page"));
        return permissionShort;
    }
}
