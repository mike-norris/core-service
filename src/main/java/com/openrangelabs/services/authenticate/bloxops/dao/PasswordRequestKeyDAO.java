package com.openrangelabs.services.authenticate.bloxops.dao;

import com.openrangelabs.services.authenticate.bloxops.dao.mappers.PasswordResetKeyMapper;
import com.openrangelabs.services.authenticate.model.PasswordResetKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import static com.openrangelabs.services.config.BloxopsDBConfig.BLOXOPS_NAMEDJDBCTEMPLATE;

@Repository
public class PasswordRequestKeyDAO {

    @Autowired
    @Qualifier(BLOXOPS_NAMEDJDBCTEMPLATE)
    NamedParameterJdbcTemplate jdbcTemplate;

    public PasswordResetKey findById(String key) throws EmptyResultDataAccessException {
        String sql = "select key, expired, timestamp::timestamptz as timestamp, username" +
                " from passwordresetkey" +
                " where key=:key;";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("key", key);
        return jdbcTemplate.query(sql, params, new PasswordResetKeyMapper()).get(0);
    }

    public int save(PasswordResetKey passwordResetKey) throws EmptyResultDataAccessException {
        String sql = "INSERT INTO passwordresetkey (key, expired, timestamp, username)" +
                " VALUES (:key, false, :timestamp, :username)" +
                " ON CONFLICT ON CONSTRAINT passwordresetkey_pkey" +
                " DO" +
                " UPDATE SET expired=:expired, timestamp=:timestamp, username=:username;";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("key", passwordResetKey.getKey());
        params.addValue("timestamp", passwordResetKey.getTimestamp());
        params.addValue("username", passwordResetKey.getUserName());
        params.addValue("expired", passwordResetKey.isExpired());
        return jdbcTemplate.update(sql, params);
    }
}
