package com.openrangelabs.services.roster.bloxops.dao;


import com.openrangelabs.services.authenticate.bloxops.dao.mappers.UserAccessMapper;
import com.openrangelabs.services.roster.bloxops.dao.mapper.*;
import com.openrangelabs.services.roster.entity.*;
import com.openrangelabs.services.roster.bloxops.dao.mapper.RosterSummaryMapper;
import com.openrangelabs.services.roster.bloxops.dao.mapper.RosterUserDatacenterMapper;
import com.openrangelabs.services.roster.bloxops.dao.mapper.RosterUserMapper;
import com.openrangelabs.services.roster.entity.RosterUser;
import com.openrangelabs.services.roster.entity.RosterUserDatacenter;
import com.openrangelabs.services.roster.entity.RosterUserSecurity;
import com.openrangelabs.services.roster.entity.UserAccess;
import com.openrangelabs.services.roster.model.RosterSummary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.openrangelabs.services.config.BloxopsDBConfig.BLOXOPS_NAMEDJDBCTEMPLATE;

@Slf4j
@Repository
public class RosterBloxopsDAO {

    @Autowired
    @Qualifier(BLOXOPS_NAMEDJDBCTEMPLATE)
    NamedParameterJdbcTemplate jdbcTemplate;

    String USERID = "userId";
    String STATUS = "status";
    String ORGID = "orgId";
    String EMAIL = "email";
    String CREATEDBY = "createdBy";
    String rosterUserId_String = "rosterUserId";
    String rosteruserIdLower = "rosteruserId";
    String DATACENTER_ID = "datacenterId";
    String ORGANIZATION_ID = "organizationId";
    String LIMIT_ONE = " limit 1 ";
    String ACCESS = "ACCESS";
    String SUCCESS = "SUCCESS";
    String fromRosterUser = " from rosteruser r ";
    String DataCenterJoinString = " LEFT JOIN rosteruser_datacenter d on r.id = d.rosteruser_id ";
    String OrganizationJoinString = " join organizations o on r.organization_id = o.organization_id";

