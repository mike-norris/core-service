package com.openrangelabs.services.log.dao;

import com.openrangelabs.services.log.model.LogRecord;
import com.openrangelabs.services.operations.dao.mappers.LogRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.openrangelabs.services.config.OperationsDBConfig.OPS_NAMEDJDBCTEMPLATE;

@Repository
public class LoggingDAO {

    @Autowired
    @Qualifier(OPS_NAMEDJDBCTEMPLATE)
    NamedParameterJdbcTemplate jdbcTemplate;

    public int addUserLog(LogRecord logRecord) {
        String sql = "INSERT INTO logs_user (user_id, organization_id, description, type , created_dt)" +
                "VALUES (:userId, :orgId, :description, :type , now())";
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("userId", logRecord.getUser_id());
        params.addValue("orgId", logRecord.getOrganization_id());
        params.addValue("description", logRecord.getDescription());
        params.addValue("type", logRecord.getType());

        return jdbcTemplate.update(sql, params);

    }

    public int addSystemLog(LogRecord logRecord) {
        String sql = "INSERT INTO logs_system (user_id, organization_id, description, type , created_dt)" +
                "VALUES (:userId, :orgId, :description, :type , now())";
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("userId", logRecord.getUser_id());
        params.addValue("orgId", logRecord.getOrganization_id());
        params.addValue("description", logRecord.getDescription());
        params.addValue("type", logRecord.getType());

        return jdbcTemplate.update(sql, params);

    }

    public List<LogRecord> getUserLogs() {
        String sql = "select l.created_dt , l.user_id, l.organization_id , l.description , l.type " +
                " from logs_user l ";

        MapSqlParameterSource params = new MapSqlParameterSource();

        return jdbcTemplate.query(sql, params, new LogRecordMapper());
    }

    public List<LogRecord> getUserLogs(int userId) {
        String sql = "select l.created_dt , l.user_id, l.organization_id , l.description , l.type " +
                " from logs_user l " +
                " where l.user_id = :userId ";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        return jdbcTemplate.query(sql, params, new LogRecordMapper());
    }

    public List<LogRecord> getSystemLogs() {
        String sql = "select l.created_dt , l.user_id, l.organization_id , l.description , l.type " +
                " from logs_system l ";

        MapSqlParameterSource params = new MapSqlParameterSource();

        return jdbcTemplate.query(sql, params, new LogRecordMapper());
    }
}
