package com.openrangelabs.services.user.profile.dao;


import com.openrangelabs.services.user.bloxops.dao.mapper.UserShortMapper;
import com.openrangelabs.services.user.entity.UserShort;
import com.openrangelabs.services.user.profile.dao.mappers.UserProfileMapper;
import com.openrangelabs.services.user.profile.model.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.openrangelabs.services.config.BloxopsDBConfig.BLOXOPS_NAMEDJDBCTEMPLATE;

@Slf4j
@Repository
public class UserBloxopsDAO {

    @Autowired
    @Qualifier(BLOXOPS_NAMEDJDBCTEMPLATE)
    NamedParameterJdbcTemplate jdbcTemplate;
    String EMAIL = "email";
    String checkID = " where id = :id";
    String updateCustomerUserProfile = " update cust_prtl_user_profile ";

    public UserProfile getUserProfile(int id) {
        String sql = "select two_factor_provider, profile_image, second_factor_enabled, second_factor_method , email_address, id, shared_user ," +
                " time_zone, grantor_email_address, dashboard_card_order, user_image_filename from bloxops.public.cust_prtl_user_profile" +
                checkID;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        List<UserProfile> result = jdbcTemplate.query(sql, params, new UserProfileMapper());
        return result.isEmpty() ? null : result.get(0);
    }
    public UserProfile getUserProfileByEmail(String email) {
        String sql = "select c.two_factor_provider, c.profile_image, c.second_factor_enabled, c.second_factor_method , c.email_address, c.id, c.shared_user ," +
                " c.time_zone, c.grantor_email_address, c.dashboard_card_order, c.user_image_filename , o.firstname , o.lastname from cust_prtl_user_profile c" +
                " join organization_users o on c.id = o.user_id" +
                " where c.email_address = :email" +
                " limit 1";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(EMAIL, email);
        List<UserProfile> result = jdbcTemplate.query(sql, params, new UserProfileMapper());
        return result.isEmpty() ? null : result.get(0);
    }

