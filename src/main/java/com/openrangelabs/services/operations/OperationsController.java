package com.openrangelabs.services.operations;

import com.openrangelabs.services.datacenter.model.bloxops.dao.DatacenterAccessResponse;
import com.openrangelabs.services.operations.jwt.*;
import com.openrangelabs.services.operations.model.*;
import com.openrangelabs.services.operations.jwt.*;
import com.openrangelabs.services.operations.model.*;
import com.openrangelabs.services.organization.OrganizationService;
import com.openrangelabs.services.organization.model.OrganizationContact;
import com.openrangelabs.services.organization.model.OrganizationDetails;
import com.openrangelabs.services.roster.RosterUserService;
import com.openrangelabs.services.roster.model.GetBadgeDetailsResponse;
import com.openrangelabs.services.roster.model.RosterUserUpdateRequest;
import com.openrangelabs.services.roster.model.RosterUserUpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/operations")
public class OperationsController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    JwtUserDetailsService jwtUserDetailsService;

    RosterUserService rosterUserService;
    OrganizationService organizationService;
    OperationsService operationsService;

    @Autowired()
    public void rosterUserController(RosterUserService rosterUserService, OrganizationService organizationService, OperationsService operationsService) {
        this.rosterUserService = rosterUserService;
        this.organizationService = organizationService;
        this.operationsService = operationsService;
    }

    @PostMapping(value = "/login")
    public JwtResponse createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception
    {
        try {
            authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
            final User userDetails = jwtUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());
            final String token = jwtTokenUtil.generateToken(userDetails);

            return new JwtResponse(token);
        }catch(Exception e){
            return new JwtResponse(null);
        }

    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    @PostMapping(value = "/validate")
    public JWTValidateResponse validateJWT(@RequestBody JWTValidateRequest jwtValidateRequest) {
        try {
            return new JWTValidateResponse(jwtTokenUtil.isTokenExpired(jwtValidateRequest.getToken()));
        }catch(Exception e){
            return new JWTValidateResponse(true);
        }
    }

    @PostMapping(value = "/badges/{rosterUserId}")
    public GetBadgeDetailsResponse getBadges(@RequestBody JWTValidateRequest jwtValidateRequest , @PathVariable int rosterUserId) {
        return operationsService.getBadges(jwtValidateRequest , rosterUserId);
    }

    @PostMapping(value = "/badgeAccess/{cardNumber}")
    public DatacenterAccessResponse getBadgeAccessLogs(@RequestBody JWTValidateRequest jwtValidateRequest , @PathVariable int cardNumber) {
       return operationsService.getBadgeAccessLogs(jwtValidateRequest,cardNumber);
    }

    @PostMapping(value = "/roster/update")
    public RosterUserUpdateResponse updateRosterUser(@RequestBody RosterUserUpdateRequest rosterUserUpdateRequest ) {
       return operationsService.updateRosterUser(rosterUserUpdateRequest);
    }

    @GetMapping(value = "/links")
    public GetLinksResponse getLinks(HttpServletRequest request ) {
        return operationsService.getLinks(request);
    }

    @GetMapping(value = "/feed")
    public GetFeedResponse getFeed(HttpServletRequest request ) {
        return operationsService.getFeed();
    }

    @GetMapping(value = "/services")
    public GetServicesResponse getServices(HttpServletRequest request ) {
        return operationsService.getServices();
    }

    @GetMapping(value = "/logs/{logType}")
    public GetLogsResponse getLogs(HttpServletRequest request , @PathVariable String logType) {
        return operationsService.getLogs(logType);
    }

    @GetMapping(value = "/logs/user/{userId}")
    public GetLogsResponse getUserLogs(HttpServletRequest request , @PathVariable Integer userId) {
        return operationsService.getUserLogs(userId);
    }

    @PostMapping(value = "/organizations/")
    public List<OrganizationDetails> getOrganizations(HttpServletRequest request)  {
        List<OrganizationDetails> organizationDetails = new ArrayList<>();

        organizationDetails = organizationService.getOrganizations();

        return organizationDetails;
    }

    @GetMapping(value = "/organization/contacts")
    public GetContactsResponse getAllEmergencyContacts(HttpServletRequest request) {
        return operationsService.getEmergencyContacts(null);
    }

    @PostMapping(value = "/organization/contacts/{orgId}")
    public GetContactsResponse getAllEmergencyContacts(HttpServletRequest request,
                                                          @PathVariable String orgId) {
        return operationsService.getEmergencyContacts(orgId);
    }

    @PostMapping(value = "/organization/contacts")
    public UploadContactsResponse processEmergencyContacts(HttpServletRequest request,
                                             @RequestParam("file") MultipartFile file) {
        return operationsService.processFile(file, "contacts");
    }

    @PostMapping(value = "/organization/contacts/add")
    public UploadContactsResponse addEmergencyContacts(HttpServletRequest request,
                                                       @RequestBody OrganizationContact organizationContact) {
        return organizationService.addEmergencyContact(organizationContact);
    }

    @PostMapping(value = "/organization/contacts/update")
    public UploadContactsResponse updateEmergencyContacts(HttpServletRequest request,
                                                       @RequestBody OrganizationContact organizationContact) {
        return organizationService.updateEmergencyContact(organizationContact);
    }

    @PostMapping(value = "/organization/contacts/delete/{contactId}")
    public UploadContactsResponse deleteEmergencyContacts(HttpServletRequest request,
                                                          @PathVariable Long contactId) {
        return organizationService.deleteEmergencyContact(contactId);
    }

    @PutMapping(value = "/links/update")
    public LinksResponse updateLink(HttpServletRequest request,@RequestBody LinksUpdateRequest linksUpdateRequest)  {
        LinksResponse linksResponse = new LinksResponse();

        linksResponse = operationsService.updateLinks(linksUpdateRequest);

        return linksResponse;
    }

    @PostMapping(value = "/links/add")
    public LinksResponse addLink(HttpServletRequest request,@RequestBody LinksUpdateRequest linksUpdateRequest)  {
        LinksResponse linksResponse = new LinksResponse();

        linksResponse = operationsService.addLink(linksUpdateRequest);

        return linksResponse;
    }

    @PostMapping(value = "/links/delete")
    public LinksResponse deleteLink(HttpServletRequest request,@RequestBody LinksUpdateRequest linksUpdateRequest)  {
        LinksResponse linksResponse = new LinksResponse();

        linksResponse = operationsService.deleteLink(linksUpdateRequest);

        return linksResponse;
    }

    @GetMapping(value = "/alerts")
    public AlertsResponse getAlerts(HttpServletRequest request) {
        return operationsService.getAlerts();
    }

    @GetMapping(value = "/alert")
    public AlertsResponse getAlert(HttpServletRequest request) {
        return operationsService.getAlert();
    }

    @PostMapping(value = "/alert")
    public AlertsResponse createAlert(HttpServletRequest request, @RequestBody Alert alert ) {
        return operationsService.createAlert(alert);
    }

    @PutMapping(value = "/alert")
    public AlertsResponse updateAlert(HttpServletRequest request, @RequestBody Alert alert ) {
        return operationsService.updateAlert(alert);
    }

    @GetMapping(value = "/subscriptions")
    public SubscriptionResponse getSubscriptions(HttpServletRequest request) {
        return operationsService.getSubscriptions();
    }

    @PostMapping(value = "/subscriptions")
    public SubscriptionResponse createSubscriptions(HttpServletRequest request, @RequestBody Subscription subscription ) {
        return operationsService.createSubscription(subscription);
    }

    @PutMapping(value = "/subscriptions")
    public SubscriptionResponse updateSubscriptions(HttpServletRequest request, @RequestBody Subscription subscription ) {
        return operationsService.updateSubscription(subscription);
    }

    @PostMapping(value = "/subscription/delete")
    public SubscriptionResponse deleteSubscriptions(HttpServletRequest request, @RequestBody Subscription subscription ) {
        return operationsService.deleteSubscription(subscription);
    }

    @GetMapping(value = "/tags/{linkId}")
    public TagResponse getTags(HttpServletRequest request , @PathVariable int linkId) {
        return operationsService.getTags(linkId);
    }

    @PostMapping(value = "/tags")
    public TagResponse updateTags(HttpServletRequest request, @RequestBody TagRequest tagRequest, @RequestBody Subscription subscription ) {
        return operationsService.updateTag(tagRequest);
    }

    @GetMapping(value = "/timesheets/{date}")
    public List<Timesheet> getTimesheets(HttpServletRequest request ,@PathVariable String date) {
        return operationsService.getTimesheets(date);
    }

    @PostMapping(value = "/signature")
    public SignatureEmailResponse sendSignatureEmail(HttpServletRequest request, @RequestBody SignatureEmailRequest signatureEmailRequest, @RequestBody Subscription subscription ) throws IOException {
        return operationsService.sendSignatureEmail(signatureEmailRequest);
    }

}