package com.openrangelabs.services.authenticate.permission.bloxops.dao;

import com.openrangelabs.services.authenticate.permission.bloxops.dao.mapper.*;
import com.openrangelabs.services.authenticate.permission.bloxops.dao.mapper.*;
import com.openrangelabs.services.authenticate.permission.enitity.Permission;
import com.openrangelabs.services.authenticate.permission.enitity.PermissionShort;
import com.openrangelabs.services.authenticate.permission.model.ComponentPermission;
import com.openrangelabs.services.authenticate.permission.model.PagePermission;
import com.openrangelabs.services.authenticate.permission.model.Organization;
import com.openrangelabs.services.authenticate.permission.model.OrganizationService;
import com.openrangelabs.services.organization.bloxops.dao.mapper.OrganizationUserMapper;
import com.openrangelabs.services.organization.model.OrganizationUser;
import com.openrangelabs.services.ticket.bloxops.mapper.TicketTypeMapper;
import com.openrangelabs.services.ticket.model.TicketType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

import static com.openrangelabs.services.config.BloxopsDBConfig.BLOXOPS_NAMEDJDBCTEMPLATE;


@Repository
public class PermissionDAO {

    @Autowired
    @Qualifier(BLOXOPS_NAMEDJDBCTEMPLATE)
    NamedParameterJdbcTemplate jdbcTemplate;

    private static String USER_ID = "userId";
    private static String SERVICE_ID = "serviceId";
    private static String ORG_ID = "orgId";
    private static String ORGANIZATION_ID = "organizationId";
    
    public List<OrganizationService> getServices(long userId) throws EmptyResultDataAccessException {
        String sql = "select o.organization_id, o.name as organization_name , s.name ,o.org_code, o.beta , s.service_id, s.display_name, os.active, os.datacenter_id, d.name AS datacenter_name , ou.role "+
        "from organization_users ou "+
        "left join organizations o on o.organization_id = ou.organization_id "+
        "left join organization_services os on o.organization_id = os.organization_id "+
        "left join services s on os.service_id = s.service_id "+
        "JOIN datacenter d ON os.datacenter_id = d.id "+
        "inner join permissions p on (p.organization_id=o.organization_id and p.service_id=s.service_id and p.user_id=ou.user_id and p.access::integer > 0 and p.enabled = true) "+
        "where ou.user_id = :userId and o.status = 1 "+
        "group by o.organization_id, ou.role, s.service_id, p.enabled, o.name, o.icon, s.display_name ,os.active ,os.datacenter_id , d.name "+
        "order by o.organization_id, s.service_id asc ;";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(USER_ID, userId);
        return jdbcTemplate.query(sql, params, new OrganizationServiceMapper());
    }

    public TicketType getTicketType(String ticketType) {
        String sql = "select type, changegear, bonita" +
                " from ticket_types" +
                " where type = :ticketType ;";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ticketType", ticketType);
        return jdbcTemplate.query(sql, params, new TicketTypeMapper()).get(0);
    }

    public List<PagePermission> getServicePermissions(long userId, long serviceId, long organizationId) throws EmptyResultDataAccessException {
        String sql = "select pg.page_id pgPageId, pg.name pgName, pg.display_name pgDisplayName, " +
                "p.id pId, p.enabled penabled, p.access pAccess, p.attribute pAttribute " +
                "from permissions p " +
                "JOIN organizations o on p.organization_id = o.organization_id " +
                "JOIN pages pg on p.page_id = pg.page_id " +
                "where p.component_id is null and p.user_id = :userId " +
                "and p.service_id = :serviceId " +
                "and p.organization_id = :organizationId and p.enabled = true and o.status = 1 " +
                "and p.enabled = true and p.access::integer > 0 " +
                "and (pg.display_name != '' or pg.display_name is not null);";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(USER_ID, userId);
        params.addValue(SERVICE_ID, serviceId);
        params.addValue(ORGANIZATION_ID, organizationId);
        return jdbcTemplate.query(sql, params, new PagePermissionMapper());

    }