    private int addRosterUserSecurity(RosterUserSecurity rosterUserSecurity) {
        String sql = "INSERT INTO rosterusersecurity (rosteruser_id, actor_id, eventtype, " +
                "eventstatus, eventmessage, createddatetime) " +
                "VALUES (:userId, :actorId, :type, :status, :message, now());";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(USERID, rosterUserSecurity.getRosteruserId());
        params.addValue("actorId", rosterUserSecurity.getActorId());
        params.addValue(STATUS, rosterUserSecurity.getEventStatus());
        params.addValue("message", rosterUserSecurity.getEventMessage());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, params, keyHolder, new String[] { "id" });
        return jdbcTemplate.update(sql, params);
    }

    public int getActiveRosterCount(long orgId) {
        String sql = "select count(ru) from rosteruser ru " +
                "where ru.organization_id = :orgId and ru.isactive = true";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ORGID, orgId);
        return jdbcTemplate.queryForObject(sql, params, Integer.class);
    }

    public List<UserAccess> findAccessByRosterUserId(long rosterUserId) {
        String sql = "select id, accesstype, eventstatus, eventtype, datacenterid, rosteruserid,userid ,created_dt " +
                "from useraccess where rosteruserid = :rosterUserId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(rosterUserId_String, rosterUserId);
        return jdbcTemplate.query(sql, params, new UserAccessMapper());
    }

    public List<RosterUser> findByOrganizationId(long orgId) throws EmptyResultDataAccessException {
        String sql = "select d.personnel_id , r.id, r.firstname, r.isactive, r.lastname, r.badgerequired, r.organization_id, " +
                "r.emailaddress, o.name as organization_name, r.position_title, r.created_by, " +
                "r.created_dt, r.authorization_by, r.authorization_dt , r.user_id" +
                fromRosterUser +
                DataCenterJoinString +
                OrganizationJoinString +
                " where r.organization_id = :orgId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ORGID, orgId);
        return jdbcTemplate.query(sql, params, new RosterUserMapper());
    }

    public RosterUser findByUserId(long userId) throws EmptyResultDataAccessException {
        String sql = "select d.personnel_id , r.id, r.firstname, r.isactive, r.lastname, r.badgerequired, r.user_id , " +
                "r.organization_id, r.emailaddress, o.name as organization_name, r.position_title, " +
                "r.created_by, r.created_dt, r.authorization_by, r.authorization_dt " +
                fromRosterUser +
                DataCenterJoinString +
                OrganizationJoinString +
                " where user_id = :userId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(USERID, userId);
        return jdbcTemplate.query(sql, params, new RosterUserMapper()).get(0);
    }
    public List<RosterSummary> getRosterSummary(long orgId) throws EmptyResultDataAccessException {
        String sql = "SELECT count(u.id) roster_count, d.datacenter_id datacenter_id " +
                "FROM rosteruser u " +
                "LEFT JOIN rosteruser_datacenter d on u.id = d.rosteruser_id " +
                "WHERE u.organization_id = :orgId and u.isactive = true " +
                "GROUP BY d.datacenter_id;";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ORGID, orgId);
        return jdbcTemplate.query(sql, params, new RosterSummaryMapper());
    }

    public long save(RosterUser rosterUser) {
        List<RosterUser> rosterUsers = new ArrayList<>();
        rosterUsers.add(rosterUser);
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(rosterUsers);
        String sql = "INSERT INTO rosteruser(firstname, lastname, emailaddress, isactive, badgerequired, organization_id, " +
                "position_title, created_by, created_dt, authorization_by, authorization_dt , user_id) " +
                "VALUES (:firstName, :lastName, :emailAddress, :isActive, :badgeRequired, :orgId, :positionTitle, " +
                ":createdBy, now(), null, null , :userId );";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("firstName", rosterUser.getFirstName());
        params.addValue("lastName", rosterUser.getLastName());
        params.addValue("emailAddress", rosterUser.getEmailAddress());
        params.addValue(EMAIL, rosterUser.getEmailAddress());
        params.addValue("isActive", rosterUser.getIsActive());
        params.addValue("badgeRequired", rosterUser.getBadgeRequired());
        params.addValue("orgId", rosterUser.getOrganizationId());
        params.addValue("positionTitle", rosterUser.getPositionTitle());
        params.addValue("createdBy", rosterUser.getCreatedBy());
        params.addValue(USERID, rosterUser.getUserId());
        int saved = jdbcTemplate.update(sql, params, keyHolder, new String[] { "id" });
        if (saved > 0) {
            try {
                RosterUserSecurity security = new RosterUserSecurity();
                long createdBy = rosterUser.getCreatedBy();
                security.setActorId(createdBy);

                Number key = keyHolder.getKey();
                if(key != null){
                    security.setRosteruserId(key.longValue());
                }

                security.setEventType(ACCESS);
                security.setEventStatus(SUCCESS);
                security.setEventMessage("Added user to roster");
                this.addRosterUserSecurity(security);
            } catch (Exception e) {
                String message = e.getMessage();
                log.error(message);
            }
        }
            Number key =  keyHolder.getKey();
            if(key != null) {
                return key.longValue();
            }else {
                return 0;
            }
    }

    public List<RosterUserDatacenter> findDatacentersByRosterId(long rosterUserId) throws EmptyResultDataAccessException {
        String sql = "select rosteruser_id, dc.name as datacenter, datacenter_id, escort_required, vendor, guest, orl_employee," +
                " status, created_by, created_dt" +
                " from rosteruser_datacenter" +
                " join datacenter dc on rosteruser_datacenter.datacenter_id=dc.id" +
                " where rosteruser_id = :id and status=1";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", rosterUserId);
        return jdbcTemplate.query(sql, params, new RosterUserDatacenterMapper());
    }

    public RosterUserDatacenter findRosterUserDatacenterByRosterId(Long rosterUserId, Long datacenterId) throws EmptyResultDataAccessException {
        String sql = "select rd.rosteruser_id, dc.name as datacenter, rd.datacenter_id, rd.escort_required, rd.vendor, " +
                "rd.guest, rd.orl_employee, rd.status, rd.created_by, rd.created_dt " +
                "from rosteruser_datacenter rd " +
                "join datacenter dc on rd.datacenter_id=dc.id " +
                "where rd.rosteruser_id = :id and dc.id = :dcid;";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", rosterUserId);
        params.addValue("dcid", datacenterId);
        List<RosterUserDatacenter> rosteruserDatacenterList = jdbcTemplate.query(sql, params, new RosterUserDatacenterMapper());
        try {
            return rosteruserDatacenterList.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public int addRosterUserToDC(RosterUserDatacenter rosterUserDatacenter) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        String sql = "INSERT INTO rosteruser_datacenter (rosteruser_id, datacenter_id, escort_required, " +
                "vendor, guest, orl_employee, status, created_by, created_dt) " +
                "VALUES (:rosteruserId, :datacenterId, :escortRequired, :vendor, :guest, :orlEmployee, " +
                ":status, :createdBy, now()) ON CONFLICT DO NOTHING;";
        params.addValue(rosteruserIdLower, rosterUserDatacenter.getRosteruserId());
        params.addValue(DATACENTER_ID, rosterUserDatacenter.getDatacenterId());
        params.addValue("escortRequired", rosterUserDatacenter.isEscortRequired());
        params.addValue("vendor", rosterUserDatacenter.isVendor());
        params.addValue("guest", rosterUserDatacenter.isGuest());
        params.addValue("orlEmployee", rosterUserDatacenter.isOrlEmployee());
        params.addValue(STATUS, rosterUserDatacenter.getStatus());
        params.addValue(CREATEDBY, rosterUserDatacenter.getCreatedBy());
        int created = jdbcTemplate.update(sql, params);
        if (created > 0) {
            try {
                RosterUserSecurity security = new RosterUserSecurity();
                long createdBy = rosterUserDatacenter.getCreatedBy();
                security.setActorId(createdBy);
                security.setRosteruserId(rosterUserDatacenter.getRosteruserId());
                security.setEventType(ACCESS);
                security.setEventStatus(SUCCESS);
                security.setEventMessage("Added user to datacenter");
                this.addRosterUserSecurity(security);
            } catch (Exception e) {
                String message = e.getMessage();
                log.error(message);
            }
        }
        return created;
    }

    public int saveAllUserAccess(List<UserAccess> userAccess) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(userAccess);
        String sql = "INSERT INTO useraccess(id, eventstatus, eventtype, accesstype, datacenterid, rosteruserid, userid, created_dt) " +
                "    VALUES (:id, :eventStatus, :eventType, :accessType, :datacenterId, :rosterUserId ,:userId, CURRENT_TIMESTAMP);";
        int[] updateCounts = jdbcTemplate.batchUpdate(sql, batch);
        return updateCounts.length;
    }

    public RosterUser getRosterUser(Long id) {
        String sql = "select r.*, o.name as organization_name, rd.personnel_id " + fromRosterUser +
                " join organizations o on r.organization_id=o.organization_id " +
                " join rosteruser_datacenter rd on r.id=rd.rosteruser_id" +
                " where r.id=:id;"; // +LIMIT_ONE+";";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        log.info("Find user SQL: "+sql);
        log.info("Find User Params: "+params);
        //return jdbcTemplate.queryForObject(sql, params, new RosterUserMapper());
        List<RosterUser> rosterUserList = jdbcTemplate.query(sql, params, new RosterUserMapper());
        if (rosterUserList.size() < 1) {
            log.warn("Find user Result: No Records Found");
            return null;
        }
        log.info("Find user Result: Record Found");
        return rosterUserList.get(0);
    }

    public RosterUser getRosterUserByEmail(String email ,long orgId) {
        log.info("Checking for roster user with email " + email + " and Org :" + orgId);
        if (orgId == 0L) {
            return getRosterUserByEmail(email);
        }
        String sql = "select d.personnel_id , r.id, r.badgerequired, r.organization_id, r.emailaddress, r.firstname, r.isactive, r.lastname, " +
                "r.position_title, r.created_by, r.created_dt, r.authorization_by, r.authorization_dt, r.user_id, " +
                "o.name as organization_name " +
                fromRosterUser +
                DataCenterJoinString +
                "join organizations o on r.organization_id=o.organization_id " +
                "where r.organization_id=:orgId and r.emailaddress=:email "+LIMIT_ONE+";";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ORGID, orgId);
        params.addValue(EMAIL, email);
        log.info("find User SQL: "+sql);
        log.info("find User Params: "+params);
        return jdbcTemplate.queryForObject(sql, params, new RosterUserMapper());
    }

    public RosterUser getRosterUserByEmail(String email) {
        String sql = "select d.personnel_id , r.id, r.badgerequired, r.organization_id, r.emailaddress, r.firstname, r.isactive, r.lastname, " +
                "r.position_title, r.created_by, r.created_dt, r.authorization_by, r.authorization_dt, r.user_id, " +
                "o.name as organization_name " +
                fromRosterUser +
                DataCenterJoinString +
                "join organizations o on r.organization_id=o.organization_id " +
                "where r.emailaddress=:email order by r.id desc "+LIMIT_ONE+";";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(EMAIL, email);
        log.info("find User SQL: "+sql);
        log.info("find User Params: "+params);
        return jdbcTemplate.queryForObject(sql, params, new RosterUserMapper());
    }

}
