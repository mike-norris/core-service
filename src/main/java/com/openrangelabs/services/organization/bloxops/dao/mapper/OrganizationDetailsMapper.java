package com.openrangelabs.services.organization.bloxops.dao.mapper;

import com.openrangelabs.services.organization.model.OrganizationDetails;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrganizationDetailsMapper implements RowMapper<OrganizationDetails> {

    @Override
    public OrganizationDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrganizationDetails organization = new OrganizationDetails();
        organization.setName(rs.getString("name"));
        organization.setOrganizationId(rs.getInt("organization_id"));
        try{
        organization.setStatus(rs.getInt("status"));}
        catch(Exception e){
            organization.setStatus(0);
        }

        return organization;
    }
}
