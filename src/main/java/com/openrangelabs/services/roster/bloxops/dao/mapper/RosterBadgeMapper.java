package com.openrangelabs.services.roster.bloxops.dao.mapper;

import com.openrangelabs.services.roster.entity.RosterBadge;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class RosterBadgeMapper implements RowMapper<RosterBadge> {

    @Override
    public RosterBadge mapRow(ResultSet rs, int rowNum) throws SQLException{
        RosterBadge rosterBadge = new RosterBadge();
        rosterBadge.setBadgeId(rs.getInt("badge_id"));
        rosterBadge.setCardNumber(rs.getInt("card_number"));
        rosterBadge.setBadgeRef(rs.getString("badge_ref"));
        rosterBadge.setActive(rs.getBoolean("active"));
        rosterBadge.setRosteruserId(rs.getInt("rosteruser_id"));
        rosterBadge.setIssuedBy(rs.getInt("issued_by"));
        rosterBadge.setIssuedDt(rs.getString("issued_dt"));
        rosterBadge.setLost(rs.getBoolean("lost"));
        rosterBadge.setGuid(rs.getString("guid"));
        rosterBadge.setPersonnelGuid(rs.getString("personnel_guid"));
        rosterBadge.setDatacenter(rs.getString("datacenter"));
        rosterBadge.setSystemObjectId(rs.getInt("system_object_id"));
        rosterBadge.setCommonName(rs.getString("common_name"));
        rosterBadge.setExpired(rs.getBoolean("expired"));
        rosterBadge.setDisabled(rs.getBoolean("disabled"));
        rosterBadge.setStolen(rs.getBoolean("stolen"));
        rosterBadge.setRevoked(rs.getBoolean("revoked"));
        rosterBadge.setDisabledByInactivity(rs.getBoolean("disabled_by_inactivity"));
        rosterBadge.setPrintDate(rs.getString("print_date"));
        rosterBadge.setActivationDt(rs.getString("activation_dt"));
        rosterBadge.setExpirationDt(rs.getString("expiration_dt"));

        return rosterBadge;
    }
}
