package com.openrangelabs.services.ticket.model;
import lombok.Data;

@Data
public class CaseDetailContext {
    private boolean isSupervisor;
    private float caseid;
    private boolean isCaseArchived;
    private float caseiduse;
    private boolean isProcessOverview;
    private boolean isAdministrator;
    private float processdefinitionid;
    private boolean isProcessInstanciation;
    private float userid;
    private boolean isTaskExecution;
    private String taskid;
    private String username;
}
