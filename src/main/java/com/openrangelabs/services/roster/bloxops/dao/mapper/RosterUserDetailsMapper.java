package com.openrangelabs.services.roster.bloxops.dao.mapper;

import com.openrangelabs.services.roster.entity.RosterUserDetails;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

public class RosterUserDetailsMapper implements RowMapper<RosterUserDetails> {

    @Override
    public RosterUserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        RosterUserDetails rosterUser = new RosterUserDetails();
        rosterUser.setBadgeRequired(rs.getBoolean("badgerequired"));
        rosterUser.setOrganizationId(rs.getLong("organization_id"));
        rosterUser.setCompanyName(rs.getString("organization_name"));
        rosterUser.setEmailAddress(rs.getString("emailaddress"));
        rosterUser.setFirstName(rs.getString("firstname"));
        rosterUser.setLastName(rs.getString("lastname"));
        rosterUser.setId(rs.getLong("id"));
        rosterUser.setUserId(rs.getLong("user_id"));
        rosterUser.setIsActive(rs.getBoolean("isactive"));
        rosterUser.setPhotoOnFile(false);
        rosterUser.setPhotoLocation("");
        rosterUser.setPositionTitle(rs.getString("position_title"));
        rosterUser.setAuthorizationBy(rs.getInt("authorization_by"));
        rosterUser.setAuthorizationDt(rs.getObject("authorization_dt", OffsetDateTime.class));
        return rosterUser;
    }
}
