package com.openrangelabs.services.roster.bloxops.dao.mapper;

import com.openrangelabs.services.roster.entity.RosterUserDatacenter;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RosterUserDatacenterMapper implements RowMapper<RosterUserDatacenter> {

    @Override
    public RosterUserDatacenter mapRow(ResultSet rs, int rowNum) throws SQLException {
        RosterUserDatacenter rosterUserDatacenter = new RosterUserDatacenter();
        rosterUserDatacenter.setRosteruserId(rs.getLong("rosteruser_id"));
        rosterUserDatacenter.setDatacenterId(rs.getLong("datacenter_id"));
        rosterUserDatacenter.setDatacenter(rs.getString("datacenter"));
        rosterUserDatacenter.setEscortRequired(rs.getBoolean("escort_required"));
        rosterUserDatacenter.setVendor(rs.getBoolean("vendor"));
        rosterUserDatacenter.setGuest(rs.getBoolean("guest"));
        rosterUserDatacenter.setOrlEmployee(rs.getBoolean("orl_employee"));
        rosterUserDatacenter.setStatus(rs.getInt("status"));
        rosterUserDatacenter.setCreatedBy(rs.getInt("created_by"));
        rosterUserDatacenter.setCreatedDt(rs.getString("created_dt"));

        return rosterUserDatacenter;
    }
}
