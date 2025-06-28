package com.openrangelabs.services.ticket.model;

import java.util.List;


import lombok.Data;

@Data
public class TicketUpdateRequest {
    String jiraId;
    String processid;
    String caseid;
    String invite_id;
    String comment;
    List<TicketAttachment> files;
    String file;
    String status;
    String organizationId;

    public TicketUpdateRequest(String processid, String caseid, String comment, List<TicketAttachment> files, String status) {
        this.processid = processid;
        this.caseid = caseid;
        this.comment = comment;
        this.files = files;
        this.status = status;
        this.invite_id = "";
    }

    public TicketUpdateRequest() {
    }
}
