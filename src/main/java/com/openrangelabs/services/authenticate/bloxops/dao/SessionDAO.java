package com.openrangelabs.services.authenticate.bloxops.dao;

import com.openrangelabs.services.authenticate.bloxops.dao.mappers.SessionTokenMapper;
import com.openrangelabs.services.authenticate.model.SessionInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.openrangelabs.services.config.BloxopsDBConfig.BLOXOPS_NAMEDJDBCTEMPLATE;

@Slf4j
@Repository
public class SessionDAO {

    @Autowired
    @Qualifier(BLOXOPS_NAMEDJDBCTEMPLATE)
    NamedParameterJdbcTemplate jdbcTemplate;

    public SessionInfo getSessionToken(String userId) throws EmptyResultDataAccessException {
        String sql = "select session_token, session_id" +
                " from cust_prtl_user_session" +
                " where user_id = :userId" +
                " order by id desc limit 1";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        log.warn("Attempting to get user Session with id:" + userId+".");
        List<SessionInfo> result = jdbcTemplate.query(sql, params, new SessionTokenMapper());
        return result.isEmpty() ? new SessionInfo() : result.get(0);
    }

    public int setSessionToken(String userId, SessionInfo sessionInfo){
        String sql = "insert into cust_prtl_user_session" +
                " (user_id, session_token, session_id, date_time) " +
                " values (:userId, :sessionToken, :sessionId, CURRENT_TIMESTAMP )";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("sessionToken", sessionInfo.getSessionToken());
        params.addValue("sessionId", sessionInfo.getSessionId());

        return jdbcTemplate.update(sql,params);
    }

}
