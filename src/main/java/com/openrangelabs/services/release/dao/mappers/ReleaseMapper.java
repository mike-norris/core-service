package com.openrangelabs.services.release.dao.mappers;

import com.openrangelabs.services.release.model.Release;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class ReleaseMapper implements RowMapper<Release> {

    @Override
    public Release mapRow(ResultSet rs, int rowNum) throws SQLException {
        log.info("ReleaseMapper -- mapRow");
        log.info("Result set: " + rs);
        int release_id = rs.getInt("release_id");
        String publish_date = rs.getString("publish_date");
        String version = rs.getString("version");
        String pdf_location = rs.getString("pdf_location");
        String viewed = rs.getString("viewed");
        int year = rs.getInt("year");
        int month = rs.getInt("month");
        int day = rs.getInt("day");

        return new Release(release_id, publish_date, version,viewed, pdf_location, year,month,day);
    }
}
