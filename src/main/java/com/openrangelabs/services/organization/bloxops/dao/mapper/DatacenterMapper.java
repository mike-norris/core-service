package com.openrangelabs.services.organization.bloxops.dao.mapper;

import com.openrangelabs.services.datacenter.entity.Datacenter;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DatacenterMapper implements RowMapper<Datacenter> {

    @Override
    public Datacenter mapRow(ResultSet rs, int rowNum) throws SQLException {
        Datacenter datacenter = new Datacenter();
        datacenter.setId(rs.getLong("id"));
        datacenter.setCity(rs.getString("physical_city"));
        datacenter.setName(rs.getString("name"));
        datacenter.setState(rs.getString("physical_state"));
        datacenter.setTimezone(rs.getString("time_zone"));
        datacenter.setManagerEmailAddress(rs.getString("manager_email_address"));
        datacenter.setManagerName(rs.getString("manager_name"));
        datacenter.setType(rs.getString("type"));
        return datacenter;
    }
}

