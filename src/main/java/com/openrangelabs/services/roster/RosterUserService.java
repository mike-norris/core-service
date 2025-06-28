package com.openrangelabs.services.roster;

import com.openrangelabs.services.authenticate.AuthenticateService;
import com.openrangelabs.services.authenticate.permission.bloxops.dao.PermissionDAO;
import com.openrangelabs.services.datacenter.bloxops.dao.mapper.DatacenterBloxopsDAO;
import com.openrangelabs.services.documents.dao.DocumentsBloxopsDAO;
import com.openrangelabs.services.organization.bloxops.dao.BloxopsOrganizationDAO;
import com.openrangelabs.services.organization.model.OrganizationUser;
import com.openrangelabs.services.roster.entity.*;
import com.openrangelabs.services.roster.model.*;
import com.openrangelabs.services.roster.entity.RosterBadge;
import com.openrangelabs.services.roster.entity.RosterUser;
import com.openrangelabs.services.roster.entity.RosterUserDatacenter;
import com.openrangelabs.services.roster.model.GetBadgeDetailsRequest;
import com.openrangelabs.services.roster.model.GetBadgeDetailsResponse;
import com.openrangelabs.services.roster.model.RosterUserUpdateRequest;
import com.openrangelabs.services.roster.model.RosterUserUpdateResponse;
import com.openrangelabs.services.signing.dao.SigningBloxopsDAO;
import com.openrangelabs.services.roster.bloxops.dao.RosterBloxopsDAO;
import com.openrangelabs.services.user.UserService;
import com.openrangelabs.services.user.bonita.BonitaUserAPIService;
import com.openrangelabs.services.user.profile.dao.UserBloxopsDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class RosterUserService {
    private RosterBloxopsDAO rosterBloxopsDAO;
    BonitaUserAPIService bonitaUserAPIService;
    AuthenticateService authenticateService;
    BloxopsOrganizationDAO bloxopsOrganizationDAO;
    UserBloxopsDAO userBloxopsDAO;
    UserService userService;
    DocumentsBloxopsDAO documentsBloxopsDao;
    SigningBloxopsDAO signingBloxopsDAO;
    DatacenterBloxopsDAO datacenterBloxopsDAO;
    PermissionDAO permissionDAO;


    @Value("${badgeJSONFileLocation}")
    String badgeJSONFileLocation;

    @Value("${bonitaEnvironment}")
    String environment;

    @Autowired
    RosterUserService(RosterBloxopsDAO rosterBloxopsDAO, BonitaUserAPIService bonitaUserAPIService,
                      BloxopsOrganizationDAO bloxopsOrganizationDAO, UserBloxopsDAO userBloxopsDAO , DocumentsBloxopsDAO documentsBloxopsDAO, SigningBloxopsDAO signingBloxopsDAO, DatacenterBloxopsDAO datacenterBloxopsDAO ,PermissionDAO permissionDAO, AuthenticateService authenticateService,UserService userService) {

        this.rosterBloxopsDAO = rosterBloxopsDAO;
        this.bonitaUserAPIService = bonitaUserAPIService;
        this.bloxopsOrganizationDAO = bloxopsOrganizationDAO;
        this.userBloxopsDAO = userBloxopsDAO;
        this.documentsBloxopsDao = documentsBloxopsDAO;
        this.signingBloxopsDAO = signingBloxopsDAO;
        this.datacenterBloxopsDAO =datacenterBloxopsDAO;
        this.permissionDAO = permissionDAO;
        this.authenticateService = authenticateService;
        this.userService = userService;
    }

    public RosterUser getDatacenterRosterUser(String email , long companyId) {
        RosterUser rosterUser = rosterBloxopsDAO.getRosterUserByEmail(email,companyId);

            rosterUser.setUserAccess(rosterBloxopsDAO.findAccessByRosterUserId(rosterUser.getId()));
            rosterUser.setDatacenterList(rosterBloxopsDAO.findDatacentersByRosterId(rosterUser.getId()));
            rosterUser.setDocuments(documentsBloxopsDao.findDocumentsByRosterId(rosterUser.getId()));
            rosterUser.setDocumentInvites(signingBloxopsDAO.findDocumentInvites(rosterUser.getEmailAddress()));
            try {
                OrganizationUser orgUser = bloxopsOrganizationDAO.getOrganizationOwners(rosterUser.getOrganizationId()).get(0);
                rosterUser.setOwner(orgUser);
            }catch(Exception e){
                log.error("No Owner found for " + rosterUser.getOrganizationId());
            }

        return rosterUser;
    }

    public RosterUser getDatacenterRosterUserById(Long id){
        return rosterBloxopsDAO.getRosterUser(id);
    }

    public GetBadgeDetailsResponse getBadgeDetails(GetBadgeDetailsRequest getBadgeDetailsRequest) {
        try{
            List<RosterBadge> rosterBadges = bloxopsOrganizationDAO.getBadgeDetails(getBadgeDetailsRequest.getRosterUserId());
            for(RosterBadge rosterBadge:rosterBadges){
                rosterBadge.setDataCenterUserAccessLogList(datacenterBloxopsDAO.getDatacenterUserAccessLogs(rosterBadge.getCardNumber()));
            }
            return new GetBadgeDetailsResponse(rosterBadges);
        }catch(Exception e){
            log.info(e.getMessage());
            log.error("Error getting badge details.");
            return new GetBadgeDetailsResponse("Error getting badge details.");
        }
    }

    public RosterUserDatacenter getRosterUserDatacenterByRosterUserId(Long rosterUserId, Long datacenterId) {
        return rosterBloxopsDAO.findRosterUserDatacenterByRosterId(rosterUserId, datacenterId);
    }

    public RosterUserUpdateResponse updateRosterUser(RosterUserUpdateRequest rosterUserUpdateRequest) {
        try{
            log.info("Updating roster user.");

            int updated = bloxopsOrganizationDAO.updateRosterUser(rosterUserUpdateRequest.getRosterUser());
            Boolean success = updated == 1 ? true:false;
            return new RosterUserUpdateResponse(success ,null);
        }catch(Exception e){
            log.error("Error updating roster user");
            log.error(e.getMessage());
            return new RosterUserUpdateResponse(false ,"Error updating roster user");
        }
    }
}
