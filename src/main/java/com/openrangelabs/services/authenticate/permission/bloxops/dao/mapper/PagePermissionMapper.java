package com.openrangelabs.services.authenticate.permission.bloxops.dao.mapper;

import com.openrangelabs.services.authenticate.permission.model.PagePermission;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PagePermissionMapper implements RowMapper<PagePermission> {

    @Override
    public PagePermission mapRow(ResultSet rs, int rowNum) throws SQLException {
        PagePermission pagePermission = new PagePermission();
        pagePermission.setPageDisplayName(rs.getString("pgDisplayName"));
        pagePermission.setPageId(rs.getLong("pgPageId"));
        pagePermission.setPageName(rs.getString("pgName"));
        pagePermission.setPermissionAccess(formatPermission(rs.getString("pAccess")));
        pagePermission.setPermissionAttribute(rs.getString("pAttribute"));
        pagePermission.setPermissionEnabled(rs.getString("pEnabled"));
        pagePermission.setPermissionId(rs.getLong("pId"));
       return pagePermission;
    }

    String formatPermission(String permission) {
        while(permission.length() < 4) {
            permission="0"+permission;
        }
        return permission;
    }
}
