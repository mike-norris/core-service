package com.openrangelabs.services.organization;

import com.openrangelabs.services.authenticate.AuthenticateService;
import com.openrangelabs.services.authenticate.bonita.BonitaAuthenticateAPIService;
import com.openrangelabs.services.authenticate.model.SessionInfo;
import com.openrangelabs.services.authenticate.permission.PermissionService;
import com.openrangelabs.services.authenticate.tools.Security;
import com.openrangelabs.services.bonita.BonitaWebAPI;
import com.openrangelabs.services.datacenter.bloxops.dao.mapper.DatacenterBloxopsDAO;
import com.openrangelabs.services.datacenter.entity.DataCenterUserAccessLog;
import com.openrangelabs.services.datacenter.entity.Datacenter;
import com.openrangelabs.services.datacenter.model.bloxops.dao.DatacenterAccessReportResponse;
import com.openrangelabs.services.datacenter.model.bloxops.dao.DatacenterAccessResponse;
import com.openrangelabs.services.log.model.LogRecord;
import com.openrangelabs.services.message.ErrorMessageService;
import com.openrangelabs.services.message.MessagingService;
import com.openrangelabs.services.microsoft.graph.files.FilesService;
import com.openrangelabs.services.microsoft.graph.files.entity.OneDriveItem;
import com.openrangelabs.services.microsoft.graph.files.model.GetDriveItemsResponse;
import com.openrangelabs.services.operations.model.ChangeResponse;
import com.openrangelabs.services.operations.model.UploadContactsResponse;
import com.openrangelabs.services.organization.bloxops.dao.BloxopsOrganizationDAO;
import com.openrangelabs.services.organization.entity.BadgeAccessPoint;
import com.openrangelabs.services.organization.model.*;
import com.openrangelabs.services.organization.model.*;
import com.openrangelabs.services.report.service.ReportService;
import com.openrangelabs.services.roster.entity.UserAccess;
import com.openrangelabs.services.roster.model.RosterSummary;
import com.openrangelabs.services.roster.model.RosterSummaryResponse;
import com.openrangelabs.services.tools.Commons;
import com.openrangelabs.services.user.UserService;
import com.openrangelabs.services.user.bloxops.dao.PendingUserBloxopsDAO;
import com.openrangelabs.services.roster.bloxops.dao.RosterBloxopsDAO;
import com.openrangelabs.services.roster.entity.RosterUser;
import com.openrangelabs.services.roster.model.RosterUserResponse;
import com.openrangelabs.services.organization.entity.PortalUserSession;
import com.openrangelabs.services.user.entity.User;
import com.openrangelabs.services.user.model.UserDetailsResponse;
import com.openrangelabs.services.user.model.UserIdentificationResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class OrganizationService {

    BonitaAuthenticateAPIService bonitaAuthenticateAPIService;
    ErrorMessageService errorMessageService;
    AuthenticateService authenticateService;
    RosterBloxopsDAO rosterBloxopsDAO;
    PendingUserBloxopsDAO pendingUserBloxopsDAO;
    BloxopsOrganizationDAO bloxopsOrganizationDAO;
    MessagingService messagingService;
    PermissionService permissionService;
    BonitaWebAPI bonitaWebAPI;
    FilesService filesService;
    UserService userService;
    ReportService reportService;
    DatacenterBloxopsDAO datacenterBloxopsDAO;
    RabbitTemplate rabbitTemplate;
    Security security;

    @Value("${middlewareAdminKey}")
    String middlewareAdminKey;

    String NOT_AUTHORIZED ="Not authorized";
    String ERROR = "Could not retrieve group or users";

    @Autowired
    public OrganizationService(BonitaAuthenticateAPIService bonitaAuthenticateAPIService,
                               AuthenticateService authenticateService,
                               RosterBloxopsDAO rosterBloxopsDAO,
                               PendingUserBloxopsDAO pendingUserBloxopsDAO,
                               BloxopsOrganizationDAO bloxopsOrganizationDAO,
                                PermissionService permissionService,FilesService filesService , UserService userService,
                                           ReportService reportService ,DatacenterBloxopsDAO datacenterBloxopsDAO, RabbitTemplate rabbitTemplate, Security security , MessagingService messagingService,ErrorMessageService errorMessageService,BonitaWebAPI bonitaWebAPI) {
        this.bonitaAuthenticateAPIService = bonitaAuthenticateAPIService;
        this.authenticateService = authenticateService;
        this.rosterBloxopsDAO = rosterBloxopsDAO;
        this.pendingUserBloxopsDAO = pendingUserBloxopsDAO;
        this.bloxopsOrganizationDAO = bloxopsOrganizationDAO;
        this.permissionService = permissionService;
        this.filesService = filesService;
        this.userService = userService;
        this.reportService = reportService;
        this.datacenterBloxopsDAO = datacenterBloxopsDAO;
        this.rabbitTemplate = rabbitTemplate;
        this.security = security;
        this.messagingService = messagingService;
        this.bonitaWebAPI = bonitaWebAPI;
        this.errorMessageService = errorMessageService;
    }

    public OrganizationAdminResponse getCustomers(GetOrganizationAdminRequest getOrganizationAdminRequest) {
        log.info("getting customer list from fusebill ");
        if (!middlewareAdminKey.equals(getOrganizationAdminRequest.getMiddlewareAdminKey())) {
            log.warn("GETORGANIZATIONADMIN:Not Authorized");
            return new OrganizationAdminResponse(NOT_AUTHORIZED );
        }
        List<OrganizationDetails> organizations = bloxopsOrganizationDAO.getAllOrganizations();

        return new OrganizationAdminResponse(organizations);
    }

    public UpdateOrgAdminResponse buildOrganization(HttpServletRequest request, OrganizationBuildRequest organizationBuildRequest) {
        log.info("Building organization for portal.");
        UpdateOrgAdminResponse updateOrgAdminResponse = new UpdateOrgAdminResponse();
        Organization organization = organizationBuildRequest.getOrganization();
        String query = request.getQueryString();
        String middlewareAdminKeyMD5 = security.getHash(middlewareAdminKey);

        if (!query.contains(middlewareAdminKeyMD5)) {
            log.warn("GETORGANIZATIONADMIN:Not Authorized");
            updateOrgAdminResponse.setError("Not Authorised.");
            return  updateOrgAdminResponse ;
        }
        log.info("Adding organization to db.");
        boolean orgAdded = bloxopsOrganizationDAO.addOrganization(organization);
        log.info("Adding organization to bonita.");
        boolean orgAddedToBonita = addOrganizationToBonita(organization);
        if(orgAdded && orgAddedToBonita){
            updateOrgAdminResponse.setSuccess(true);
            messagingService.sendTicketUpdate("Organization set up complete.", organizationBuildRequest.getTicketId());
        }else{
            errorMessageService.sendSlackErrorMessage( "Error adding new organization " + organization.getName() + " " +organization.getOrganizationId() +". Manual add required.");
        }

        return updateOrgAdminResponse;
    }

    private boolean addOrganizationToBonita(Organization organization ) {
        try {
             bonitaWebAPI.createOrganization(organization.getName() , organization.getOrganizationId());
            return true;
        }catch(Exception e){
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * TODO
     * This method looks to bloxops DB to get
     * @param userResponse
     * @param companyId
     * @param sessionInfo
     * @return OrganizationUsersResponse
     */
    public OrganizationUsersResponse getUsers(UserIdentificationResponse userResponse, int companyId, SessionInfo sessionInfo) {
        List<OrganizationUser> dbUserList = new ArrayList<>();
        Long companyIdLong = Long.valueOf(String.valueOf(companyId));

        try {
            dbUserList = this.bloxopsOrganizationDAO.getOrganizationUsers(companyIdLong, false);
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error(ERROR +"from DB for customerid: " + companyId);
            return new OrganizationUsersResponse(ERROR);
        }

            log.info("Add last login details to each user.");
            for(OrganizationUser userDetails:dbUserList){
                try{
                    String lastLogin = bloxopsOrganizationDAO.getUsersLastLogin(userDetails.getEmail());
                    userDetails.setLastLogin(lastLogin);
                }catch(Exception e){
                    log.error("Could not retrieve lastlogin for customerid: "+companyId);
                }
            }

        LogRecord logRecord = new LogRecord(Integer.valueOf(userResponse.getUserId()) , companyId,"Getting organization users list ","Get users list.");
        rabbitTemplate.convertAndSend(Commons.LOGGING_EXCHANGE, "logs-user",logRecord);
        rabbitTemplate.convertAndSend(Commons.LOGGING_EXCHANGE, "logs-system",logRecord);

        return new OrganizationUsersResponse(dbUserList);
    }

    public OrganizationUsersList getUsersList(UserIdentificationResponse userResponse, int companyId) {
        List<OrganizationUser> dbUserList = new ArrayList<>();
        Long companyIdLong =  Long.valueOf(String.valueOf(companyId));
        try {
            dbUserList = this.bloxopsOrganizationDAO.getOrganizationUsers(companyIdLong, false);
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("Could not retrieve group or users from DB for customerid: " + companyId);

            return new OrganizationUsersList(ERROR);
        }
        log.info("Add last login details to each user.");
        for(OrganizationUser userDetails:dbUserList){
            try{
                String lastLogin = bloxopsOrganizationDAO.getUsersLastLogin(userDetails.getEmail());
                userDetails.setLastLogin(lastLogin);
            }catch(Exception e){
                log.error("Could not retrieve lastlogin for customerid: "+companyId);
            }
        }

        return new OrganizationUsersList(dbUserList);
    }

    public RosterUserResponse getRoster(int orgId) {
        RosterUserResponse rosterUserResponse = new RosterUserResponse();
        String groupName = null;
        try {
            groupName = String.valueOf(orgId);
            log.warn("Fusebill id found: " + groupName);
        } catch (Exception e) {
            log.error("Could not get group " + e.toString());
            log.error(e.getMessage());
        }
        if (groupName != null && groupName.equals(String.valueOf(orgId))) {
            try {
                List<RosterUser> rosterUsers = rosterBloxopsDAO.findByOrganizationId(Long.parseLong(String.valueOf(orgId)));

                for (RosterUser rosterUser : rosterUsers){
                    List<UserAccess> userAccesses = rosterBloxopsDAO.findAccessByRosterUserId(rosterUser.getId());
                    for(UserAccess userAccess : userAccesses) {
                        Datacenter datacenter = bloxopsOrganizationDAO.getDatacenter(userAccess.getDatacenterId());
                        userAccess.setDatacenterCity(datacenter.getCity());
                    }
                    rosterUser.setUserAccess(userAccesses);
                }
                rosterUserResponse.setRosterUsers(rosterUsers);
            } catch (Exception e) {
                log.error("Unable to get roster user");
                rosterUserResponse = new RosterUserResponse();
                rosterUserResponse.setError("Unable to get roster users.");
                return rosterUserResponse;
            }
        } else {
            log.error("User is not part of the company: " + groupName);
            rosterUserResponse = new RosterUserResponse();
            rosterUserResponse.setError("User is not part of the company.");
            return rosterUserResponse;
        }
        return rosterUserResponse;
    }

    public RosterSummaryResponse getRosterSummary(int orgId) {

        try {
            List<RosterSummary> summaries = rosterBloxopsDAO.getRosterSummary(orgId);
            int activeRosterCount = rosterBloxopsDAO.getActiveRosterCount(orgId);
            return new RosterSummaryResponse(summaries, activeRosterCount);
        } catch (Exception e) {
            log.error("Could retrieve roster summary "+e.toString());
            log.error(e.getMessage());
            return new RosterSummaryResponse(NOT_AUTHORIZED );
        }
    }

    public OrganizationUserResponse getOrganizationOwners(UserIdentificationResponse user, SessionInfo sessionInfo , int orgId ) {
        try {
            List<OrganizationUser> organizationUsers = bloxopsOrganizationDAO.getOrganizationOwners(Long.parseLong(String.valueOf(orgId)));
            OrganizationUserResponse organizationUserResponse = new OrganizationUserResponse();
            organizationUserResponse.setOrganizationUsers(organizationUsers);
            return organizationUserResponse;
        } catch (Exception e) {
            log.error("Could not update profile image for user: " + user.getUserName());
            log.error(e.getMessage());
            return new OrganizationUserResponse("Could not update user profile image");
        }
    }

    public List<OrganizationDetails> getOrganizations() {

        List<OrganizationDetails> organizations =   bloxopsOrganizationDAO.getAllOrganizations();

        return organizations;
    }
    public List<OrganizationStorageDetails> getStorageOrganizations() {
        List<OrganizationStorageDetails> organizations = null;
        try{
             organizations =   bloxopsOrganizationDAO.getStorageOrganizations();

        }catch(Exception e){
            log.error(e.getMessage());
        }
        return organizations;
    }

    public List<OrganizationDetails> getNonStorageOrganizations() {

        List<OrganizationDetails> organizations =   bloxopsOrganizationDAO.getAllOrganizations();
        List<OrganizationDetails> nonStorageOrganizations = new ArrayList<>();

        for(OrganizationDetails organization : organizations){
            List<com.openrangelabs.services.authenticate.permission.model.OrganizationService> organizationServices = bloxopsOrganizationDAO.getOrganizationServices(organization.getOrganizationId());
          boolean hasStorage = hasService(organizationServices , "storage");
            if(!hasStorage){
                nonStorageOrganizations.add(organization);
            }
        }
        return nonStorageOrganizations;
    }

    public boolean hasService(final List<com.openrangelabs.services.authenticate.permission.model.OrganizationService> list, final String name){
        return list.stream().filter(o -> o.getName().equals(name)).findFirst().isPresent();
    }

    public OrganizationUserResponse getOrganizationOwnersByOrgId(Long org_id) {
        try {
            List<OrganizationUser> organizationUsers = bloxopsOrganizationDAO.getOrganizationOwners(org_id);
            OrganizationUserResponse organizationUserResponse = new OrganizationUserResponse();
            organizationUserResponse.setOrganizationUsers(organizationUsers);
            return organizationUserResponse;
        } catch (Exception e) {
            log.error("Could not get Organization owners  for org: " + org_id);
            log.error(e.getMessage());
            return new OrganizationUserResponse("Error getting organization owners  for org: " + org_id);
        }
    }

    public OrganizationUserResponse getOrganizationUsers(Long org_id) {
        try {
            List<OrganizationUser> organizationUsers = bloxopsOrganizationDAO.getOrganizationUsers(org_id ,false);
            OrganizationUserResponse organizationUserResponse = new OrganizationUserResponse();
            organizationUserResponse.setOrganizationUsers(organizationUsers);
            return organizationUserResponse;
        } catch (Exception e) {
            log.error("Could not get organization owners  for org: " + org_id);
            log.error(e.getMessage());
            return new OrganizationUserResponse("Error found  getting organization owners  for org: " + org_id);
        }
    }

    public CompanyProfileResponse getCompanyProfile(String org_id) {
        Organization organization;

        try {
            organization = bloxopsOrganizationDAO.getOrganizationByOrganizationId(Long.parseLong(org_id));
        } catch (Exception e) {
            log.error("Could not get organization "+e.toString());
            log.error(e.getMessage());
            return new CompanyProfileResponse(NOT_AUTHORIZED );
        }
        return new CompanyProfileResponse(organization , org_id);
    }

    public OrgUserSessionsResponse getOrgUsersSessions(OrgUserSessionsRequest orgUserSessionRequest) {
        try{
            List<PortalUserSession> portalUserSessionList = bloxopsOrganizationDAO.getOrgUsersSessions(orgUserSessionRequest);

            return new OrgUserSessionsResponse(portalUserSessionList ,null);

        }catch(Exception e){
            log.error("Error getting portal user sessions " );
            log.error(e.getMessage());
            return new OrgUserSessionsResponse("Error");
        }
    }

    public BadgeAccessPointsResponse getOrgBadgeAccessPoints(BadgeAccessPointsRequest badgeAccessPointsRequest) {
        try{
            List<BadgeAccessPoint> badgeAccessPointList = bloxopsOrganizationDAO.getOrgBadgeAccessPoints(badgeAccessPointsRequest);

            return new BadgeAccessPointsResponse(badgeAccessPointList ,null);

        }catch(Exception e){
            log.error("Error getting badge access points. " );
            log.error(e.getMessage());
            return new BadgeAccessPointsResponse("Error");
        }
    }


    public OrgHasValidNDAResponse hasOrgValidNDA(Long org_id, String userId) {
        try{
            log.info("Getting organization with org_id " + org_id);
            Organization organization = bloxopsOrganizationDAO.getOrganizationByOrganizationId(org_id);
            String NDAFileName = organization.getOrgCode() +"-"+userId +"-NDA";
            log.info("searching" + NDAFileName);
            GetDriveItemsResponse getDriveItemResponse = filesService.searchItem(NDAFileName);
            List<OneDriveItem> driveItemList =  getDriveItemResponse.getDriveItemList();
            if(driveItemList == null|| driveItemList.size() == 0){
                log.info("No NDA found for orgnaization " + organization.getOrganizationId());
                return new OrgHasValidNDAResponse(false,null, "No NDA found." );
            }
            OneDriveItem  driveItem = driveItemList.get(0);
            if(driveItem != null ){
                log.info("Found NDA on file for " + NDAFileName);

                log.info("Checking if NDA on file is valid for " + NDAFileName +". Created date/time "+ driveItem.getCreatedDT());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E MMM d H:m:s z u");
                String date = driveItem.getCreatedDT();
                LocalDate localDate = LocalDate.parse(date, formatter);

                boolean isNDAValid = isDateWithinLast6Months(localDate);
                if(isNDAValid && driveItem.getName().equals(NDAFileName+".pdf")){
                    return new OrgHasValidNDAResponse(true,driveItem.getCreatedDT(), null);
                }else{
                    return new OrgHasValidNDAResponse(false,null, "NDA has expired.");
                }
            }else{
                log.info("No file found for " + NDAFileName);
                return new OrgHasValidNDAResponse(false,null, null);
            }
        }catch(Exception e){
            log.error(e.getMessage());
            log.error("Error getting organization NDA details with org_id " + org_id);
            return new OrgHasValidNDAResponse(false,null, "Error getting organization NDA details with org_id ");
        }
    }

    public boolean isDateWithinLast6Months(LocalDate dateToCheck){
        LocalDate currentDate = LocalDate.now();
        LocalDate currentDateMinus6Months = currentDate.minusMonths(24);
        if (dateToCheck.isBefore(currentDateMinus6Months)) {
            log.info("24 months older than current date , NDA Expired");
            return false;
        }else{
            log.info("NOT 24 months older than current date!");
            return true;
        }
    }

    public OrgServicesResponse getOrganizationServices(Long org_id) {
        try{
            log.info("Retrieving company services for :" + org_id);
            List<com.openrangelabs.services.authenticate.permission.model.OrganizationService> organizationServices = bloxopsOrganizationDAO.getAllOrganizationServices(org_id);

            return new OrgServicesResponse(organizationServices , null);

        }catch(Exception e){
            log.error(e.getMessage());
            log.error("Error retrieving company services.");
            return new OrgServicesResponse(null,"Error getting organization services.");
        }
    }
    public OrgServicesResponse getOrganizationServicesAdmin(Long org_id ,String adminKey) {
        try{
            if (!middlewareAdminKey.equals(adminKey)) {
                log.warn("GETORGANIZATIONSERVICEADMIN:Not Authorized");
                return new OrgServicesResponse(null,NOT_AUTHORIZED );
            }
            log.info("Retrieving company services for :" + org_id);
            List<com.openrangelabs.services.authenticate.permission.model.OrganizationService> organizationServices = bloxopsOrganizationDAO.getOrganizationServices(org_id);

            return new OrgServicesResponse(organizationServices , null);

        }catch(Exception e){
            log.error(e.getMessage());
            log.error("Error retrieving company services.");
            return new OrgServicesResponse(null,"Error getting organization services.");
        }
    }

    public UpdateOrgServiceAdminResponse updateOrganizationServicesAdmin(UpdateOrgServiceAdminRequest updateServiceAdminRequest) {
        if (!middlewareAdminKey.equals(updateServiceAdminRequest.getMiddlewareAdminKey())) {
            log.warn("UPDATEORGANIZATIONSERVICEADMIN:Not Authorized");
            return new UpdateOrgServiceAdminResponse(false,NOT_AUTHORIZED );
        }

        try{
            boolean success =false;

            com.openrangelabs.services.authenticate.permission.model.OrganizationService organizationService = updateServiceAdminRequest.getOrganizationService();
            log.info("Checking to see if record exists.");
             boolean exists = bloxopsOrganizationDAO.checkOrgServiceExists(organizationService.getOrganizationId(), organizationService.getServiceId());
             if(exists){
                 log.info("Record exists - attempting to update organization_services record.");
                  success = bloxopsOrganizationDAO.updateOrgService(organizationService);
             }else{
                 log.info("No record exists - attempting to insert organization_services record.");
                 success = bloxopsOrganizationDAO.addOrgService(organizationService);
             }

            return new UpdateOrgServiceAdminResponse(success,null);
        }catch(Exception e){
            log.error(e.getMessage());
            log.error("Error updating Org Service " + e.toString());
            return new UpdateOrgServiceAdminResponse(false,"Error occurred trying to update org service.");
        }


    }

    public UpdateOrgServiceAdminResponse deleteOrganizationServicesAdmin(UpdateOrgServiceAdminRequest updateServiceAdminRequest) {
        if (!middlewareAdminKey.equals(updateServiceAdminRequest.getMiddlewareAdminKey())) {
            log.warn("UPDATEORGANIZATIONSERVICEADMIN:Not Authorized");
            return new UpdateOrgServiceAdminResponse(false,NOT_AUTHORIZED );
        }

        try{
            boolean success;

            com.openrangelabs.services.authenticate.permission.model.OrganizationService organizationService = updateServiceAdminRequest.getOrganizationService();
            log.info("Checking to see if record exists.");
            boolean exists = bloxopsOrganizationDAO.checkOrgServiceExists(organizationService.getOrganizationId(), organizationService.getServiceId());
            if(exists && organizationService.getServiceId() != 3 && organizationService.getServiceId() != 2){
                log.info("Record exists - attempting to delete organization_services record.");
                success = bloxopsOrganizationDAO.deleteOrgService(organizationService);
            }else{
                log.info("No record exists - cannot delete.");
               success = false;
            }
            LogRecord logRecord = new LogRecord(0 , Integer.parseInt(String.valueOf(organizationService.getOrganizationId())),"Delete organization service " +organizationService.getServiceId() ,"Delete Org Service");
            rabbitTemplate.convertAndSend(Commons.LOGGING_EXCHANGE, "logs-user",logRecord);
            rabbitTemplate.convertAndSend(Commons.LOGGING_EXCHANGE, "logs-system",logRecord);
            return new UpdateOrgServiceAdminResponse(success,null);
        }catch(Exception e){
            log.error(e.getMessage());
            log.error("Error deleting Org Service " + e.toString());
            return new UpdateOrgServiceAdminResponse(false,"Error occurred trying to delete org service.");
        }
    }

    public DatacenterAccessReportResponse sendDatacenterSOCReportByOrganization(Long organizationId, UserIdentificationResponse user, SessionInfo sessionInfo) {
        if (null == user) {
            user = bonitaAuthenticateAPIService.getUserIdentification(sessionInfo);
        }
        String orgId = String.valueOf(organizationId);
        UserDetailsResponse userDetails = userService.getUserDetailsById(Integer.valueOf(user.getUserId()), Integer.parseInt(orgId), sessionInfo);
        User requestingUser = userDetails.getUser();
        DatacenterAccessReportResponse datacenterAccessReportResponse = new DatacenterAccessReportResponse(false, requestingUser.getUserProfile().getEmailAddress(), "");
        Organization org = bloxopsOrganizationDAO.getOrganizationByOrganizationId(organizationId);
        List<OrganizationUser> ownerList = bloxopsOrganizationDAO.getOrganizationOwners(organizationId);

        // Check that the user is an owner of the requested organization
        boolean granted = false;
        for (OrganizationUser owner : ownerList) {
            Long ownerId = owner.getUserId();
            Long userId = Long.valueOf(user.getUserId());
            if (ownerId.equals(userId)) {
                granted = true;
            }
            if (owner.getEmail().trim().contains(requestingUser.getUserProfile().getEmailAddress().trim()) && !granted) {
                granted = true;
            }
        }
        if (!granted) {
            datacenterAccessReportResponse.setError("Permission denied");
            return datacenterAccessReportResponse;
        }

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HHmmss").format(new Date());

        List<DataCenterUserAccessLog> dataCenterUserAccessLogs = getOrganizationAccessRecords(organizationId, 400);

        reportService.addReportInformation("Badging/Access Report", ""+timeStamp, ""+org.getName(), ""+requestingUser.getLastname()+", "+requestingUser.getFirstname());

        List<String> header = new ArrayList<>();
        header.add("Card Number");
        header.add("DC Access User");
        header.add("Data Center");
        header.add("Access Point");
        header.add("Message Type");
        header.add("Date Time");
        reportService.setReportHeader(header);

        int zebra = 0;
        for (DataCenterUserAccessLog accessRecord : dataCenterUserAccessLogs) {
            List<String> row = new ArrayList<>();
            row.add(accessRecord.getDataCenter().toUpperCase()+"-090-"+accessRecord.getCardNumber());
            row.add(accessRecord.getDataCenter());
            row.add(accessRecord.getMessageType());
            row.add(accessRecord.getMessageDT());
            reportService.addReportBodyLine(row, zebra);
            if (zebra > 0) {
                zebra = 0;
            } else {
                zebra = 1;
            }
        }

        if (reportService.execute()) {
            try {
                reportService.email(requestingUser.getUserProfile().getEmailAddress());
                datacenterAccessReportResponse = new DatacenterAccessReportResponse(true, requestingUser.getUserProfile().getEmailAddress(), "");
            } catch (Exception e) {
                log.error(e.getMessage());
                datacenterAccessReportResponse = new DatacenterAccessReportResponse(false, requestingUser.getUserProfile().getEmailAddress(), "Failed to send email");
            }
        }
        return datacenterAccessReportResponse;
    }

    public List<DataCenterUserAccessLog> getOrganizationAccessRecords(Long organizationId, int limit) {
        try{
            List<DataCenterUserAccessLog> dataCenterUserAccessLogs = datacenterBloxopsDAO.getDatacenterUserAccessLogsByOrganization(organizationId, limit);

            return dataCenterUserAccessLogs;
        }catch(Exception e){
            log.error(e.getMessage());
            return new ArrayList<>();
        }
    }

    public DatacenterAccessResponse getDatacenterUserAccessByOrganization(Long organizationId, int userId, SessionInfo sessionInfo) {
        try{
        String orgId = String.valueOf(organizationId);
        UserDetailsResponse userDetails = userService.getUserDetailsById(userId, Integer.parseInt(orgId), sessionInfo);
        User requestingUser = userDetails.getUser();
        Organization org = bloxopsOrganizationDAO.getOrganizationByOrganizationId(organizationId);
        List<OrganizationUser> ownerList = bloxopsOrganizationDAO.getOrganizationOwners(organizationId);
        // Check that the user is an owner of the requested organization
        boolean granted = false;
        for (OrganizationUser owner : ownerList) {
            Long ownerId = owner.getUserId();

            if (ownerId.equals((long)userId) && !granted) {
                granted = true;
            }
            if (owner.getEmail().trim().contains(requestingUser.getUserProfile().getEmailAddress().trim()) && !granted) {
                granted = true;
            }
        }
        if (!granted) {
            return new DatacenterAccessResponse("Permission denied.");
        }
        try{
            List<DataCenterUserAccessLog> dataCenterUserAccessLogs = getOrganizationAccessRecords(organizationId, 310);
            return new DatacenterAccessResponse(dataCenterUserAccessLogs);
        }catch(Exception e){
            log.error(e.getMessage());
            return new DatacenterAccessResponse("Error getting datacenter access for the organization.");
        }
        }catch (Exception e){
            log.error(e.getMessage());
            return new DatacenterAccessResponse("Error getting datacenter user access for the organization.");
        }
    }

    public DatacenterAccessResponse getDataCenterUserAccessLogsAdmin(int cardNumber){
        try{
            List<DataCenterUserAccessLog> dataCenterUserAccessLogs = datacenterBloxopsDAO.getDatacenterUserAccessLogs(cardNumber);
            return new DatacenterAccessResponse(dataCenterUserAccessLogs);
        }catch(Exception e){
            log.error(e.getMessage());
            return new DatacenterAccessResponse("Error getting datacenter access for the organization.");
        }
    }

    public UpdateOrgAdminResponse updateOrganizationAdmin(UpdateOrgAdminRequest updateOrgAdminRequest) {

        if (!middlewareAdminKey.equals(updateOrgAdminRequest.getMiddlewareAdminKey())) {
            log.warn("UPDATEORGANIZATIONSERVICEADMIN:Not Authorized");
            return new UpdateOrgAdminResponse(false,NOT_AUTHORIZED );
        }

        try{
            boolean success =false;
            Organization organization = updateOrgAdminRequest.getOrganization();
                log.info("Record exists - attempting to update organization record.");
                success = bloxopsOrganizationDAO.updateOrgStatus(organization.getOrganizationId(), Integer.parseInt(organization.getStatus()));

                    if(organization.getStatus() == "1"){
                    log.info("If enabling Organization - enable owners accounts");
                    activateOrgOwners(String.valueOf(organization.getOrganizationId()));
                     }else{
                        log.info("If disabling Organization - disable user accounts");
                       deActivateOrgUsers(String.valueOf(organization.getOrganizationId()));
                }

            LogRecord logRecord = new LogRecord(0 , Integer.parseInt(String.valueOf(organization.getOrganizationId())),"Update organization setting status -" + organization.getStatus() ,"Update Organization");
            rabbitTemplate.convertAndSend(Commons.LOGGING_EXCHANGE, "logs-user",logRecord);
            rabbitTemplate.convertAndSend(Commons.LOGGING_EXCHANGE, "logs-system",logRecord);
            log.info("Send ticket Update.");

            String comment = organization.getStatus() == "1" ? "Organization Activated." : "Organization De-activated.";
            messagingService.sendTicketUpdate(comment, updateOrgAdminRequest.getJiraId());
            return new UpdateOrgAdminResponse(success,null);
        }catch(Exception e){
            log.error(e.getMessage());
            log.error("Error updating Org Service " + e.toString());
            return new UpdateOrgAdminResponse(false,"Error occurred trying to update org service.");
        }

    }
    public void activateOrgOwners(String orgId) {
        List<OrganizationUser> orgOwnersList = bloxopsOrganizationDAO.getOrganizationOwners(Long.valueOf(orgId));
        for(OrganizationUser owner : orgOwnersList){
            bloxopsOrganizationDAO.updateOrganizationUsersStatus(true, owner.getUserId());
        }
    }

    public void deActivateOrgUsers(String orgId) {
        List<OrganizationUser> orgUsersList = bloxopsOrganizationDAO.getOrganizationUsers(Long.valueOf(orgId),true);
        for(OrganizationUser user : orgUsersList ){
            bloxopsOrganizationDAO.updateOrganizationUsersStatus(false, user.getUserId());
        }
    }

    public ChangeResponse createService(UpdateServiceRequest updateServiceRequest) {
        ChangeResponse changeResponse = new ChangeResponse();

        try{
            int updated = bloxopsOrganizationDAO.addService(updateServiceRequest.getService());
            if (updated != 0) {
                changeResponse.setSuccess(true);
                if (updateServiceRequest.getTicketId() != null && !updateServiceRequest.getTicketId().isEmpty()) {
                    messagingService.sendTicketUpdate("Service added to customer account.", updateServiceRequest.getTicketId());
                }
            } else {
                changeResponse.setError("Error adding service.");
            }
        }catch(Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            log.error("Error adding service");
            changeResponse.setError(e.getMessage());
        }
        return changeResponse;
    }

    public ChangeResponse deleteService(UpdateServiceRequest updateServiceRequest) {
        ChangeResponse changeResponse = new ChangeResponse();

        try{
            int updated = bloxopsOrganizationDAO.removeService(updateServiceRequest.getService());
            if (updated != 0) {
                changeResponse.setSuccess(true);
                if (updateServiceRequest.getTicketId() != null && !updateServiceRequest.getTicketId().isEmpty()) {
                    messagingService.sendTicketUpdate("Service updated on customer account.", updateServiceRequest.getTicketId());
                }
            } else {
                changeResponse.setError("Error updating service.");
            }
        }catch(Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            log.error("Error updating service");
            changeResponse.setError(e.getMessage());
        }
        return changeResponse;
    }

    public boolean saveAllContacts(List<OrganizationContact> contacts) {

       return bloxopsOrganizationDAO.saveAllEmergencyContacts(contacts);

    }

    public UploadContactsResponse addEmergencyContact(OrganizationContact organizationContact) {
        try {
           boolean success = bloxopsOrganizationDAO.addOrganizationContact(organizationContact);
            if(success){
                return new UploadContactsResponse(true, null);
            }else{
                return new UploadContactsResponse(false, null);
            }
        }catch(Exception e){
            e.printStackTrace();
            return new UploadContactsResponse(false, e.getMessage());
        }
    }

    public UploadContactsResponse deleteEmergencyContact(Long id) {
        try {
            boolean success = bloxopsOrganizationDAO.deleteOrganizationContact(id);
            if(success){
                return new UploadContactsResponse(true, null);
            }else{
                return new UploadContactsResponse(false, null);
            }
        }catch(Exception e){
            e.printStackTrace();
            return new UploadContactsResponse(false, e.getMessage());
        }
    }

    public UploadContactsResponse updateEmergencyContact(OrganizationContact organizationContact) {
        try {
            boolean success = bloxopsOrganizationDAO.updateOrganizationContact(organizationContact);
            if(success){
                return new UploadContactsResponse(true, null);
            }else{
                return new UploadContactsResponse(false, null);
            }
        }catch(Exception e){
            e.printStackTrace();
            return new UploadContactsResponse(false, e.getMessage());
        }
    }
}
