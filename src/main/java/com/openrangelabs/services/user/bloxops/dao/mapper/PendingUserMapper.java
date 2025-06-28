package com.openrangelabs.services.user.bloxops.dao.mapper;

import com.openrangelabs.services.user.repository.PendingUser;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class PendingUserMapper implements RowMapper<PendingUser> {
    @Override
    public PendingUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        PendingUser pendingUser = new PendingUser();
        pendingUser.setOrganizationId(rs.getLong("organization_id"));
        pendingUser.setEmailAddress(rs.getString("emailaddress"));
        pendingUser.setFirstName(rs.getString("firstname"));
        pendingUser.setLastName(rs.getString("lastname"));
        pendingUser.setIsProcessed(rs.getBoolean("isprocessed"));
        pendingUser.setId(rs.getLong("id"));
        pendingUser.setTimeStamp(OffsetDateTime.parse(rs.getString("timestamp"), DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        return pendingUser;
    }
}
