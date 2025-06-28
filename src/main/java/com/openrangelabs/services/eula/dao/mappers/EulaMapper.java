package com.openrangelabs.services.eula.dao.mappers;

import com.openrangelabs.services.eula.entity.Eula;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class EulaMapper implements RowMapper<Eula> {

    @Override
    public Eula mapRow(ResultSet rs, int rowNum) throws SQLException {
        log.info("EulaMapper -- mapRow");
        log.info("Result set: " + rs);
        int eulaId = rs.getInt("eula_id");
        String createdDateTime = rs.getString("created_date_time");
        String updatedDateTime = rs.getString("updated_date_time");
        String version = rs.getString("version");
        String publishedDate = rs.getString("published_date");
        String pdfLocation = rs.getString("pdf_location");
        String htmlLocation = rs.getString("html_location");

        return new Eula( eulaId, createdDateTime, version,  publishedDate, updatedDateTime, htmlLocation,pdfLocation);
    }


}
