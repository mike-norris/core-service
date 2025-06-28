package com.openrangelabs.services.authenticate.permission.bloxops.dao.mapper;

import com.openrangelabs.services.authenticate.permission.model.Organization;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrganizationMapper implements RowMapper<Organization> {

    @Override
    public Organization mapRow(ResultSet rs, int rowNum) throws SQLException {
        Organization organization = new Organization();
        organization.setOrganizationName(rs.getString("name"));
        organization.setOrganizationIcon(rs.getString("icon"));
        organization.setOrganizationId(rs.getLong("organization_id"));
        return organization;
    }
}
