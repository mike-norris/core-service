package com.openrangelabs.services.user.bloxops.dao.mapper;

import com.openrangelabs.services.user.entity.UserShort;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserShortMapper implements RowMapper<UserShort> {

    @Override
    public UserShort mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserShort userShort = new UserShort();
        userShort.setUser_id(rs.getInt("user_id"));
        userShort.setActive(rs.getBoolean("active"));
        userShort.setFirstname(rs.getString("firstname"));
        userShort.setLastname(rs.getString("lastname"));
        userShort.setEmail_address(rs.getString("email_address"));
        userShort.setOrganization_id(rs.getInt("organization_id"));
        userShort.setRole(rs.getString("role"));
        userShort.setOrganizationName(rs.getString("organizationName"));
        userShort.setLastLogin(rs.getString("lastLogin"));
        return userShort;
    }

}
