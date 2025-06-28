package com.openrangelabs.services.authenticate.bloxops.dao.mappers;

import com.openrangelabs.services.authenticate.model.PasswordSession;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PasswordSessionTokenMapper implements RowMapper<PasswordSession> {

    @Override
    public PasswordSession mapRow(ResultSet rs, int rowNum) throws SQLException {
        PasswordSession session = new PasswordSession();
        session.setExpired(rs.getBoolean("expired"));
        session.setId(rs.getString("id"));
        session.setUrl(rs.getString("url"));
        session.setUserName(rs.getString("user_name"));
        return session;
    }
}
