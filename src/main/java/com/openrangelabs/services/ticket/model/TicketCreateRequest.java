package com.openrangelabs.services.ticket.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TicketCreateRequest {
    String PORTAL ="portal";

    String requester;
    @JsonProperty("requester_id")
    String requesterId;
    @JsonProperty("public_request")
    String publicRequest;
    String status;
    String title;
    String description;
    String priority;
    @JsonProperty("internal_priority")
    String internalPriority;
    String type;
    @JsonProperty("request_type")
    String requestType;
    @JsonProperty("request_method")
    String requestMethod = PORTAL;
    @JsonProperty("process_id")
    String processId;
    @JsonProperty("companyId")
    String customerId;
    @JsonProperty("userID")
    String userId;
    @JsonProperty("jira_ticket")
    String jiraTicket = "";
    String location;
    @JsonProperty("impact_to_customer")
    String impactToCustomer;
    @JsonProperty("impact_to_openrangelabs")
    String impactToOrl;
    @JsonProperty("resolution_notes")
    String resolutionNotes;
    @JsonProperty("general_support_question_type")
    String generalSupportQuestionType;
    @JsonProperty("general_support_question")
    String generalSupportQuestion;
    @JsonProperty("general_support_equipment")
    String generalSupportEquipment;
    @JsonProperty("feedback_type")
    String feedbackType;
    @JsonProperty("feedback_jira_ticket")
    String feedbackJiraTicket;
    @JsonProperty("feedback_browser_data")
    String feedbackBrowserData;
    @JsonProperty("billing_details")
    List<TicketBillingDetails> billingDetails;
    @JsonProperty("outage_details")
    List<TicketOutageDetails> outageDetails;
    @JsonProperty("related_tickets")
    List<RelatedTicket> relatedTickets;
    List<TicketAssignee> assignees;
    @JsonProperty("request_log")
    List<TicketRequestLog> requestLog;
    @JsonProperty("comments")
    List<TicketComment> comments;
    @JsonProperty("attachments")
    List<TicketAttachment> attachments;
    @JsonProperty("notifications")
    List<TicketNotification> notifications;
    @JsonProperty("related_services")
    List<RelatedService> relatedServices;
    @JsonProperty("test_results")
    List<TestResult> testResults;
    String storageDeleteId;
    String service = "";
    @JsonProperty("record_id")
    String recordId;
    @JsonProperty("invite_id")
    String inviteId;
    String browserInfo;
    @JsonProperty("requester_contact_type")
    String requesterContactType;
    @JsonProperty("requester_contact_value")
    String requesterContactValue;

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public String getRequesterContactType() { return requesterContactType; }

    public void setRequesterContactType(String requesterContactType) { this.requesterContactType = requesterContactType; }

    public String getRequesterContactValue() { return requesterContactValue; }

    public void setRequesterContactValue(String requesterContactValue) { this.requesterContactValue = requesterContactValue; }

    public String getPublicRequest() {
        return publicRequest;
    }

    public void setPublicRequest(String publicRequest) {
        this.publicRequest = publicRequest;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getInternalPriority() {
        return internalPriority;
    }

    public void setInternalPriority(String internalPriority) {
        this.internalPriority = internalPriority;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
        if (null == requestMethod) {
            this.requestMethod = PORTAL;
        } else {
            if (requestMethod.equals("")) {
                this.requestMethod = PORTAL;
            }
        }
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getJiraTicket() {
        return jiraTicket;
    }

    public void setJiraTicket(String jiraTicket) {
        this.jiraTicket = jiraTicket;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImpactToCustomer() {
        return impactToCustomer;
    }

    public void setImpactToCustomer(String impactToCustomer) {
        this.impactToCustomer = impactToCustomer;
    }

    public String getImpactToOrl() {
        return impactToOrl;
    }

    public void setImpactToOrl(String impactToOrl) {
        this.impactToOrl = impactToOrl;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }

    public void setResolutionNotes(String resolutionNotes) {
        this.resolutionNotes = resolutionNotes;
    }

    public String getGeneralSupportQuestionType() {
        return generalSupportQuestionType;
    }

    public void setGeneralSupportQuestionType(String generalSupportQuestionType) {
        this.generalSupportQuestionType = generalSupportQuestionType;
    }

    public String getGeneralSupportQuestion() {
        return generalSupportQuestion;
    }

    public void setGeneralSupportQuestion(String generalSupportQuestion) {
        this.generalSupportQuestion = generalSupportQuestion;
    }

    public String getGeneralSupportEquipment() {
        return generalSupportEquipment;
    }

    public void setGeneralSupportEquipment(String generalSupportEquipment) {
        this.generalSupportEquipment = generalSupportEquipment;
    }

    public String getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(String feedbackType) {
        this.feedbackType = feedbackType;
    }

    public String getFeedbackJiraTicket() {
        return feedbackJiraTicket;
    }

    public void setFeedbackJiraTicket(String feedbackJiraTicket) {
        this.feedbackJiraTicket = feedbackJiraTicket;
    }

    public String getFeedbackBrowserData() {
        return feedbackBrowserData;
    }

    public void setFeedbackBrowserData(String feedbackBrowserData) {
        this.feedbackBrowserData = feedbackBrowserData;
    }

    public List<TicketBillingDetails> getBillingDetails() {
        return billingDetails;
    }

    public void setBillingDetails(List<TicketBillingDetails> billingDetails) {
        this.billingDetails = billingDetails;
    }

    public List<TicketOutageDetails> getOutageDetails() {
        return outageDetails;
    }

    public void setOutageDetails(List<TicketOutageDetails> outageDetails) {
        this.outageDetails = outageDetails;
    }

    public List<RelatedTicket> getRelatedTickets() {
        return relatedTickets;
    }

    public void setRelatedTickets(List<RelatedTicket> relatedTickets) {
        this.relatedTickets = relatedTickets;
    }

    public List<TicketAssignee> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<TicketAssignee> assignees) {
        this.assignees = assignees;
    }

    public List<TicketRequestLog> getRequestLog() {
        return requestLog;
    }

    public void setRequestLog(List<TicketRequestLog> requestLog) {
        this.requestLog = requestLog;
    }

    public List<TicketComment> getComments() {
        return comments;
    }

    public void setComments(List<TicketComment> comments) {
        this.comments = comments;
    }

    public List<TicketAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<TicketAttachment> attachments) {
        this.attachments = attachments;
    }

    public List<TicketNotification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<TicketNotification> notifications) {
        this.notifications = notifications;
    }

    public List<RelatedService> getRelatedServices() {
        return relatedServices;
    }

    public void setRelatedServices(List<RelatedService> relatedServices) {
        this.relatedServices = relatedServices;
    }

    public List<TestResult> getTestResults() {
        return testResults;
    }

    public void setTestResults(List<TestResult> testResults) {
        this.testResults = testResults;
    }

    public String getStorageDeleteId() {
        return storageDeleteId;
    }

    public void setStorageDeleteId(String storageDeleteId) {
        this.storageDeleteId = storageDeleteId;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) { this.service = service; }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) { this.recordId = recordId; }

    public String getBrowserInfo() {
        return browserInfo;
    }

    public void setBrowserInfo(String browserInfo) {
        this.browserInfo = browserInfo;
    }
}
