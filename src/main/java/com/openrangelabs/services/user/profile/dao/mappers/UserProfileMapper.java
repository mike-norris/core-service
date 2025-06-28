package com.openrangelabs.services.user.profile.dao.mappers;

import com.openrangelabs.services.user.profile.model.UserProfile;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserProfileMapper implements RowMapper<UserProfile> {

    @Override
    public UserProfile mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserProfile userProfile = new UserProfile();
        userProfile.setTwoFactorProvider(rs.getString("two_factor_provider"));
        userProfile.setProfileImage(rs.getString("profile_image"));
        userProfile.setSecondFactorMethod(rs.getString("second_factor_method"));
        userProfile.setTimeZone(rs.getString("time_zone"));
        userProfile.setGrantorEmailAddress(rs.getString("grantor_email_address"));
        userProfile.setEmailAddress(rs.getString("email_address"));
        userProfile.setId(rs.getInt("id"));
        userProfile.setDashboardCardOrder(rs.getString("dashboard_card_order"));
        userProfile.setSecondFactorEnabled(rs.getBoolean("second_factor_enabled"));
        userProfile.setUserImageFilename(rs.getString("user_image_filename"));
        userProfile.setSharedUser(rs.getBoolean("shared_user"));
        try {
            userProfile.setFirstName(rs.getString("firstname"));
            userProfile.setLastName(rs.getString("lastname"));
        }catch(Exception e){
        }
        return userProfile;
    }
}
