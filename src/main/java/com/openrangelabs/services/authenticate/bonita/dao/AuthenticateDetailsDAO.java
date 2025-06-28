package com.openrangelabs.services.authenticate.bonita.dao;

import com.openrangelabs.services.authenticate.bonita.dao.mappers.UserIdMapper;
import com.openrangelabs.services.authenticate.bonita.dao.mappers.UserNameMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

import static com.openrangelabs.services.config.BonitaDBConfig.BONITA_NAMEDJDBCTEMPLATE;


@Repository
public class AuthenticateDetailsDAO {

    @Autowired
    @Qualifier(BONITA_NAMEDJDBCTEMPLATE)
    NamedParameterJdbcTemplate jdbcTemplate;

    
    public String getUserName(String email) {
        String sql = "select u.username" +
                " from user_contactinfo as c join user_ as u on c.userid = u.id" +
                " where email = :email and personal = false and enabled = true";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", email);
        List<String> result = jdbcTemplate.query(sql, params, new UserNameMapper());
        return result.isEmpty() ? null : result.get(0);
    }

    public int getUserId(String email) {
        String sql = "select u.id" +
                " from user_contactinfo as c join user_ as u on c.userid = u.id" +
                " where email = :email and personal = false and enabled = true";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", email);
        List<String> result = jdbcTemplate.query(sql, params, new UserIdMapper());
        return result.isEmpty() ? null : Integer.valueOf(result.get(0));
    }
}