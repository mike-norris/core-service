package com.openrangelabs.services.roster.bloxops.dao.mapper;

import com.openrangelabs.services.roster.entity.RosterUserSecurity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

public class RosterUserSecurityMapper implements RowMapper<RosterUserSecurity> {

    @Override
    public RosterUserSecurity mapRow(ResultSet rs, int rowNum) throws SQLException {
        RosterUserSecurity rosterUserSecurity = new RosterUserSecurity();
        rosterUserSecurity.setId(rs.getLong("idzZZAsq"));
        rosterUserSecurity.setRosteruserId(rs.getLong("rosteruser_id"));
        rosterUserSecurity.setActorId(rs.getLong("actor_id"));
        rosterUserSecurity.setEventType(rs.getString("eventtype"));
        rosterUserSecurity.setEventStatus(rs.getString("eventstatus"));
        rosterUserSecurity.setEventMessage(rs.getString("eventmessage"));
        rosterUserSecurity.setCreatedDateTime(rs.getObject("created_dt", OffsetDateTime.class));

        return rosterUserSecurity;
    }
}
