package com.openrangelabs.services.organization.bloxops.dao;

import com.openrangelabs.services.authenticate.permission.bloxops.dao.mapper.OrganizationServiceMapper;
import com.openrangelabs.services.authenticate.permission.model.OrganizationService;
import com.openrangelabs.services.datacenter.entity.Datacenter;
import com.openrangelabs.services.organization.bloxops.dao.mapper.*;
import com.openrangelabs.services.organization.bloxops.dao.mapper.*;
import com.openrangelabs.services.organization.entity.BadgeAccessPoint;
import com.openrangelabs.services.organization.entity.PortalUserSession;
import com.openrangelabs.services.organization.model.*;
import com.openrangelabs.services.organization.model.*;
import com.openrangelabs.services.roster.bloxops.dao.mapper.RosterBadgeMapper;
import com.openrangelabs.services.roster.entity.RosterBadge;
import com.openrangelabs.services.roster.entity.RosterUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.openrangelabs.services.config.BloxopsDBConfig.BLOXOPS_NAMEDJDBCTEMPLATE;

@Slf4j
@Repository
public class BloxopsOrganizationDAO {

    @Autowired
    @Qualifier(BLOXOPS_NAMEDJDBCTEMPLATE)
    NamedParameterJdbcTemplate jdbcTemplate;
    String ORG_ID ="orgId";
    String USER_ID ="userId";
    String STATUS ="status";
    String SERVICE_ID ="serviceId";

    public Organization getOrganizationByOrganizationId(long orgId) {
        String sql = "select * " +
                " from organizations" +
                " where organization_id = :orgId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ORG_ID, orgId);
        return jdbcTemplate.query(sql, params, new OrganizationMapper()).get(0);
    }

    public boolean updateOrganization(Organization organization) {
        String sql = "update organizations " +
                " set salesforce_id = :salesforceId  " +
                " where organization_id = :orgId ";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("salesforceId", organization.getSalesforceId());
        params.addValue("orgId",organization.getOrganizationId());

        int success = jdbcTemplate.update(sql, params);
        if(success == 1){
            return true;
        }else{
            return false;
        }
    }

    public boolean addOrganization(Organization organization) {
        try {
            String sql = "select * from createcustomerwithbilling(:orgId::integer, :name::varchar, null,:intacctID::integer , 'intacct'::varchar);";
            MapSqlParameterSource params = new MapSqlParameterSource();

            params.addValue("name", organization.getName());
            params.addValue("orgId", organization.getOrganizationId());
            params.addValue("intacctID" , organization.getIntacctID());

            List<OrganizationDetails> organizations = jdbcTemplate.query(sql, params, new OrganizationDetailsMapper());
            if (!organizations.isEmpty()) {
                return true;
            } else {
                return false;
            }
        }catch (Exception e){
            log.error(e.getMessage());
            return false;
        }
    }

    public Organization getOrganizationByCompanyName(String companyName) {
        String sql = "select *" +
                " from organizations" +
                " where name = :companyName";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("companyName", companyName);
        return jdbcTemplate.query(sql, params, new OrganizationMapper()).get(0);
    }

    public Datacenter getDatacenter(long datacenterId) {
        String sql = "select id, city, name, state , timezone " +
                " from datacenter" +
                " where id = :datacenterId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("datacenterId", datacenterId);
        return jdbcTemplate.query(sql, params, new DatacenterMapper()).get(0);
    }

    public Datacenter getDatacenterByTag(String datacenterTag) {
        String sql = "select * " +
                " from datacenter" +
                " where name = Upper(:datacenterTag)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("datacenterTag", datacenterTag);
        return jdbcTemplate.query(sql, params, new DatacenterMapper()).get(0);
    }

    public List<OrganizationService> getOrganizationServices(long organizationId) {
        String sql = "select o.organization_id,  o.name as organization_name , s.name, o.org_code,o.beta,s.service_id, '' as role ," +
                "    s.display_name, os.active, os.datacenter_id, d.name AS datacenter_name " +
                " FROM organization_services os " +
                "         JOIN organizations o ON os.organization_id = o.organization_id " +
                "         JOIN services s ON os.service_id = s.service_id " +
                "         JOIN datacenter d ON os.datacenter_id = d.id " +
                "WHERE os.organization_id = :organizationId " +
                "  AND os.active = TRUE; ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("organizationId", organizationId);
        return jdbcTemplate.query(sql, params, new OrganizationServiceMapper());
    }

