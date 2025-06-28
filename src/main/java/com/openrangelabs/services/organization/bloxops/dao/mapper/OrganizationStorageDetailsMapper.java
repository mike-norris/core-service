package com.openrangelabs.services.organization.bloxops.dao.mapper;


import com.openrangelabs.services.organization.model.OrganizationStorageDetails;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrganizationStorageDetailsMapper implements RowMapper<OrganizationStorageDetails> {

    @Override
    public OrganizationStorageDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrganizationStorageDetails organization = new OrganizationStorageDetails();
        organization.setName(rs.getString("name"));
        organization.setOrganizationId(rs.getInt("organization_id"));
        organization.setOrg_code(rs.getString("org_code"));
        organization.setStoragetype(rs.getString("storage_type"));
        organization.setPackagetype(rs.getString("package_type"));
        organization.setStatus(rs.getString("status"));
        organization.setCommitmentamount(rs.getString("commitment_amount"));
        organization.setAtl(rs.getBoolean("atl"));
        organization.setBmh(rs.getBoolean("bhm"));
        organization.setCha(rs.getBoolean("cha"));
        organization.setHsv(rs.getBoolean("hsv"));
        organization.setGsv(rs.getBoolean("gsp"));
        organization.setGsv(rs.getBoolean("myr"));
        return organization;
    }
}
