package com.openrangelabs.services.datacenter.bloxops.dao.mapper;

import com.openrangelabs.services.datacenter.entity.DataCenterUserAccessLog;
import com.openrangelabs.services.datacenter.entity.Datacenter;
import com.openrangelabs.services.operations.dao.mappers.LinkMapper;
import com.openrangelabs.services.operations.model.Link;
import com.openrangelabs.services.operations.model.Service;
import com.openrangelabs.services.organization.bloxops.dao.mapper.DatacenterMapper;
import com.openrangelabs.services.organization.bloxops.dao.mapper.ServiceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.openrangelabs.services.config.BloxopsDBConfig.BLOXOPS_NAMEDJDBCTEMPLATE;

@Repository
public class DatacenterBloxopsDAO {

    @Autowired
    @Qualifier(BLOXOPS_NAMEDJDBCTEMPLATE)
    NamedParameterJdbcTemplate jdbcTemplate;

    public List<DataCenterUserAccessLog> getDatacenterUserAccessLogs(int cardNumber) {
        String sql = "select ra.* from rosteruser_access ra " +
                "join badges b on ra.card_number=b.card_number " +
//                "join badge_access_points bap on ra.access_point_guid = bap.guid " +
                "where b.card_number = :cardNumber" +
                " ORDER BY ra.message_dt DESC " +
                " LIMIT 15 ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("cardNumber", cardNumber);
        return jdbcTemplate.query(sql, params, new DataCenterUserAccessLogMapper());
    }

    /**
     * TODO
     * Change this to get the records by organization
     * @param organizationId
     * @return
     */
    public List<DataCenterUserAccessLog> getDatacenterUserAccessLogsByOrganization(Long organizationId, int days) {
        String sql = "select distinct(ra.guid), ra.partition_id, ra.message_dt, ra.personnel_guid, ra.personnel_name, ra.access_point_guid, ra.access_point_name, " +
                "                ra.message_text, ra.message_type, ra.status, ra.card_number, b.datacenter, r.lastname, r.firstname, ra.access_point_name as access_point  " +
                "from rosteruser_access ra " +
                "left join badges b on ra.card_number=b.card_number " +
                "left join rosteruser r on b.rosteruser_id = r.id " +
//                "join badge_access_points bap on ra.access_point_guid = bap.guid " +
                "where r.organization_id = :organizationId and ra.message_dt > current_timestamp - ( :days || ' days')::interval " +
                "order by ra.message_dt desc " +
                "limit 20";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("organizationId", organizationId);
        params.addValue("days", days);
        return jdbcTemplate.query(sql, params, new DataCenterUserAccessLogMapper());
    }

    public Datacenter getDatacenter(Long datacenterId) {
        String sql = "select id, physical_city, name, physical_state , time_zone ,manager_email_address , manager_name , type " +
                " from datacenter" +
                " where id = :datacenterId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("datacenterId", datacenterId);
        return jdbcTemplate.query(sql, params, new DatacenterMapper()).get(0);
    }

    public List<Service> getAllServices() {
        String sql = "select * " +
                " from services;";

        MapSqlParameterSource params = new MapSqlParameterSource();

        return jdbcTemplate.query(sql, params, new ServiceMapper());
    }

    public Datacenter getDatacenterByName(String name) {
        String sql = "select * " +
                " from datacenter  " +
                " where name = :name ;";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", name);
        return jdbcTemplate.query(sql, params, new DatacenterMapper()).get(0);
    }

    public List<Link> getServices() throws EmptyResultDataAccessException {
        String sql = "select l.id, l.url, l.name,l.description, l.name ,l.department ,l.approved " +
                " from links l ";

        MapSqlParameterSource params = new MapSqlParameterSource();

        return jdbcTemplate.query(sql, params, new LinkMapper());
    }
}
