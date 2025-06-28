package com.openrangelabs.services.authenticate.bloxops.dao.mappers;

import com.openrangelabs.services.authenticate.model.PasswordResetKey;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class PasswordResetKeyMapper implements RowMapper<PasswordResetKey> {

    @Override
    public PasswordResetKey mapRow(ResultSet rs, int rowNum) throws SQLException {
        PasswordResetKey passwordResetKey = new PasswordResetKey();
        passwordResetKey.setKey(rs.getString("key"));
        passwordResetKey.setExpired(rs.getBoolean("expired"));
        passwordResetKey.setTimestamp(OffsetDateTime.parse(rs.getString("timestamp")+"00", DateTimeFormatter.ofPattern("y-M-d H:m:s.nZ")));
        passwordResetKey.setUserName(rs.getString("username"));
        return passwordResetKey;
    }
}
