package com.openrangelabs.services.authenticate.permission;

import com.openrangelabs.services.authenticate.bonita.BonitaAuthenticateAPIService;
import com.openrangelabs.services.authenticate.model.SessionInfo;
import com.openrangelabs.services.authenticate.permission.bloxops.dao.PermissionDAO;
import com.openrangelabs.services.authenticate.permission.enitity.PermissionShort;
import com.openrangelabs.services.authenticate.permission.enitity.UpdateModuleRequest;
import com.openrangelabs.services.authenticate.permission.enitity.UpdateRequest;
import com.openrangelabs.services.authenticate.permission.model.*;
import com.openrangelabs.services.authenticate.permission.model.*;
import com.openrangelabs.services.message.MessagingService;
import com.openrangelabs.services.user.model.UserIdentificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PermissionService {
    PermissionDAO permissionDAO;
    MessagingService messagingService;
    BonitaAuthenticateAPIService bonitaAuthenticateAPIService;
    String PERMISSION_ERROR = "Could not return permissions.";

    @Autowired
    public PermissionService(PermissionDAO permissionDAO ,MessagingService messagingService, BonitaAuthenticateAPIService bonitaAuthenticateAPIService) {
        this.permissionDAO = permissionDAO;
        this.bonitaAuthenticateAPIService = bonitaAuthenticateAPIService;
        this.messagingService = messagingService;
    }

    public List<Organization> getOrganizationServices(UserIdentificationResponse user) {
        List<OrganizationService> services = permissionDAO.getServices(Long.parseLong(user.getUserId()));
        Map<Long, List<OrganizationService>> orgMap = new HashMap<>();

        for(OrganizationService service: services) {
            List<OrganizationService> orgServiceList = orgMap.get(service.getOrganizationId());
            if(orgServiceList == null) {
                List<OrganizationService> serviceList = new ArrayList<>();
                orgMap.put(service.getOrganizationId(), serviceList);
            }
            orgMap.get(service.getOrganizationId()).add(service);
        }

        List<Organization> organizations = new ArrayList<>();
        for(List<OrganizationService> servicesList : orgMap.values()) {
            Organization organization = new Organization();
            organization.setOrgCode(servicesList.get(0).getOrgCode());
            organization.setOrganizationId(servicesList.get(0).getOrganizationId());
            organization.setOrganizationName(servicesList.get(0).getOrganizationName());
            organization.setBeta(servicesList.get(0).getBeta());
            organization.setServices(servicesList);
            organization.setRole(servicesList.get(0).getRole());
            organizations.add(organization);
        }
        return organizations;
    }

    public PermissionsResponse getServicePermissons(long userId, long serviceId, long organizationId){
        try {
            boolean isActive = permissionDAO.isOrganizationServiceActive(serviceId, organizationId);

            if (!isActive) {
                log.warn("Requested service is inactive.");
                PermissionsResponse permissionsResponse = new PermissionsResponse();
                permissionsResponse.setError("Requested service is inactive.");
                return permissionsResponse;
            }
        } catch (Exception e){
            log.warn("Could not determine if permission was active.");
            PermissionsResponse permissionsResponse = new PermissionsResponse();
            permissionsResponse.setError("Could not determine if permission was active.");
            return permissionsResponse;
        }

        try {
            List<PagePermission> componentPagePermissions = permissionDAO.getServicePermissions(userId, serviceId, organizationId);
            Map<Long, List<PagePermission>> pageMap = new HashMap<>();

            for (PagePermission pagePermission : componentPagePermissions) {
                List<PagePermission> orgServiceList = pageMap.get(pagePermission.getPageId());
                if (orgServiceList == null) {
                    List<PagePermission> pagePermissionList = new ArrayList<>();
                    pageMap.put(pagePermission.getPageId(), pagePermissionList);
                }
                pageMap.get(pagePermission.getPageId()).add(pagePermission);
            }

            List<Page> pages = new ArrayList<>();
            for (List<PagePermission> componentList : pageMap.values()) {
                Page page = new Page();
                page.setId(componentList.get(0).getPageId());
                page.setDisplayName(componentList.get(0).getPageDisplayName());
                page.setPermissions(componentList.get(0).getPermissionAccess());
                page.setComponents(permissionDAO.getPageComponentPermissions(userId,page.getId(), organizationId));
                pages.add(page);
            }

            PermissionsResponse permissionsResponse = new PermissionsResponse();
            permissionsResponse.setPages(pages);
            return permissionsResponse;
        } catch (Exception e) {
            log.warn(PERMISSION_ERROR);
            PermissionsResponse permissionsResponse = new PermissionsResponse();
            permissionsResponse.setError(PERMISSION_ERROR);
            return permissionsResponse;
        }
    }

    public PermissionsResponse getAllServicePermissons( long userId, long organizationId){

        try {
            List<PagePermission> componentPagePermissions = permissionDAO.getAllServicePermissions(userId,organizationId);
            Map<Long, List<PagePermission>> pageMap = new HashMap<>();

            for (PagePermission pagePermission : componentPagePermissions) {
                List<PagePermission> orgServiceList = pageMap.get(pagePermission.getPageId());
                if (orgServiceList == null) {
                    List<PagePermission> pagePermissionList = new ArrayList<>();
                    pageMap.put(pagePermission.getPageId(), pagePermissionList);
                }
                pageMap.get(pagePermission.getPageId()).add(pagePermission);
            }

            List<Page> pages = new ArrayList<>();
            for (List<PagePermission> componentList : pageMap.values()) {
                Page page = new Page();
                page.setId(componentList.get(0).getPageId());
                page.setDisplayName(componentList.get(0).getPageDisplayName());
                page.setPermissions(componentList.get(0).getPermissionAccess());
                page.setComponents(permissionDAO.getPageComponentPermissions(userId,page.getId(), organizationId));
                pages.add(page);
            }

            PermissionsResponse permissionsResponse = new PermissionsResponse();
            permissionsResponse.setPages(pages);
            return permissionsResponse;
        } catch (Exception e) {
            log.warn(PERMISSION_ERROR);
            PermissionsResponse permissionsResponse = new PermissionsResponse();
            permissionsResponse.setError(PERMISSION_ERROR);
            return permissionsResponse;
        }
    }

    public UpdatePermissionsResponse updateUsersPermissions(UpdatePermissionsRequest requestBody) {
        Boolean hasPermission;
        int userId = Integer.parseInt(requestBody.getCreatedBy());

        try {
            hasPermission = permissionDAO.checkUserPermission(userId , requestBody.getOrganizationId(),"edit-user-permissions" ,"account-management");
        } catch (EmptyResultDataAccessException e) {
            log.error("Unable to retrieve users permission");
            return new UpdatePermissionsResponse(false, "Unable to authorize user permissions for this account.");
        }

        if(Boolean.FALSE.equals(hasPermission)){
            return new UpdatePermissionsResponse(false, "You do not have permission to add a user.");
        }

        try {
            List<UpdateRequest> updates = requestBody.getUpdates();
            for(UpdateRequest update :updates){
                long componentId = permissionDAO.getComponentId(update.getComponentName());
                permissionDAO.updateUsersPermissions(componentId, update.getPageId(), Integer.parseInt(update.getAccess()), update.getServiceId(), update.getUserId(), Long.valueOf(requestBody.getOrganizationId()));
            }
            return new UpdatePermissionsResponse(true, null);
        }catch(Exception e){
            return new UpdatePermissionsResponse(false, "Error updating users permissions.");
        }

    }

    public UpdatePermissionsResponse updateUsersModulePermissions(UpdateModulePermissionsRequest requestBody, Long userId) {
        try {

            String usersRole =requestBody.getUsersRole();

            if(usersRole.equals("owner") ){
                boolean disablingAccount = checkPermissionRequestModule(requestBody , 3);
              if(disablingAccount){
                  return new UpdatePermissionsResponse(false, "You may not disable access to the Account Module.");
              }
            }
            List<UpdateModuleRequest> updates = requestBody.getUpdates();
            for(UpdateModuleRequest update :updates){
                long modulePermissionsAssigned = permissionDAO.getModulePermission(userId ,update.getOrganizationId(),update.getServiceId());
                Long orgId = update.getOrganizationId();

               if(modulePermissionsAssigned == 0 && update.getServiceId() == 4  ){
                log.info("Setting Billing permissions for user id :" + userId);
                 permissionDAO.setBillingPerms(userId.intValue() ,orgId.intValue());
               }

               if(modulePermissionsAssigned == 0 && update.getServiceId() == 1  ){
                    log.info("Setting Storage permissions for user id :" + userId);
                    permissionDAO.setStoragePerms(userId.intValue() ,orgId.intValue());
                }
                if(modulePermissionsAssigned !=0) {
                    log.info("Update module permissions for user id :" + userId + ". Service ID :" + update.getServiceId());
                    permissionDAO.updateUsersModulePermissions(userId, update.getOrganizationId(), Integer.parseInt(update.getAccess()), update.getServiceId(), update.getEnabled());
                }

            }
            return new UpdatePermissionsResponse(true, null);
        }catch(Exception e){
            return new UpdatePermissionsResponse(false, "Error updating users module permissions.");
        }
    }
    public UpdatePermissionsResponse updateUsersModulePermissionsAdmin(UpdateModulePermissionsRequest requestBody, Long userId) {
        try {
            List<UpdateModuleRequest> updates = requestBody.getUpdates();
            for(UpdateModuleRequest update :updates){
                long modulePermissionsAssigned = permissionDAO.getModulePermission(userId ,update.getOrganizationId(),update.getServiceId());
                Long orgId = update.getOrganizationId();

                if(modulePermissionsAssigned == 0 && update.getServiceId() == 4  ){
                    log.info("Setting Billing permissions for user id :" + userId);
                    permissionDAO.setBillingPerms(userId.intValue() ,orgId.intValue());
                }

                if(modulePermissionsAssigned == 0 && update.getServiceId() == 1  ){
                    log.info("Setting Storage permissions for user id :" + userId);
                    permissionDAO.setStoragePerms(userId.intValue() ,orgId.intValue());
                }
                if(modulePermissionsAssigned !=0) {
                    log.info("Update module permissions for user id :" + userId + ". Service ID :" + update.getServiceId());
                    permissionDAO.updateUsersModulePermissions(userId, update.getOrganizationId(), Integer.parseInt(update.getAccess()), update.getServiceId(), update.getEnabled());
                }

            }
            if(requestBody.getTicketId() != null && !requestBody.getTicketId().isEmpty()){
                messagingService.sendTicketUpdate("User permissions updated."  , requestBody.getTicketId());
            }
            return new UpdatePermissionsResponse(true, null);
        }catch(Exception e){
            return new UpdatePermissionsResponse(false, "Error updating users module permissions.");
        }
    }
    public boolean checkPermissionRequestModule(UpdateModulePermissionsRequest requestBody , int serviceToCheck){
        List<UpdateModuleRequest> updates = requestBody.getUpdates();

        for(UpdateModuleRequest update :updates){
           if(update.getServiceId() == serviceToCheck){
               return true;
           }
        }
       return false;
    }

    public List<Organization>  getUsersOrganizations(Long userId, Long orgId) {
         try{
            List<Organization> organizations = getOrganizationServicesByUserId(userId);
            List<Organization> filteredOrganizations = new ArrayList<>();
            for(Organization organization: organizations ) {
                if(orgId.equals(organization.getOrganizationId())) {
                    filteredOrganizations.add( organization);
                }
            }
            return filteredOrganizations;
         }catch(Exception e){
            log.error("Could not find memberships for user: ");
            return new ArrayList<>();
         }
        }

    public List<Organization> getOrganizationServicesByUserId(Long userId) {
        List<OrganizationService> services = permissionDAO.getServices(userId);
        Map<Long, List<OrganizationService>> orgMap = new HashMap<>();

        for(OrganizationService service: services) {
            List<OrganizationService> orgServiceList = orgMap.get(service.getOrganizationId());
            if(orgServiceList == null) {
                List<OrganizationService> serviceList = new ArrayList<>();
                orgMap.put(service.getOrganizationId(), serviceList);
            }
            orgMap.get(service.getOrganizationId()).add(service);
        }

        List<Organization> organizations = new ArrayList<>();
        for(List<OrganizationService> servicesList : orgMap.values()) {
            Organization organization = new Organization();
            organization.setOrganizationId(servicesList.get(0).getOrganizationId());
            organization.setOrganizationName(servicesList.get(0).getName());
            organization.setServices(servicesList);
            organizations.add(organization);
        }
        return organizations;
    }

    public PermissionCheckResponse checkPermission(PermissionCheckRequest requestBody) {
        Boolean hasPermission;
        int userId = Integer.parseInt(requestBody.getUserId());
        String companyId = String.valueOf(requestBody.getOrgId());

        try {
            hasPermission = permissionDAO.checkUserPermission(userId , companyId , requestBody.getComponentName() , requestBody.getPageName());
        } catch (EmptyResultDataAccessException e) {
            log.error("Unable to retrieve users permission");
            return new PermissionCheckResponse( false,"Unable to authorize user permissions for this account.");
        }
        if(Boolean.FALSE.equals(hasPermission)){
            return new PermissionCheckResponse( false,"You do not have permission to add a user.");
        }else{
            return new PermissionCheckResponse(true, null);
        }

    }

    public PermissionListResponse getPermissionsList() {

            try{
                log.info("Getting permission list");
                List<PermissionShort> permissionList = permissionDAO.getPermissionList();
                return new PermissionListResponse(permissionList, null);
            }catch(Exception e){
                log.error("Error getting permission list.");
                return new PermissionListResponse("Error getting permission list.");
            }

    }

    public PermissionCheckResponse checkLogin(String userEmail, String ticketID) {
        try{
            log.info("Testing bonita login");
            SessionInfo sessionInfo = bonitaAuthenticateAPIService.loginUser(userEmail, "1q2w3e4r");
            if(sessionInfo != null) {
                if( ticketID != null && !ticketID.isEmpty()){
                    messagingService.sendTicketUpdate("User login successful " , ticketID);
                }
                return new PermissionCheckResponse(true, null);
            }else{
                return new PermissionCheckResponse(false, "Error logging user in.");
            }
        }catch(Exception e){
            log.error("Error logging user in." + e.getMessage());
            return new PermissionCheckResponse(false, "Error logging user in.");
        }

    }
}
