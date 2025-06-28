package com.openrangelabs.services.release.dao;


import com.openrangelabs.services.release.dao.mappers.ReleaseMapper;
import com.openrangelabs.services.release.model.Release;
import com.openrangelabs.services.release.model.ReleaseRecordRequest;
import com.openrangelabs.services.release.model.ReleaseViewedRecordRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.openrangelabs.services.config.BloxopsDBConfig.BLOXOPS_NAMEDJDBCTEMPLATE;

@Repository
public class ReleaseBloxopsDAO {

    @Autowired
    @Qualifier(BLOXOPS_NAMEDJDBCTEMPLATE)
    NamedParameterJdbcTemplate jdbcTemplate;

    public List<Release> findAll(int user_id) {
        String sql = "select r.release_id," +
                " EXTRACT(YEAR FROM r.release_date) as year," +
                " EXTRACT(MONTH FROM r.release_date) as month," +
                " EXTRACT(DAY FROM r.release_date) as day," +
                " r.publish_date, r.release_date, r.version, r.pdf_location, v.created_dt as \"viewed\"" +
                " from releases r\n" +
                " LEFT JOIN release_views v ON v.release_id=r.release_id AND v.user_id=:user_id" +
                " where" +
                " r.publish_date IS NOT NULL" +
                " AND r.publish_date <= now()" +
                " order by release_date ASC;";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", user_id);
        return jdbcTemplate.query(sql, params, new ReleaseMapper());

    }
    public List<Release> findLatest() {
        String sql = "select r.release_id," +
                " EXTRACT(YEAR FROM r.release_date) as year," +
                " EXTRACT(MONTH FROM r.release_date) as month," +
                " EXTRACT(DAY FROM r.release_date) as day," +
                " r.publish_date, r.release_date, r.version, r.pdf_location, v.created_dt as \"viewed\"" +
                " from releases r\n" +
                " LEFT JOIN release_views v ON v.release_id=r.release_id " +
                " where" +
                " r.publish_date IS NOT NULL" +
                " AND r.publish_date <= now()" +
                " order by release_date DESC " +
                " LIMIT 1;";

        MapSqlParameterSource params = new MapSqlParameterSource();
        return jdbcTemplate.query(sql, params, new ReleaseMapper());
    }


    public void addViewedReleaseRecord(ReleaseViewedRecordRequest addRecordRequest) {
        String sql = "INSERT INTO release_views (" +
                " release_id, user_id, created_dt)" +
                " VALUES (:releaseId, :userId, now())" +
                " ON CONFLICT ON CONSTRAINT pk_release_user DO NOTHING;";

        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("userId", addRecordRequest.getUserId());
        params.addValue("releaseId", addRecordRequest.getReleaseId());
        jdbcTemplate.update(sql, params);


    }


    public void addReleaseRecord(ReleaseRecordRequest recordRequest) {
        String sql = "INSERT INTO releases (" +
        " publish_date, release_date, version, pdf_location)" +
        " VALUES (:publishDate, :releaseDate, :version, :pdfLocation)"+
        " ON CONFLICT ON CONSTRAINT uniq_version DO NOTHING;";

        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("publishDate", recordRequest.getPublishDate());
        params.addValue("releaseDate", recordRequest.getReleaseDate());
        params.addValue("version", recordRequest.getVersion());
        params.addValue("pdfLocation", recordRequest.getPdfLocation());
        jdbcTemplate.update(sql, params);
    }

    public void updateReleaseRecord(int release_id, ReleaseRecordRequest recordRequest) {
        String sql = "UPDATE releases"+
                " SET publish_date=:publishDate, release_date=:releaseDate, version=:version, pdf_location=:pdfLocation, updated_dt=(now())"+
                " WHERE release_id=:release_id;";

        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("publishDate", recordRequest.getPublishDate());
        params.addValue("releaseDate", recordRequest.getReleaseDate());
        params.addValue("version", recordRequest.getVersion());
        params.addValue("pdfLocation", recordRequest.getPdfLocation());
        params.addValue("release_id", release_id);
        jdbcTemplate.update(sql, params);

    }

    public void deleteReleaseRecord(int release_id) {
        String sql = "UPDATE releases"+
        " SET deleted_dt=now()"+
        " WHERE release_id=:release_id;";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("release_id", release_id);
        jdbcTemplate.update(sql, params);
    }
}
