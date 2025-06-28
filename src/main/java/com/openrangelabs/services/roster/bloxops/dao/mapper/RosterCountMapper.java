package com.openrangelabs.services.roster.bloxops.dao.mapper;

import com.openrangelabs.services.roster.entity.RosterCount;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RosterCountMapper implements RowMapper<RosterCount> {

    @Override
    public RosterCount mapRow(ResultSet rs, int rowNum) throws SQLException {
        RosterCount rosterCount = new RosterCount();
        rosterCount.setCount(rs.getLong("count"));
        rosterCount.setDatacenter(rs.getString("datacenter"));
        rosterCount.setCity(rs.getString("city"));
        rosterCount.setState(rs.getString("state"));
        return rosterCount;
    }
}
