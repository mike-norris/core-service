package com.openrangelabs.services.organization.bloxops.dao.mapper;

import com.openrangelabs.services.organization.model.OrganizationContact;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrganizationContactMapper implements RowMapper<OrganizationContact> {

    @Override
    public OrganizationContact mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrganizationContact contact = new OrganizationContact();
        contact.setId(rs.getLong("id"));
        contact.setOrganizationId(rs.getLong("organization_id"));
        contact.setOrganizationTempName(rs.getString("organization_temp_name"));
        contact.setFirstName(rs.getString("first_name"));
        contact.setLastName(rs.getString("last_name"));
        contact.setEmail(rs.getString("email"));
        contact.setPhone(rs.getString("phone"));
        contact.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        contact.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return contact;
    }
}
