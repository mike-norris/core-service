package com.openrangelabs.services.eula.dao.mappers;

import com.openrangelabs.services.eula.model.EulaUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class EulaUserDetailsMapper implements RowMapper<EulaUserDetails> {
    @Override
    public EulaUserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        log.info("EulaUserDetailsMapper -- mapRow");
        log.info("Result set: " + rs);
        int eulaId = rs.getInt("eula_id");
        String createdDateTime = rs.getString("created_date_time");
        String updatedDateTime = rs.getString("updated_date_time");
        String version = rs.getString("version");
        String status = rs.getString("status");

        return new EulaUserDetails( eulaId, createdDateTime, version,  status, updatedDateTime);
    }
}
