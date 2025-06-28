package com.openrangelabs.services.authenticate.permission.bloxops.dao.mapper;

import com.openrangelabs.services.authenticate.permission.model.OrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class OrganizationServiceMapper implements RowMapper<OrganizationService> {

    @Override
    public OrganizationService mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrganizationService service = new OrganizationService();
        service.setDisplayName(rs.getString("display_name"));
        service.setDatacenterName(rs.getString("datacenter_name"));
        service.setName(rs.getString("name"));
        service.setRole(rs.getString("role"));
        service.setOrganizationId(rs.getLong("organization_id"));
        service.setServiceId(rs.getLong("service_id"));
        service.setDatacenterId(rs.getInt("datacenter_id"));
        service.setOrganizationName(rs.getString("organization_name"));
        service.setActive(false);

        try{
            service.setOrgCode(rs.getString("org_code"));
            service.setBeta(rs.getBoolean("beta"));
        }catch(Exception e){
            log.error(e.getMessage());
        }

        try {
            if (rs.findColumn("active") > 0 && rs.getBoolean("active")) {
                service.setActive(true);
            }
        } catch (Exception e) {
            service.setActive(false);
        }
        return service;
    }
}