    public List<PagePermission> getAllServicePermissions(long userId, long organizationId) throws EmptyResultDataAccessException {
        String sql = "select pg.page_id pgPageId, pg.name pgName, pg.display_name pgDisplayName, " +
                "p.id pId, p.enabled penabled, p.access pAccess, p.attribute pAttribute  " +
                "from permissions p " +
                "JOIN organizations o on p.organization_id = o.organization_id " +
                "JOIN pages pg on p.page_id = pg.page_id " +
                "where p.component_id is null and p.user_id = :userId " +
                "and p.organization_id = :organizationId and p.enabled = true and o.status = 1 " +
                "and p.enabled = true and p.access::integer > 0 " +
                "and (pg.display_name != '' or pg.display_name is not null);";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(USER_ID, userId);
        params.addValue(ORGANIZATION_ID, organizationId);
        return jdbcTemplate.query(sql, params, new PagePermissionMapper());
    }

    public List<ComponentPermission> getPageComponentPermissions(long userId, long pageId, long organizationId) throws EmptyResultDataAccessException {
        String sql = "select c.name as cname, display_name, lpad(default_access::text, 4,'0') as default_access, (select s.display_name module from services s where p.service_id = s.service_id   ), " +
                "lpad(access::text, 4,'0') as access, pc.identifier as identifier " +
                "from pages_components pc " +
                "join components c on pc.component_id = c.component_id " +
                "join permissions p on p.page_id = pc.page_id and p.component_id = c.component_id and p.organization_id = :organizationId " +
                "where p.page_id = :pageId and p.user_id = :userId and p.enabled = true and  p.enabled = true " +
                "group by c.name, display_name, default_access, access, pc.identifier ,p.service_id ";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(USER_ID, userId);
        params.addValue("pageId", pageId);
        params.addValue(ORGANIZATION_ID, organizationId);
        return jdbcTemplate.query(sql, params, new ComponentPermissionMapper());
    }


    public boolean isOrganizationServiceActive(long serviceId, long organizationId) {
        String sql = "select os.organization_id " +
                "from organization_services os " +
                "left join organizations o on os.organization_id = o.organization_id " +
                "where os.service_id = :serviceId and o.organization_id = :organizationId and o.status=1; ";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(SERVICE_ID, serviceId);
        params.addValue(ORGANIZATION_ID, organizationId);
        List<Integer> result = jdbcTemplate.queryForList(sql, params, Integer.class);
        return !result.isEmpty();
    }

    public List<Organization> getOrganizationsForUser(long userId) {
        String sql = "select o.icon, o.name, o.organization_id" +
                " from organization_users ou" +
                " LEFT JOIN organizations o on ou.organization_id = o.organization_id" +
                " where o.status = 1 and ou.user_id = :userId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(USER_ID, userId);
        return jdbcTemplate.query(sql, params, new OrganizationMapper());
    }


    public long getComponentId(String componentName) {
        String sql = "select component_id" +
                " from components" +
                " where name = :componentName limit 1;";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("componentName", componentName);
        return jdbcTemplate.queryForObject(sql, params, Long.class);
    }

    public void updateUsersPermissions(long componentId ,long pageId ,int access , long serviceId , long userId, long organizationId){

            String sql = "UPDATE permissions " +
                    "SET access=lpad(:access::text, 4,'0') " +
                    "WHERE component_id=:componentId " +
                    "AND page_id=:pageId AND service_id=:serviceId " +
                    "AND user_id =:userId AND organization_id=:organizationId;";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("componentId", componentId);
        params.addValue("pageId", pageId);
        params.addValue("access", access);
        params.addValue(SERVICE_ID, serviceId);
        params.addValue(USER_ID, userId);
        params.addValue(ORGANIZATION_ID, organizationId);

        jdbcTemplate.update(sql, params);

        }

    public void updateUsersModulePermissions(long userId, long orgId, int access, long serviceId, boolean enabled ) {
        String sql = "UPDATE permissions " +
                "SET access=:access , enabled=:enabled " +
                "WHERE organization_id=:orgId AND service_id=:serviceId AND user_id =:userId; ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("orgId", orgId);
        params.addValue("enabled", enabled);
        params.addValue("access", access);
        params.addValue(SERVICE_ID, serviceId);
        params.addValue(USER_ID, userId);
        jdbcTemplate.update(sql, params);
    }

    public long getModulePermission(long userId , long orgId , long serviceId) {
        String sql = "select count(*)" +
                " from permissions" +
                " where user_id = :userId and organization_id = :orgId and service_id = :serviceId ;";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(USER_ID, userId);
        params.addValue(ORG_ID, orgId);
        params.addValue(SERVICE_ID, serviceId);
        return jdbcTemplate.queryForObject(sql, params, Long.class);
    }

