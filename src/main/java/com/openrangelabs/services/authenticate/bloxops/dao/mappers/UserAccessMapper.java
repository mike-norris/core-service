package com.openrangelabs.services.authenticate.bloxops.dao.mappers;

import com.openrangelabs.services.roster.entity.UserAccess;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAccessMapper implements RowMapper<UserAccess> {

    @Override
    public UserAccess mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserAccess userAccess = new UserAccess();
        userAccess.setId(rs.getLong("id"));
        userAccess.setRosterUserId(rs.getInt("userid"));
        userAccess.setAccessType(rs.getString("accesstype"));
        userAccess.setDatacenterId(rs.getLong("datacenterid"));
        userAccess.setEventStatus(rs.getString("eventstatus"));
        userAccess.setEventType(rs.getString("eventtype"));
        userAccess.setRosterUserId(rs.getLong("rosteruserid"));
        userAccess.setCreatedDT(rs.getString("created_dt"));
        return userAccess;
    }
}
