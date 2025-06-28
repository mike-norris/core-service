package com.openrangelabs.services.notifications;

import com.openrangelabs.services.authenticate.model.SessionInfo;
import com.openrangelabs.services.datacenter.entity.DataCenterUserAccessLog;
import com.openrangelabs.services.datacenter.model.bloxops.dao.DatacenterAccessResponse;
import com.openrangelabs.services.notifications.dao.BloxopsNotificationsDAO;
import com.openrangelabs.services.notifications.model.Notification;
import com.openrangelabs.services.notifications.model.NotificationsResponse;
import com.openrangelabs.services.organization.OrganizationService;
import com.openrangelabs.services.organization.entity.PortalUserSession;
import com.openrangelabs.services.organization.model.OrgUserSessionsRequest;
import com.openrangelabs.services.organization.model.OrgUserSessionsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class NotificationsService {

    BloxopsNotificationsDAO bloxopsNotificationsDAO;
    OrganizationService organizationService;

    @Autowired
    public void notificationsService(BloxopsNotificationsDAO bloxopsNotificationsDAO , OrganizationService organizationService) {
        this.bloxopsNotificationsDAO = bloxopsNotificationsDAO;
        this.organizationService = organizationService;
    }

    public NotificationsResponse getUserAccess(int userId) {
        try{
            List<Notification> notificationList = bloxopsNotificationsDAO.getUserAccess(userId);
            return new NotificationsResponse(notificationList , null);
        }catch(Exception e){
            log.error(e.getMessage());
            return new NotificationsResponse(null , "Error retrieving user access logs");
        }
    }

    public NotificationsResponse getPortalSessions(OrgUserSessionsRequest orgUserSessionRequest) {
       OrgUserSessionsResponse orgUserSessionsResponse =  organizationService.getOrgUsersSessions(orgUserSessionRequest);
        List<PortalUserSession> orgUserSessionList = orgUserSessionsResponse.getOrgUserSessionList();
        List<Notification> notificationsList = new ArrayList<>();
        for(PortalUserSession portalUserSession : orgUserSessionList){
            Notification notification = new Notification();
            notification.setEventtype(portalUserSession.getDescription());
            notification.setUserid(portalUserSession.getUser_id());
            notification.setAccesstype(portalUserSession.getDescription());
            notification.setCreated_dt(portalUserSession.getEvent_date());
            notificationsList.add(notification);
        }
        return new NotificationsResponse(notificationsList ,null);
    }

    public NotificationsResponse getDatacenterUserAccess(Long organizationId,int userId ,SessionInfo sessionInfo) {
       try {
           DatacenterAccessResponse datacenterAccessResponse = organizationService.getDatacenterUserAccessByOrganization(organizationId, userId, sessionInfo);
           List<DataCenterUserAccessLog> dataCenterUserAccessLogs = datacenterAccessResponse.getDataCenterUserAccessLogs();
           List<Notification> notificationsList = new ArrayList<>();
           for (DataCenterUserAccessLog dataCenterUserAccessLog : dataCenterUserAccessLogs) {
               Notification notification = new Notification();
               notification.setEventtype(dataCenterUserAccessLog.getMessageText());
               notification.setAccesstype(dataCenterUserAccessLog.getMessageType());
               notification.setUserid(userId);
               notification.setCreated_dt(dataCenterUserAccessLog.getMessageDT());
               notificationsList.add(notification);
           }
           return new NotificationsResponse(notificationsList, null);
       }catch(Exception e){
           log.error(e.getMessage());
           return new NotificationsResponse(null , "Error retrieving badge access logs");
       }
    }
}