    public boolean hasUserPermsSet(int userId , Long orgId ){
        String sql = "select * from getuserperm(:userId::integer, :orgId::integer);";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(USER_ID, userId);
        params.addValue(ORG_ID, orgId);
        List<Permission>  permissions = jdbcTemplate.query(sql ,params, new PermissionMapper());
        if (permissions.size() > 0) {
            return true;
        }
        return false;
    }

    public boolean isActive(int userId, Long orgId) {
        String sql = "select * from organization_users ou where ou.user_id=:userId and ou.organization_id=:orgId;";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(USER_ID, userId);
        params.addValue(ORG_ID, orgId);
        List<OrganizationUser> orgUser = jdbcTemplate.query(sql ,params, new OrganizationUserMapper());
            if (orgUser.size() > 0) {
                return orgUser.get(0).isActive();
            }
        return false;
    }

    public String getRole(int userId, Long orgId) {
        String sql = "select * from organization_users ou where ou.user_id=:userId and ou.organization_id=:orgId;";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(USER_ID, userId);
        params.addValue(ORG_ID, orgId);
        List<OrganizationUser> orgUser = jdbcTemplate.query(sql ,params, new OrganizationUserMapper());
            if (orgUser.size() > 0) {
                return orgUser.get(0).getRole();
            }
        return null;
    }

    public List<Permission> setDefaultPerms(int userId , int orgId ){
        String sql = "select * from useradddefaultperm(:userId ,:orgId);";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(USER_ID, userId);
        params.addValue(ORG_ID, orgId);
        return jdbcTemplate.query(sql ,params, new PermissionMapper());
    }

    public Boolean flushUserPerms(int userId , int orgId ){
        String sql = "select * from flushuserperm(:userId ,:orgId);";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(USER_ID, userId);
        params.addValue(ORG_ID, orgId);
        return jdbcTemplate.queryForObject(sql ,params, Boolean.class);
    }

    public List<Permission> setDefaultOwnerPerms(int userId , int orgId ){
        String sql = "select * from useradddefaultownerperm(:userId ,:orgId);";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(USER_ID, userId);
        params.addValue(ORG_ID, orgId);
        return jdbcTemplate.query(sql ,params, new PermissionMapper());
    }

    public List<Permission> setBillingPerms(int userId , int orgId ){
        String sql = "select * from useraddbillingperm(:userId ,:orgId , false );";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(USER_ID, userId);
        params.addValue(ORG_ID, orgId);
        return jdbcTemplate.query(sql ,params, new PermissionMapper());
    }

    public List<Permission> setBillingOwnerPerms(int userId , int orgId ){
        String sql = "select * from useraddbillingownerperm(:userId ,:orgId , false );";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(USER_ID, userId);
        params.addValue(ORG_ID, orgId);
        return jdbcTemplate.query(sql ,params, new PermissionMapper());
    }

    public List<Permission> setStoragePerms(int userId , int orgId ){
        String sql = "select * from useraddstorageperm(:userId ,:orgId );";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(USER_ID, userId);
        params.addValue(ORG_ID, orgId);
        return jdbcTemplate.query(sql ,params,new PermissionMapper());
    }

    public Boolean checkUserPermission(int userId, String companyId, String componentName, String pageName) throws EmptyResultDataAccessException {
        String sql = "select p.access from permissions p " +
                " left join components c on p.component_id=c.component_id " +
                " where p.user_id = :userId and p.organization_id = :companyId::int " +
                " and p.page_id = (select page_id from pages where name = :pageName) " +
                " and c.name = :componentName limit 1";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(USER_ID, userId);
        params.addValue("companyId", companyId);
        params.addValue("pageName", pageName);
        params.addValue("componentName", componentName);
        int access = jdbcTemplate.queryForObject(sql, params, Integer.class) ;

        if(access == 1111){
            return true;
        }else{
            return false;
        }
    }

    public List<PermissionShort> getPermissionList() {
        String sql ="select c.display_name as description, p.display_name as page, s.display_name as module , c.name as name " +
                "from components c " +
                "join pages_components pc on c.component_id=pc.component_id " +
                "join pages p on pc.page_id=p.page_id " +
                "join services s on p.service_id = s.service_id;";
        MapSqlParameterSource params = new MapSqlParameterSource();

        return jdbcTemplate.query(sql, params, new PermissionShortMapper());
    }
}
