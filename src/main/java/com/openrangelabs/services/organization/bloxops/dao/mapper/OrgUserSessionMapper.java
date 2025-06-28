package com.openrangelabs.services.organization.bloxops.dao.mapper;

import com.openrangelabs.services.organization.entity.PortalUserSession;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrgUserSessionMapper implements RowMapper<PortalUserSession> {

    @Override
    public PortalUserSession mapRow(ResultSet rs, int rowNum) throws SQLException {
        PortalUserSession portalUserSession = new PortalUserSession();

        portalUserSession.setDescription(rs.getString("description"));
        portalUserSession.setCity(rs.getString("city"));
        portalUserSession.setUser_id(rs.getInt("user_id"));
        portalUserSession.setFirstname(rs.getString("firstname"));
        portalUserSession.setLastname(rs.getString("lastname"));
        portalUserSession.setUsername(rs.getString("email_address"));
        portalUserSession.setEmail_address(rs.getString("email_address"));
        portalUserSession.setEvent_date(rs.getString("event_date"));
        portalUserSession.setEvent_time(rs.getString("event_time"));

        return portalUserSession;

    }

}