    public List<OrganizationService> getAllOrganizationServices(long organizationId) {
        String sql = "select o.organization_id,  o.name as organization_name , s.name, o.org_code,o.beta,s.service_id, '' as role ," +
                "    s.display_name, os.active, os.datacenter_id, d.name AS datacenter_name " +
                " FROM organization_services os " +
                "         JOIN organizations o ON os.organization_id = o.organization_id " +
                "         JOIN services s ON os.service_id = s.service_id " +
                "         JOIN datacenter d ON os.datacenter_id = d.id " +
                "WHERE os.organization_id = :organizationId; ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("organizationId", organizationId);
        return jdbcTemplate.query(sql, params, new OrganizationServiceMapper());
    }

    public List<OrganizationUser> getOrganizationOwners(long org_id) {
        String sql = "select o.organization_id, o.user_id, o.active, trim(o.role) as role , o.firstName ,o.lastName ,c.email_address , o.changegear_id " +
                ", c.profile_image " +
                " from organization_users o " +
                " left join cust_prtl_user_profile c on c.id = o.user_id " +
                " where organization_id=:orgId and role = 'owner' and o.active = true ;";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ORG_ID, org_id);
        return jdbcTemplate.query(sql, params, new OrganizationUserMapper());
    }

    public List<OrganizationUser> getOrganizationUsers(long orgId, boolean activeOnly) {
        String sql = "select ou.organization_id, ou.user_id, ou.active, ou.role ,ou.lastName ,ou.firstName ,c.email_address ,c.profile_image ,ou.changegear_id , c.shared_user " +
                " from organization_users ou" +
                " left join cust_prtl_user_profile c on id = ou.user_id" +
                " where ou.organization_id =  :orgId";
        if (activeOnly == true) {
            sql = sql + " and ou.active = True";
        }
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ORG_ID, orgId);
        List<OrganizationUser> result = jdbcTemplate.query(sql, params, new OrganizationUserMapper());
        return result;
    }
    public OrganizationUser getOrganizationUser(long orgId, int userId) {
        String sql = "select ou.organization_id, ou.user_id, ou.active, ou.role ,ou.lastName ,ou.firstName ,ou.changegear_id " +
                " from organization_users ou" +
                " where ou.organization_id =  :orgId AND ou.user_id = :userId";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ORG_ID, orgId);
        params.addValue(USER_ID, userId);
        List<OrganizationUser> result = jdbcTemplate.query(sql, params, new OrganizationUserMapper());

        if(result.size() == 0){
            OrganizationUser orgUser = null;
            return  orgUser;
        }else{
            return result.get(0);
        }
    }

    public OrganizationUser getOrganizationUserById(int userId) {
        String sql = "select ou.organization_id, ou.user_id, ou.active, ou.role ,ou.lastName ,ou.firstName ,ou.changegear_id " +
                " from organization_users ou" +
                " where ou.user_id = :userId";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(USER_ID, userId);
        List<OrganizationUser> result = jdbcTemplate.query(sql, params, new OrganizationUserMapper());

        if(result.size() == 0){
            OrganizationUser orgUser = null;
            return  orgUser;
        }else{
            return result.get(0);
        }
    }

    public String getUsersLastLogin(String usersEmail) {
        String sql = "select s.date_time" +
                " from cust_prtl_user_session s " +
                " where s.user_id=:usersEmail " +
                " order by s.date_time desc " +
                " limit 1;";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("usersEmail", usersEmail);
        return jdbcTemplate.queryForObject(sql, params, String.class);
    }

    public List<OrganizationDetails> getAllOrganizations() {
        String sql = "select organization_id , name , status " +
                " from organizations " +
                " order by name ;";

        MapSqlParameterSource params = new MapSqlParameterSource();
        return jdbcTemplate.query(sql, params, new OrganizationDetailsMapper());
    }
    public List<OrganizationStorageDetails> getStorageOrganizations() {
        String sql = "select o.organization_id , o.name, o.org_code, o.status , o.parent_id, o.icon, os.atl ,os.bhm , os.cha, os.hsv, os.gsp , os.myr , sp.commitment_amount, sp.package_type, sp.storage_type  " +
                " from organizations o" +
                " left join storage_package sp on o.organization_id = sp.organization_id " +
                " left join organization_services os on o.organization_id = os.organization_id " +
                " join services s on os.service_id = s.service_id " +
                " where lower(s.name) = 'storage' and os.active = true ;";

        MapSqlParameterSource params = new MapSqlParameterSource();
        return jdbcTemplate.query(sql, params, new OrganizationStorageDetailsMapper());
    }
    public List<OrganizationDetails> getAllNonStorageOrganizations() {
        String sql = "select o.organization_id , o.name " +
                " from organizations o " +
                " left join organization_services os on o.organization_id = os.organization_id " +
                " join services s on os.service_id = s.service_id " +
                " where lower(s.name) = 'support' or lower(s.name) = 'account' or lower(s.name) = 'billing'" +
                " GROUP BY o.organization_id, o.name;";

        MapSqlParameterSource params = new MapSqlParameterSource();
        return jdbcTemplate.query(sql, params, new OrganizationDetailsMapper());
    }

    public int updateOrganizationUsersStatus(boolean status ,long userId) {
        String sql = "update organization_users " +
                " set active = :status " +
                " where user_id = :userId;";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(STATUS, status);
        params.addValue(USER_ID, userId);
        return jdbcTemplate.update(sql, params);
    }

    public int updateOrganizationUsersRole(long orgId ,long userId , String role) {
        try {
            String sql = "update organization_users " +
                    " SET role = :role " +
                    " where organization_id = :orgId AND user_id = :userId;";

            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("role", role);
            params.addValue("orgId", orgId);
            params.addValue("userId", userId);
            return jdbcTemplate.update(sql, params);
        }catch (Exception e){
            log.error(e.getMessage());
            return 0;
        }
    }

    public List<RosterBadge> getBadgeDetails(int rosterUserId) {
        String sql = "select badge_id, card_number, badge_ref , active, rosteruser_id, issued_by , issued_dt, lost, system_object_id , guid, personnel_guid, datacenter" +
                " ,common_name ,expired , disabled, stolen , revoked, disabled_by_inactivity , print_date ,activation_dt,expiration_dt  " +
                " from badges b" +
                " where rosteruser_id = :rosterUserId ;";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("rosterUserId", rosterUserId);
        return jdbcTemplate.query(sql, params, new RosterBadgeMapper());

    }

    public int updateBadgeDetails(String guid, boolean disabled, String datacenter) {
        boolean active = false;
        if (!disabled) {
            active = true;
        }
        String sql = "update badges " +
                " set disabled = :disabled, active = :active" +
                " where guid = :guid AND datacenter =:datacenter;";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("disabled", disabled);
        params.addValue("active", active);
        params.addValue("datacenter", datacenter);
        params.addValue("guid", guid);
        return jdbcTemplate.update(sql, params);
    }

    public String getLocationTagByFacilityCode(int facilityCode) {

        String sql = " select name" +
                "        from  datacenter d" +
                "        left join datacenter_facility_codes f on d.id=f.datacenter_id" +
                "        where f.facility_code = :facilityCode;";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("facilityCode", facilityCode);

        return jdbcTemplate.queryForObject(sql, params, String.class);
    }

    public List<PortalUserSession> getOrgUsersSessions(OrgUserSessionsRequest orgUserSessionRequest) {

            String sql = "select 'Login' as description, '' as city, ou.user_id, ou.firstname, ou.lastname, cpus.user_id as email_address," +
                    " cpus.date_time at time zone :timezone  as event_date," +
                    " cpus.date_time as event_time " +
                    " from cust_prtl_user_session cpus" +
                    " join cust_prtl_user_profile cpup on cpus.user_id=cpup.email_address" +
                    " join organization_users ou on cpup.id = ou.user_id" +
                    " where ou.organization_id = :orgId" +
                    " order by cpus.date_time DESC " +
                    " limit 20;";


            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue(ORG_ID, orgUserSessionRequest.getOrganizationId());
            params.addValue("timezone", orgUserSessionRequest.getTimezone());
            return jdbcTemplate.query(sql, params, new OrgUserSessionMapper());
    }

    public List<BadgeAccessPoint> getOrgBadgeAccessPoints(BadgeAccessPointsRequest badgeAccessPointsRequest) {
        String sql = "select r.id , a.message_type , to_char(a.message_dt at time zone :timezone,'mm-dd-YY HH12:MI AM') as message_dt " +
                " ,b.common_name ,b.badge_id, a.access_point_guid, b.datacenter ,bap.name" +
                " from rosteruser_access a" +
                " left join badges b on b.card_number = a.card_number" +
                " left join rosteruser_datacenter rd on rd.personnel_id = b.personnel_id" +
                " left join rosteruser r on r.id = rd.rosteruser_id" +
                " left join badge_access_points bap on   bap.guid = a.access_point_guid" +
                " where r.organization_id = :orgId" +
                " order by a.message_dt DESC " +
                " limit 20;";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ORG_ID, badgeAccessPointsRequest.getOrganizationId());
        params.addValue("timezone", badgeAccessPointsRequest.getTimezone());
        return jdbcTemplate.query(sql, params, new BadgeAccessPointMapper());

    }

    public boolean checkOrgServiceExists(Long orgId, long serviceId) {
        String sql = "select exists( select os.organization_id, os.service_id, os.active, lower(s.name) as name, os.atl ,os.bhm , os.cha,os.hsv,os.gsp, os.myr, " +
                "'' as icon, '' as role, s.display_name " +
                "from organization_services os " +
                "join services s on os.service_id = s.service_id " +
                "where organization_id=:organizationId and os.service_id = :serviceId ) ";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(SERVICE_ID, serviceId);
        params.addValue("organizationId", orgId);
        boolean exists = jdbcTemplate.queryForObject(sql, params, Boolean.class);
        return exists;
    }

    public boolean updateOrgService(OrganizationService organizationService) {
        String sql = "update organization_services " +
                " set active = :status , datacenter_id = :datacenterId  " +
                " where organization_id = :orgId and service_id = :serviceId;";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(STATUS, organizationService.isActive());
        params.addValue(ORG_ID,organizationService.getOrganizationId());
        params.addValue(SERVICE_ID,organizationService.getServiceId());
        params.addValue("datacenterId", organizationService.getDatacenterId());

        int success = jdbcTemplate.update(sql, params);
        if(success == 1){
            return true;
        }else{
            return false;
        }
    }

    public boolean addOrgService(OrganizationService organizationService) {
        String sql = "INSERT INTO organization_services (organization_id, service_id, active, datacenter_id " +
                "VALUES (:orgId, :serviceId, :status, :datacenterId)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ORG_ID, organizationService.getOrganizationId());
        params.addValue(SERVICE_ID,organizationService.getServiceId());
        params.addValue(STATUS, organizationService.isActive());
        params.addValue("datacenterId", organizationService.getDatacenterId());

       int success = jdbcTemplate.update(sql, params);
        if(success == 1){
            return true;
        }else{
            return false;
        }
    }

    public boolean deleteOrgService(OrganizationService organizationService) {
        String sql = "DELETE from organization_services " +
                " where organization_id = :orgId and service_id = :serviceId;";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(ORG_ID, organizationService.getOrganizationId());
        params.addValue(SERVICE_ID,organizationService.getServiceId());

        int success = jdbcTemplate.update(sql, params);
        if(success == 1){
            return true;
        }else{
            return false;
        }
    }

    public int updateRosterUser(int rosterUserId, String email) {
        String sql = "UPDATE rosteruser set user_id = (select id from cust_prtl_user_profile where email_address = :email) " +
                " where id = :rosterUserId;";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("rosterUserId" , rosterUserId);
        params.addValue("email" , email);

        int success = jdbcTemplate.update(sql, params);
        return success;
    }

    public int updateRosterUser(RosterUser rosterUser) {
        String sql = "UPDATE rosteruser set user_id =:userId , email =:email , emailaddress =:email " +
                " where id = :rosterUserId;";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("rosterUserId" , rosterUser.getId());
        params.addValue("email" , rosterUser.getEmailAddress());
        params.addValue("userId" , rosterUser.getUserId());

        int success = jdbcTemplate.update(sql, params);
        return success;
    }

    public boolean getOrganizationStatus(int id) {
        String sql = "select status " +
                " from organizations  " +
                " where organization_id=:organizationId ";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("organizationId", id);
        Integer status = jdbcTemplate.queryForObject(sql, params, Integer.class);

        if(status.equals(1)){
            return true;
        }else {
            return false;
        }

    }
    public boolean updateOrgStatus(long orgId, int status) {
        String sql = "update organizations " +
                " set status = :status  " +
                " where organization_id = :orgId ;";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(STATUS, status);
        params.addValue(ORG_ID,orgId);

        int success = jdbcTemplate.update(sql, params);
        if(success == 1){
            return true;
        }else{
            return false;
        }
    }

    public int addService(OrganizationService service) {
        String sql = "INSERT INTO organization_services (organization_id, service_id , datacenter_id, active  )" +
                "VALUES (:organizationId, :serviceId , :datacenterId , :active);";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("organizationId", service.getOrganizationId());
        params.addValue("serviceId", service.getServiceId());
        params.addValue("datacenterId", service.getDatacenterId());
        params.addValue("active", service.isActive());

        return jdbcTemplate.update(sql, params);

    }

    public int removeService(OrganizationService service) {
        String sql = "DELETE FROM organization_services " +
                "WHERE organization_id = :organizationId AND service_id = :serviceId AND datacenter_id = :datacenterId;";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("organizationId", service.getOrganizationId());
        params.addValue("serviceId", service.getServiceId());
        params.addValue("datacenterId", service.getDatacenterId());

        return jdbcTemplate.update(sql, params);

    }

    public boolean saveAllEmergencyContacts(List<OrganizationContact> contacts) {

        String sql = "INSERT INTO organization_contacts " +
                "    (organization_id, organization_temp_name, first_name, last_name, email, phone, created_at, updated_at) " +
                " VALUES (:organizationId, :organizationTempName, :firstName, :lastName, :email, :phone, NOW(), NOW()) ;";


        MapSqlParameterSource[] batchParams = contacts.stream().map(contact -> {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("organizationId", contact.getOrganizationId());
            params.addValue("organizationTempName", contact.getOrganizationTempName());
            params.addValue("firstName", contact.getFirstName());
            params.addValue("lastName", contact.getLastName());
            params.addValue("email", contact.getEmail());
            params.addValue("phone", contact.getPhone());
            return params;
        }).toArray(MapSqlParameterSource[]::new);

        int[] insertCounts = jdbcTemplate.batchUpdate(sql, batchParams);

        return insertCounts.length > 0 && java.util.Arrays.stream(insertCounts).sum() > 0;

    }

    public List<OrganizationContact> findEmergencyContacts(String organizationId) {
            String sql = "SELECT id, organization_id, organization_temp_name, first_name, last_name, email, phone, created_at, updated_at " +
                    "FROM organization_contacts " +
                    (organizationId != null && !organizationId.isEmpty() ? "WHERE organization_id = :orgId " : "") +
                    "ORDER BY created_at DESC; ";

            MapSqlParameterSource params = new MapSqlParameterSource();
            if (organizationId != null && !organizationId.isEmpty()) {
                params.addValue("orgId", Long.parseLong(organizationId));
            }

            return jdbcTemplate.query(sql, params, new OrganizationContactMapper());
    }

    public boolean updateOrganizationContact(OrganizationContact contact) {
        String sql = "UPDATE organization_contacts " +
                "SET first_name = :firstName, last_name = :lastName, email = :email, " +
                "phone = :phone, updated_at = NOW() , organization_id =:orgId " +
                "WHERE id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", contact.getId())
                .addValue("firstName", contact.getFirstName())
                .addValue("lastName", contact.getLastName())
                .addValue("email", contact.getEmail())
                .addValue("phone", contact.getPhone())
                .addValue("orgId", contact.getOrganizationId());
        return jdbcTemplate.update(sql, params) > 0;
    }

    public boolean deleteOrganizationContact(Long id) {
        String sql = "DELETE FROM organization_contacts WHERE id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);

        return jdbcTemplate.update(sql, params) > 0;
    }

    public boolean addOrganizationContact(OrganizationContact contact) {
        String sql = "INSERT INTO organization_contacts " +
                "(organization_id, organization_temp_name, first_name, last_name, email, phone, created_at, updated_at) " +
                "VALUES (:organizationId, :organizationTempName, :firstName, :lastName, :email, :phone, NOW(), NOW())";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("organizationId", contact.getOrganizationId())
                .addValue("organizationTempName", contact.getOrganizationTempName())
                .addValue("firstName", contact.getFirstName())
                .addValue("lastName", contact.getLastName())
                .addValue("email", contact.getEmail())
                .addValue("phone", contact.getPhone());

        return jdbcTemplate.update(sql, params) > 0;
    }
}
