package com.openrangelabs.services.message.bloxops.dao;

import com.openrangelabs.services.config.BloxopsDBConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ErrorMessageDAO {

    @Autowired
    @Qualifier(BloxopsDBConfig.BLOXOPS_NAMEDJDBCTEMPLATE)
    NamedParameterJdbcTemplate jdbcTemplate;
    String ENTRY = "entry";
    String SELECT_MESSAGE = "select message";
    String AUTH_ERROR = "Could not Authenticate.";
    String CUST_PORTAL_TABLE = " from cust_prtl_messages ";

    public String getBonitaTokenErrorMessage(int entry) {
        String sql = SELECT_MESSAGE +
                CUST_PORTAL_TABLE +
                " where module = 'login' and section = 'token' and entry = :entry";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ENTRY, Math.min(entry,3));
        try {
            return jdbcTemplate.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return AUTH_ERROR;
        }
    }

    public String getAuthenicateErrorMessage(int entry) {
        String sql = SELECT_MESSAGE +
                CUST_PORTAL_TABLE  +
                " where module = 'login' and section = 'authfail' and entry = :entry";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ENTRY, Math.min(entry,3));
        try {
            return jdbcTemplate.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return AUTH_ERROR;
        }
    }

    public String getUserNameErrorMessage(int entry) {
        String sql = SELECT_MESSAGE +
                CUST_PORTAL_TABLE +
                " where module = 'login' and section = 'username' and entry = :entry";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ENTRY, Math.min(entry,3));
        try {
            return jdbcTemplate.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return AUTH_ERROR;
        }
    }

    public String getPasswordErrorMessage(int entry) {
        String sql = SELECT_MESSAGE +
                CUST_PORTAL_TABLE +
                " where module = 'login' and section = 'password' and entry = :entry";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ENTRY, Math.min(entry,4));
        try {
            return jdbcTemplate.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return AUTH_ERROR;
        }
    }

    public String getInactiveOrgErrorMessage(int entry) {
        String sql = SELECT_MESSAGE +
                CUST_PORTAL_TABLE +
                " where module = 'login' and section = 'inactOrg' and entry = :entry";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ENTRY, (Math.round((Math.random() * (2 - entry) + entry))));
        try {
            return jdbcTemplate.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return AUTH_ERROR;
        }
    }

    public String getInactiveUserErrorMessage(int entry) {
        String sql = SELECT_MESSAGE +
                CUST_PORTAL_TABLE +
                " where module = 'login' and section = 'inactUser' and entry = :entry";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ENTRY, Math.min(entry,2));
        try {
            return jdbcTemplate.queryForObject(sql, params, String.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return AUTH_ERROR;
        }
    }
}