    public boolean getUserProfileSecondFactor(String email) {
        String sql = "select id, two_factor_provider, email_address, profile_image, second_factor_enabled, second_factor_method , shared_user , " +
                "time_zone, grantor_email_address, dashboard_card_order, user_image_filename from cust_prtl_user_profile " +
                "where email_address = :email and second_factor_enabled = true limit 1;";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(EMAIL, email);
        List<UserProfile> result = jdbcTemplate.query(sql, params, new UserProfileMapper());
        try {
            return result.get(0).isSecondFactorEnabled();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void createUserProfile(int id, String email , boolean isSharedUser) {
        String sql = "INSERT INTO cust_prtl_user_profile (id, email_address, profile_image, two_factor_provider, time_zone, " +
                "grantor_email_address, dashboard_card_order, user_image_filename ,second_factor_method , shared_user)" +
                "VALUES (:id, :email, :profileImage, :twoFactorProvider, :timeZone, :grantorEmail, :cardOrder, :userImageFilename ,'Email' , :isSharedUser)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        params.addValue(EMAIL, email);
        params.addValue("profileImage", 1);
        params.addValue("twoFactorProvider", "gauthify");
        params.addValue("timeZone", 1);
        params.addValue("grantorEmail", "");
        params.addValue("cardOrder", 1);
        params.addValue("userImageFilename", "");
        params.addValue("isSharedUser" , isSharedUser);
        jdbcTemplate.update(sql, params);
    }

    public void updateUserProfileImage(int id, int imageId) {
        String sql = updateCustomerUserProfile +
                " set profile_image = :imageId" +
                checkID;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        params.addValue("imageId", imageId);
        jdbcTemplate.update(sql, params);
    }

    public void updateUserProfileMfa(int id, boolean mfa) {
        String sql = updateCustomerUserProfile +
                " set second_factor_enabled = :mfa " +
                checkID;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        params.addValue("mfa", mfa);
        jdbcTemplate.update(sql, params);
    }

    public void updateSharedUser(int id, boolean isSharedUser) {
        String sql = updateCustomerUserProfile +
                " set shared_user = :isSharedUser " +
                " where id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        params.addValue("isSharedUser", isSharedUser);
        jdbcTemplate.update(sql, params);
    }

    public void updateUserProfileMfaMethod(int id, String mfaMethod) {
        String sql = updateCustomerUserProfile +
                " set second_factor_method = :mfaMethod " +
                checkID;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        params.addValue("mfaMethod", mfaMethod);
        jdbcTemplate.update(sql, params);
    }

    public void updateUserProfileEmail(int id, String email) {
        String sql = updateCustomerUserProfile +
                " set email_address = :email" +
                checkID;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        params.addValue(EMAIL, email);
        jdbcTemplate.update(sql, params);
    }

    public void updateUserProfileImageFilename(int id,  String filename) {
        String sql = updateCustomerUserProfile +
                " set user_image_filename = :filename " +
                checkID;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        params.addValue("filename", filename);

        jdbcTemplate.update(sql, params);
    }

    public void updateUsersName(int userId ,String firstName ,String lastName) {
        String sql = "update organization_users " +
                " set firstname =:firstName ,lastname =:lastName " +
                " where user_id = :userId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("firstName", firstName);
        params.addValue("lastName", lastName);

        jdbcTemplate.update(sql, params);
    }
    public void addUserRecord(long orgId,  int userId ,boolean activeUser ,String userRole,String firstName ,String lastName) {
        String sql = "INSERT INTO organization_users (organization_id, user_id, active, role, firstname, lastname) " +
                "VALUES (:orgId, :userId, :activeUser, :userRole, :firstName, :lastName)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("orgId", orgId);
        params.addValue("userId", userId);
        params.addValue("activeUser", activeUser);
        params.addValue("userRole", userRole);
        params.addValue("firstName", firstName);
        params.addValue("lastName", lastName);

        jdbcTemplate.update(sql, params);
    }

    public List<UserShort> getPortalUsers() {
        String sql = "select ou.lastname , ou.firstname ,ou.active ,ou.user_id , ou.organization_id , ou.role ," +
                " (select o.name as organizationName from organizations o where ou.organization_id = o.organization_id ), " +
                " (select cp.email_address from cust_prtl_user_profile cp where cp.id = ou.user_id ), " +
                " (select s.date_time as lastLogin from cust_prtl_user_session s where s.user_id = (select cp.email_address from cust_prtl_user_profile cp where cp.id = ou.user_id )  order by s.date_time desc limit 1) " +
                " from organization_users ou " +
                " order by ou.lastname ASC ";
        MapSqlParameterSource params = new MapSqlParameterSource();

        return  jdbcTemplate.query(sql, params, new UserShortMapper());
    }

    public UserShort getPortalUserByEmailAddress(Long organizationId, String emailAddress) {
        String sql = "select ou.lastname, ou.firstname, ou.active, ou.user_id, ou.organization_id, ou.role, o.name as organizationName, cp.email_address " +
                "from organization_users ou " +
                "join organizations o on ou.organization_id=o.organization_id " +
                "join cust_prtl_user_profile cp on ou.user_id=cp.id "+
                "where LOWER(cp.email_address)=:emailAddress and ou.organization_id=:organizationId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("emailAddress", emailAddress.toLowerCase());
        params.addValue("organizationId", organizationId);
        try {
            List<UserShort> userList = jdbcTemplate.query(sql, params, new UserShortMapper());
            if (userList.size() > 0) {
                return userList.get(0);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public UserShort getPortalUserByID(Long organizationId, Long userId) {
        String sql = "select ou.lastname, ou.firstname, ou.active, ou.user_id, ou.organization_id, ou.role, o.name as organizationName, cp.email_address " +
                "from organization_users ou " +
                "join organizations o on ou.organization_id=o.organization_id " +
                "join cust_prtl_user_profile cp on ou.user_id=cp.id "+
                "where LOWER(cp.id)=:userId and ou.organization_id=:organizationId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("organizationId", organizationId);
        try {
            List<UserShort> userList = jdbcTemplate.query(sql, params, new UserShortMapper());
            if (userList.size() > 0) {
                return userList.get(0);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public boolean deleteUserById(int userId) {
        String sql = "DELETE FROM cust_prtl_user_profile WHERE id = :userId ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);

        int rowsAffected = jdbcTemplate.update(sql, params);
        return rowsAffected > 0;
    }

    public boolean deleteUserOrg(int userId) {
        String sql = "DELETE FROM organization_users WHERE user_id = :userId ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);

        int rowsAffected = jdbcTemplate.update(sql, params);
        return rowsAffected > 0;
    }


    public boolean deleteUserRoster(int userId) {
        String sql = "DELETE FROM organization_users WHERE user_id = :userId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);

        int rowsAffected = jdbcTemplate.update(sql, params);
        return rowsAffected > 0;
    }
}
