package com.openrangelabs.services.organization;

import com.openrangelabs.services.authenticate.model.SessionInfo;
import com.openrangelabs.services.datacenter.model.bloxops.dao.DatacenterAccessReportResponse;
import com.openrangelabs.services.datacenter.model.bloxops.dao.DatacenterAccessResponse;
import com.openrangelabs.services.log.LogResponseBodyService;
import com.openrangelabs.services.operations.jwt.JwtTokenUtil;
import com.openrangelabs.services.operations.model.ChangeResponse;
import com.openrangelabs.services.organization.model.*;
import com.openrangelabs.services.organization.model.*;
import com.openrangelabs.services.roster.model.RosterSummaryResponse;
import com.openrangelabs.services.roster.model.RosterUserResponse;
import com.openrangelabs.services.user.model.UserIdentificationResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.management.ManagementFactory;
import java.util.List;

import static com.openrangelabs.services.authenticate.bonita.BonitaAuthenticateAPIService.BONITA_API_TOKEN_NAME;
import static com.openrangelabs.services.authenticate.bonita.BonitaAuthenticateAPIService.SESSION_ID_NAME;

@RestController
@RequestMapping("/organization")

public class OrganizationController {
    OrganizationService organizationService;
    LogResponseBodyService logService;
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    public void organizationController(OrganizationService organizationService, LogResponseBodyService logService, JwtTokenUtil jwtTokenUtil) {
        this.organizationService = organizationService;
        this.logService = logService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping(value = "/admin")
    public OrganizationAdminResponse getCustomers(@RequestBody GetOrganizationAdminRequest getOrganizationAdminRequest,
                                                  HttpServletRequest request) {
        OrganizationAdminResponse response = organizationService.getCustomers(getOrganizationAdminRequest);
        return response;
    }

    @PostMapping(value = "/admin/services/{org_id}")
    public OrgServicesResponse OrgServicesResponse (@PathVariable Long org_id, @RequestBody GetOrganizationAdminRequest getOrganizationAdminRequest, HttpServletRequest request) {
        return (OrgServicesResponse) logService.logResponse(
                organizationService.getOrganizationServicesAdmin(org_id ,getOrganizationAdminRequest.getMiddlewareAdminKey()),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }

    @PutMapping(value = "/admin/services")
    public UpdateOrgServiceAdminResponse UpdateOrgService( @RequestBody UpdateOrgServiceAdminRequest updateServiceAdminRequest, HttpServletRequest request) {
        return (UpdateOrgServiceAdminResponse) logService.logResponse(
                organizationService.updateOrganizationServicesAdmin(updateServiceAdminRequest),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }

    @PostMapping(value = "/admin/update")
    public UpdateOrgAdminResponse UpdateOrgService(@RequestBody UpdateOrgAdminRequest updateOrgAdminRequest, HttpServletRequest request) {
        return (UpdateOrgAdminResponse) logService.logResponse(
                organizationService.updateOrganizationAdmin(updateOrgAdminRequest),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }

    @PostMapping(value = "/admin/services")
    public UpdateOrgServiceAdminResponse OrgServicesDelete ( @RequestBody UpdateOrgServiceAdminRequest updateServiceAdminRequest, HttpServletRequest request) {
        return (UpdateOrgServiceAdminResponse) logService.logResponse(
                organizationService.deleteOrganizationServicesAdmin(updateServiceAdminRequest),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }

    @GetMapping(value = "/{orgId}/users")
    public OrganizationUsersResponse getUsers(@PathVariable("orgId") int orgId,
                                              @CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                              @CookieValue(SESSION_ID_NAME) String sessionId,
                                              HttpServletRequest request) {
        UserIdentificationResponse user = (UserIdentificationResponse) request.getAttribute("user");
        return this.organizationService.getUsers(user,orgId, new SessionInfo(sessionToken, sessionId));
    }

    @GetMapping(value = "/admin/{orgId}/users/list")
    public OrganizationUsersList getUsersAdminList(@PathVariable("orgId") int orgId,
                                              HttpServletRequest request) {
        UserIdentificationResponse user = (UserIdentificationResponse) request.getAttribute("user");
        return this.organizationService.getUsersList(user, orgId);
    }

    @GetMapping(value = "/{orgId}/users/list")
    public OrganizationUsersList getUsersList(@PathVariable("orgId") int orgId,
                                              @CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                              @CookieValue(SESSION_ID_NAME) String sessionId,
                                              HttpServletRequest request) {
        UserIdentificationResponse user = (UserIdentificationResponse) request.getAttribute("user");
        return this.organizationService.getUsersList(user, orgId);
    }
    
    @GetMapping(value = "/{orgId}/roster")
    public RosterUserResponse getRoster(@PathVariable("orgId") int orgId,
                                        @CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                        @CookieValue(SESSION_ID_NAME) String sessionId,
                                        HttpServletRequest request) {
        return this.organizationService.getRoster(orgId);
    }

    
    @GetMapping(value = "/{orgId}/roster/summary")
    public RosterSummaryResponse getRosterSummary(@PathVariable("orgId") int orgId,
                                                  @CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                                  @CookieValue(SESSION_ID_NAME) String sessionId,
                                                  HttpServletRequest request) {
        return this.organizationService.getRosterSummary(orgId);
    }

    @GetMapping(value = "/{orgId}/owners")
    public OrganizationUserResponse getOrganizationOwners(@PathVariable("orgId") int orgId,
                                                          @CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                                        @CookieValue(SESSION_ID_NAME) String sessionId,
                                                        HttpServletRequest request) {
        UserIdentificationResponse user = (UserIdentificationResponse) request.getAttribute("user");
        return (OrganizationUserResponse) logService.logResponse(
                organizationService.getOrganizationOwners(user, new SessionInfo(sessionToken, sessionId) , orgId),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }

    @PostMapping(value = "/admin/build")
    public UpdateOrgAdminResponse buildOrganization( HttpServletRequest request , @RequestBody OrganizationBuildRequest organizationBuildRequest)  {

        return (UpdateOrgAdminResponse) logService.logResponse(
                organizationService.buildOrganization(request ,organizationBuildRequest),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }

    @GetMapping(value = "/organizations/storage")
    public List<OrganizationStorageDetails> getStorageOrganizations( HttpServletRequest request)  {

        return  organizationService.getStorageOrganizations();
    }

    @GetMapping(value = "/organizations/nostorage/")
    public List<OrganizationDetails> getNonStorageOrganizations(HttpServletRequest request)  {

        return  organizationService.getNonStorageOrganizations();
    }


    @GetMapping(value = "/organizations/owners/{org_id}")
    public OrganizationUserResponse getOrganizationOwnersByOrgId(@PathVariable Long org_id, HttpServletRequest request)  {

        return  organizationService.getOrganizationOwnersByOrgId(org_id);
    }

    @GetMapping(value = "/organizations/users/{org_id}")
    public OrganizationUserResponse getOrganizationUsers(@PathVariable Long org_id, HttpServletRequest request)  {

        return  organizationService.getOrganizationUsers(org_id);
    }

    
    @PostMapping(value = "/sessions")
    public OrgUserSessionsResponse getOrgUsersSessions(@RequestBody OrgUserSessionsRequest orgUserSessionRequest,
                                             HttpServletRequest request) {
        return (OrgUserSessionsResponse) logService.logResponse(
                organizationService.getOrgUsersSessions(orgUserSessionRequest),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }
    
    @PostMapping(value = "/accesspoints")
    public BadgeAccessPointsResponse getOrgUsersSessions(@RequestBody BadgeAccessPointsRequest badgeAccessPointsRequest,
                                                         HttpServletRequest request) {
        return (BadgeAccessPointsResponse) logService.logResponse(
                organizationService.getOrgBadgeAccessPoints(badgeAccessPointsRequest),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());

    }

    @GetMapping(value = "/nda/{org_id}")
    public OrgHasValidNDAResponse hasOrgValidNDA(@PathVariable Long org_id,@CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                                    @CookieValue(SESSION_ID_NAME) String sessionId,HttpServletRequest request) {
        UserIdentificationResponse user = (UserIdentificationResponse) request.getAttribute("user");
        return (OrgHasValidNDAResponse) logService.logResponse(
                organizationService.hasOrgValidNDA(org_id,user.getUserId()),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }

    @GetMapping(value = "/services/{org_id}")
    public OrgServicesResponse OrgServicesResponse (@PathVariable Long org_id, @CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                                    @CookieValue(SESSION_ID_NAME) String sessionId, HttpServletRequest request) {
        return (OrgServicesResponse) logService.logResponse(
                organizationService.getOrganizationServices(org_id),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }

    @GetMapping(value = "/admin/services/{org_id}")
    public OrgServicesResponse OrgServicesResponse (@PathVariable Long org_id, HttpServletRequest request) {
        return (OrgServicesResponse) logService.logResponse(
                organizationService.getOrganizationServices(org_id),
                request.getMethod(),
                request.getPathInfo(),
                ManagementFactory.getRuntimeMXBean().getName());
    }

    @GetMapping(value = "/report/{organizationId}/soc")
    public DatacenterAccessReportResponse getDatacenterUserAccessLogs(@PathVariable Long organizationId,
                                                                      @CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                                                      @CookieValue(SESSION_ID_NAME) String sessionId,
                                                                      HttpServletRequest request) {
        UserIdentificationResponse user = (UserIdentificationResponse) request.getAttribute("user");
        return (DatacenterAccessReportResponse) logService.logResponse(
                organizationService.sendDatacenterSOCReportByOrganization(organizationId, user, new SessionInfo(sessionToken, sessionId)),
                request.getMethod(),
                request.getPathInfo(),
                request.getHeader("pid"));
    }

    @GetMapping(value = "/accesslogs/{organizationId}/{userId}")
    public DatacenterAccessResponse getDatacenterOrganizationUserAccessLogs(@PathVariable Long organizationId,@PathVariable int userId,
                                                                            @CookieValue(BONITA_API_TOKEN_NAME) String sessionToken,
                                                                            @CookieValue(SESSION_ID_NAME) String sessionId,
                                                                            HttpServletRequest request) {
        return (DatacenterAccessResponse) logService.logResponse(
                organizationService.getDatacenterUserAccessByOrganization(organizationId, userId, new SessionInfo(sessionToken, sessionId)),
                request.getMethod(),
                request.getPathInfo(),
                request.getHeader("pid"));
    }

    @PostMapping(value = "/admin/service")
    public ChangeResponse addService(HttpServletRequest request, @RequestBody UpdateServiceRequest updateServiceRequest)  {
        return organizationService.createService(updateServiceRequest);
    }

    @PostMapping(value = "/admin/service/remove")
    public ChangeResponse deleteService(HttpServletRequest request, @RequestBody UpdateServiceRequest updateServiceRequest)  {
        return organizationService.deleteService(updateServiceRequest);
    }

}
