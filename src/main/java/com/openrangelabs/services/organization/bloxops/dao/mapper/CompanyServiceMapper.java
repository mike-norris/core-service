package com.openrangelabs.services.organization.bloxops.dao.mapper;

import com.openrangelabs.services.authenticate.permission.enitity.CompanyService;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CompanyServiceMapper implements RowMapper<CompanyService> {

    @Override
    public CompanyService mapRow(ResultSet rs, int rowNum) throws SQLException {
        CompanyService companyService = new CompanyService();
        companyService.setActive(rs.getBoolean("isactive"));
        companyService.setCustomerId(rs.getLong("customerid"));
        companyService.setDatacenterId(rs.getLong("datacenterid"));
        companyService.setId(rs.getLong("id"));
        companyService.setService(rs.getString("service"));
        companyService.setServiceName(rs.getString("servicename"));
        companyService.setAtl(rs.getBoolean("atl"));
        companyService.setBmh(rs.getBoolean("bmh"));
        companyService.setCha(rs.getBoolean("cha"));
        companyService.setHsv(rs.getBoolean("hsv"));
        companyService.setGsv(rs.getBoolean("gsv"));
        return companyService;
    }
}

