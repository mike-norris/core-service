package com.openrangelabs.services.roster.bloxops.dao.mapper;

import com.openrangelabs.services.roster.entity.RosterUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

@Slf4j
public class RosterUserMapper implements RowMapper<RosterUser> {

    @Override
    public RosterUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        RosterUser rosterUser = new RosterUser();
        rosterUser.setBadgeRequired(rs.getBoolean("badgerequired"));
        rosterUser.setOrganizationId(rs.getLong("organization_id"));
        try {
            rosterUser.setCompanyName(rs.getString("organization_name"));
        } catch (Exception e) {
            String devNull = e.getMessage();
            log.error(devNull);
        }
        rosterUser.setEmailAddress(rs.getString("emailaddress"));
        rosterUser.setFirstName(rs.getString("firstname"));
        rosterUser.setLastName(rs.getString("lastname"));
        rosterUser.setId(rs.getLong("id"));
        rosterUser.setUserId(rs.getLong("user_id"));
        rosterUser.setIsActive(rs.getBoolean("isactive"));
        rosterUser.setPositionTitle(rs.getString("position_title"));
        rosterUser.setCreatedBy(rs.getInt("created_by"));
        rosterUser.setCreatedDt(rs.getObject("created_dt", OffsetDateTime.class));
        rosterUser.setAuthorizationBy(rs.getInt("authorization_by"));
        rosterUser.setAuthorizationDt(rs.getObject("authorization_dt", OffsetDateTime.class));

        try{
            rosterUser.setPersonnelId(rs.getInt("personnel_id"));
        }catch(Exception e){
            log.info("Rosteruser not set up in rosteruser_datacenter");
            log.error(e.getMessage());
        }

        return rosterUser;
    }
}
