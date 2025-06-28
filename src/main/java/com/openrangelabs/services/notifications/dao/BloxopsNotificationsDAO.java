package com.openrangelabs.services.notifications.dao;

import com.openrangelabs.services.notifications.model.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.openrangelabs.services.config.BloxopsDBConfig.BLOXOPS_NAMEDJDBCTEMPLATE;

@Repository
public class BloxopsNotificationsDAO {

    @Autowired
    @Qualifier(BLOXOPS_NAMEDJDBCTEMPLATE)
    NamedParameterJdbcTemplate jdbcTemplate;

    public List<Notification> getUserAccess(int userId) {
        String sql = "select accesstype , eventstatus , eventtype , userid, rosteruserid, created_dt " +
                " from useraccess" +
                " where userid = :userId and created_dt IS NOT NULL";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        return jdbcTemplate.query(sql, params, new NotificationsMapper());
    }
}
