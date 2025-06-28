package com.openrangelabs.services.organization.bloxops.dao.mapper;

import com.openrangelabs.services.organization.model.OrganizationUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class OrganizationUserMapper implements RowMapper<OrganizationUser> {

    @Override
    public OrganizationUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrganizationUser user = new OrganizationUser();
        user.setOrganizationId(rs.getLong("organization_id"));
        user.setUserId(rs.getLong("user_id"));
        user.setActive(rs.getBoolean("active"));
        user.setRole(rs.getString("role"));
        user.setFirstName(rs.getString("firstName"));
        user.setLastName(rs.getString("lastName"));
        user.setChangegearId(rs.getInt("changegear_id"));
        try {
            user.setEmail(rs.getString("email_address"));
        }catch(Exception e){
          //  log.error(e.getMessage());
        }
        try {
            user.setProfileImage(rs.getString("profile_image"));
            user.setSharedUser(rs.getBoolean("shared_user"));
        }catch(Exception e){
           // log.error(e.getMessage());
        }
        return user;
    }
}

