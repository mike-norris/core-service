package com.openrangelabs.services.authenticate.bloxops.dao.mappers;

import com.openrangelabs.services.authenticate.model.SessionInfo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SessionTokenMapper implements RowMapper<SessionInfo> {

    @Override
    public SessionInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        SessionInfo sessionInfo = new SessionInfo();
        sessionInfo.setSessionToken(rs.getString("session_token"));
        sessionInfo.setSessionId(rs.getString("session_id"));
        return sessionInfo;
    }
}
