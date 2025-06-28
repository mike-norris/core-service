package com.openrangelabs.services.ticket.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TicketDetail {

    private Long persistenceId;
    private String persistenceId_string;
    private Long persistenceVersion;
    private String persistenceVersion_string;
    private String created_date_time;
    private String updated_date_time;
    private String due_date_time;
    private String completed_date_time;
    private String requester;
    private Long requestor_id;
    private String requestor_id_string;
    private Boolean public_request;
    private String status;
    private String title;
    private String description;
    private String priority;
    private String internal_priority;
    private String type;
    private String request_type;
    private String request_method;
    @JsonProperty("process_id")
    private Long processId;
    private String process_id_string;
    private String customer_id;
    private Long user_id;
    private String user_id_string;
    private String jira_ticket;
    private String location;
    private String impact_to_customer;
    private String impact_to_openrangelabs;
    List<TicketAssignee> assignees;
    List<String> related_tickets;
    List<String> request_log;
    List<Comment> comments;
    List<Attachment> attachments;
    List<String> notifications;
    List<String> related_services;
    List<String> test_results;
    private String resolution_notes;
    List<String> billing_details;
    List<String> outage_details;
    private String general_support_question_type;
    private String general_support_question;
    private String general_support_equipment;
    private String feedback_type;
    private String feedback_jira_ticket;
    private String feedback_browser_data;
    private String service;
    private String record_id;
    private String invite_id;
    private String record_id_string;
    private String cStatus;

    public String getProcessId() {
        return process_id_string;
    }

    public void setProcessId(String process_id_string) {
        this.process_id_string = process_id_string;
    }
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
