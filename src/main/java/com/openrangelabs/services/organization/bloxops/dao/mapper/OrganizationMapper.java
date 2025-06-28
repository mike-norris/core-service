package com.openrangelabs.services.organization.bloxops.dao.mapper;

import com.openrangelabs.services.organization.model.Organization;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrganizationMapper implements RowMapper<Organization> {

    @Override
    public Organization mapRow(ResultSet rs, int rowNum) throws SQLException {
        Organization organization = new Organization();
        organization.setOrganizationId(rs.getLong("organization_id"));
        organization.setFusebillId(rs.getLong("fusebill_id"));
        organization.setParentId(rs.getLong("parent_id"));
        organization.setName(rs.getString("name"));
        organization.setOrgCode(rs.getString("org_code"));
        organization.setIcon(rs.getString("icon"));
        organization.setStatus(rs.getString("status"));
        organization.setIntacctID(rs.getString("intacct_id"));
        organization.setBillingPlatform(rs.getString("billing_platform"));
        organization.setSalesforceId(rs.getInt("salesforce_id"));
        organization.setPhoneNumber(rs.getString("phone_number"));
        organization.setEmailAddress(rs.getString("email_address"));
        organization.setAddress1(rs.getString("address_1"));
        organization.setAddress2(rs.getString("address_2"));
        organization.setCity(rs.getString("city"));
        organization.setState(rs.getString("state"));
        organization.setZipcode(rs.getString("zipcode"));
        return organization;
    }
}

