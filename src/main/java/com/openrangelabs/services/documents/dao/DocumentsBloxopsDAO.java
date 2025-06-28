package com.openrangelabs.services.documents.dao;

import com.openrangelabs.services.documents.entity.Document;
import com.openrangelabs.services.documents.mappers.DocumentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

import static com.openrangelabs.services.config.BloxopsDBConfig.BLOXOPS_NAMEDJDBCTEMPLATE;

@Repository
public class DocumentsBloxopsDAO {

    @Autowired
    @Qualifier(BLOXOPS_NAMEDJDBCTEMPLATE)
    NamedParameterJdbcTemplate jdbcTemplate;

    public void writeDocumentToDB(String documentName, String documentKey, String documentLocation, String mime, Boolean s3, Boolean local, long organizationId, int userId, Long rosterId , String s3Container) {
        String sql = "INSERT INTO documents (document_name,document_key, document_location, mime, s3, local, " +
                " organization_id, user_id, roster_id,s3_container ,created_by)" +
                "VALUES (:documentName, :documentKey, :documentLocation, :mime, :s3, :local, :organizationId, :userId, :rosterId, :s3Container,:userId)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("documentName", documentName);
        params.addValue("documentKey", documentKey);
        params.addValue("documentLocation", documentLocation);
        params.addValue("mime", mime);
        params.addValue("s3", s3);
        params.addValue("local", local);
        params.addValue("organizationId", organizationId);
        params.addValue("userId", userId);
        params.addValue("rosterId", rosterId);
        params.addValue("s3Container", s3Container);
        jdbcTemplate.update(sql, params);
    }
    public List<Document> findDocumentsByRosterId(long rosterUserId) throws EmptyResultDataAccessException {
        String sql = "select d.roster_id, d.user_id, d.document_location,d.document_key, d.document_name, d.created_dt, d.deleted ,d.s3_container ,d.document_id " +
                " from documents d" +
                " where d.roster_id = :id ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", rosterUserId);
        return jdbcTemplate.query(sql, params, new DocumentMapper());
    }
    public List<Document> findDocumentsByUserId(long userId) throws EmptyResultDataAccessException {
        String sql = "select roster_id, user_id, document_location, document_key, document_name, created_dt, deleted ,s3_container ,document_id " +
                " from documents" +
                " where user_id = :id ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", userId);
        return jdbcTemplate.query(sql, params, new DocumentMapper());
    }

    public int updateDocumentToDeleted(Long documentId , Date deletedDt) {
        String sql = "update documents " +
                " set deleted = true ,deleted_dt = :deletedDt " +
                " where document_id = :documentId ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("documentId", documentId);
        params.addValue("deletedDt", deletedDt);
        return jdbcTemplate.update(sql, params);
    }

    public String getS3Key(String key) {
        String sql = "select s3_container " +
                " from documents" +
                " where document_key = :key ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("key", key);

        return jdbcTemplate.queryForObject(sql, params, String.class);
    }
}
