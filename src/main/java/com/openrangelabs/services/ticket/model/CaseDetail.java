package com.openrangelabs.services.ticket.model;

import java.util.List;
import lombok.Data;

@Data
public class CaseDetail {
    List<String> notes;
    private String processid;
    CaseDetailContext context;
    private String latestComments;
    private String latestFile;
    private String taskTitle;
    SupportRequest supportRequest;
    private String status;
    private String cStatus;

    public void setStatus(String status, String cStatus) {
        this.status = status;
        if (null == status) {
            this.status = cStatus;
        }
    }

    public String getStatus() {
        if (null == this.status) {
            this.status = this.cStatus;
        }
        return this.status;
    }
}
