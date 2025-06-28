package com.openrangelabs.services.user.bloxops.dao;


import com.openrangelabs.services.user.bloxops.dao.mapper.PendingUserMapper;
import com.openrangelabs.services.user.repository.PendingUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.openrangelabs.services.config.BloxopsDBConfig.BLOXOPS_NAMEDJDBCTEMPLATE;

@Repository
public class PendingUserBloxopsDAO {

    @Autowired
    @Qualifier(BLOXOPS_NAMEDJDBCTEMPLATE)
    NamedParameterJdbcTemplate jdbcTemplate;

    String ORGID = "orgId";

    public PendingUser findById(long orgId) {
        String sql = "select id, organization_id, emailaddress, firstname, lastname, isprocessed, timestamp" +
                " from pendinguser r where r.organization_id = :orgId and r.isprocessed = true";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ORGID , orgId);
        return jdbcTemplate.query(sql, params, new PendingUserMapper()).get(0);
    }

    public List<PendingUser> findByOrganizationId(long orgId) throws EmptyResultDataAccessException {
        String sql = "select id, organization_id, emailaddress, firstname, lastname, isprocessed, timestamp" +
                " from pendinguser r where r.organization_id = :orgId and r.isprocessed = true";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ORGID , orgId);
        return jdbcTemplate.query(sql, params, new PendingUserMapper());
    }

    public long save(PendingUser pendingUser) throws EmptyResultDataAccessException {
        String sql = "insert into pendinguser (organization_id, emailaddress, firstname, lastname, isprocessed, timestamp)" +
                " values (:orgId, :emailAddress, :firstname, :lastname, :isProcessed, :timestamp)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ORGID , pendingUser.getOrganizationId());
        params.addValue("emailAddress", pendingUser.getEmailAddress());
        params.addValue("firstname", pendingUser.getFirstName());
        params.addValue("lastname", pendingUser.getLastName());
        params.addValue("isProcessed", pendingUser.getIsProcessed());
        params.addValue("timestamp", pendingUser.getTimeStamp());
        jdbcTemplate.update(sql, params, keyHolder, new String[] { "id" });
        if( keyHolder.getKey() != null ){
            Number key = keyHolder.getKey();
            if(key != null) {
                return key.longValue();
            }else{
                return 0;
            }
        }else{
            return 0;
        }
    }
}
