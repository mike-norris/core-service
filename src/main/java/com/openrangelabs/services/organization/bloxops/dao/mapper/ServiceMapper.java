package com.openrangelabs.services.organization.bloxops.dao.mapper;

import com.openrangelabs.services.operations.model.Service;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ServiceMapper implements RowMapper<Service> {

    @Override
    public Service mapRow(ResultSet rs, int rowNum) throws SQLException {
        Service service = new Service();
        service.setServiceId(rs.getInt("service_id"));
        service.setName(rs.getString("name"));
        service.setDescription(rs.getString("description"));
        service.setDisplayName(rs.getString("display_name"));
        service.setParent_service_id(rs.getInt("parent_service_id"));
        return service;
    }

}
