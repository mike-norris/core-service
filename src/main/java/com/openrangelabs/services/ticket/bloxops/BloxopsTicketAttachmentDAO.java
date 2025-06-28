package com.openrangelabs.services.ticket.bloxops;


import com.openrangelabs.services.ticket.bloxops.mapper.AttachmentHashMapper;
import com.openrangelabs.services.ticket.model.AttachmentHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import static com.openrangelabs.services.config.BloxopsDBConfig.BLOXOPS_NAMEDJDBCTEMPLATE;


@Repository
public class BloxopsTicketAttachmentDAO {


    @Autowired
    @Qualifier(BLOXOPS_NAMEDJDBCTEMPLATE)
    NamedParameterJdbcTemplate jdbcTemplate;

    public long saveAttachmentHash(String hash, long orgId, String objectKey, String mimeType, String filename) {
        String sql = "INSERT INTO ticket_attachment_hash (hash, organization_id, object_key, mime_type, filename ) " +
                " VALUES (:hash, :orgId, :objectKey, :mimeType, :filename);";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("hash", hash);
        params.addValue("mimeType", mimeType);
        params.addValue("orgId", orgId);
        params.addValue("objectKey", objectKey);
        params.addValue("filename", filename);
        return jdbcTemplate.update(sql, params);
    }

    public AttachmentHash getAttachmentHash(String hash) {
        String sql = "select trim(hash) hash, organization_id, trim(object_key) object_key, mime_type, filename" +
                " from ticket_attachment_hash" +
                " where hash = :hash";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("hash", hash);
        return jdbcTemplate.query(sql, params, new AttachmentHashMapper()).get(0);
    }

    public AttachmentHash getAttachmentKey(String key) {
        String sql = "select trim(hash) hash, organization_id, trim(object_key) object_key, mime_type, filename" +
                " from ticket_attachment_hash" +
                " where object_key = :key";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("key", key);
        return jdbcTemplate.query(sql, params, new AttachmentHashMapper()).get(0);
    }
}
