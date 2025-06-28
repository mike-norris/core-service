package com.openrangelabs.services.roster.bloxops.dao.mapper;

import com.openrangelabs.services.roster.model.RosterSummary;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RosterSummaryMapper implements RowMapper<RosterSummary> {

    @Override
    public RosterSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
        RosterSummary rosterSummary =  new RosterSummary();
        rosterSummary.setRosterUserCount(rs.getLong("roster_count"));
        rosterSummary.setDatacenterId(rs.getLong("datacenter_id"));
        return rosterSummary;
    }
}
