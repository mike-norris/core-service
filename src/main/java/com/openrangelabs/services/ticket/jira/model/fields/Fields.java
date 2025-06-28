package com.openrangelabs.services.ticket.jira.model.fields;

import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public class Fields {

    @Value("${environment}")
    String environment;

    JiraStatus status;
    String summary;
    String timespent;
    List<String> fixVersions;
    String aggregatetimespent;
    Object resolution;
    String resolutiondate;
    Integer workratio;
    String lastViewed;
    String created;

    String customfield_10500; // production name
    String customfield_10502; // production phone or dev phone
    String customfield_10800; // production company
    JiraCompanyFusebill customfield_11407; // production company::fusebill
    String customfield_10318; // production email
    String customfield_11134; // production description

    String customfield_10501; // dev name
    String customfield_10504; // dev company
    String customfield_10503; // dev email
    String customfield_10505; // dev description
    JiraCompanyFusebill customfield_10506; // dev company::fusebill

    List<String> labels;
    Integer timeestimate;
    String aggregatetimeoriginalestimate;
    List<String> versions;
    List<String> issuelinks;
    String updated;
    IssueType issueType;
    Project project;
    Watches watches;
    Priority priority;
    Assignee assignee;

    public String getSummary(){return summary;}
    public void setSummary(String summary){this.summary = summary;}

    public IssueType getIssueType() {
        return issueType;
    }
    public void setIssueType(IssueType issueType) {
        this.issueType = issueType;
    }

    public String getTimespent() {
        return timespent;
    }
    public void setTimespent(String timespent) {
        this.timespent = timespent;
    }

    public List<String> getFixVersions() {
        return fixVersions;
    }
    public void setFixVersions(List<String> fixVersions) {
        this.fixVersions = fixVersions;
    }

    public String getAggregatetimespent() {
        return aggregatetimespent;
    }
    public void setAggregatetimespent(String aggregatetimespent) {
        this.aggregatetimespent = aggregatetimespent;
    }

    public Object getResolution() {
        return resolution;
    }
    public void setResolution(Object resolution) {
        this.resolution = resolution;
    }

    public String getResolutiondate() {
        return resolutiondate;
    }
    public void setResolutiondate(String resolutiondate) {
        this.resolutiondate = resolutiondate;
    }

    public Integer getWorkratio() {
        return workratio;
    }
    public void setWorkratio(Integer workratio) {
        this.workratio = workratio;
    }

    public String getLastViewed() {
        return lastViewed;
    }
    public void setLastViewed(String lastViewed) {
        this.lastViewed = lastViewed;
    }

    public String getCreated() {
        return created;
    }
    public void setCreated(String created) {
        this.created = created;
    }

    public String getCustomfield_10500() { return customfield_10500; }
    public void setCustomfield_10500(String customfield_10500) { this.customfield_10500 = customfield_10500; }

    public String getCustomfield_10501() { return customfield_10501; }
    public void setCustomfield_10501(String customfield_10501) { this.customfield_10501 = customfield_10501; }

    public String getCustomfield_10502() { return customfield_10502; }
    public void setCustomfield_10502(String customfield_10502) { this.customfield_10502 = customfield_10502; }

    public String getCustomfield_10503() { return customfield_10503; }
    public void setCustomfield_10503(String customfield_10503) { this.customfield_10503 = customfield_10503; }

    public String getCustomfield_10504() { return customfield_10504; }
    public void setCustomfield_10504(String customfield_10504) { this.customfield_10504 = customfield_10504; }

    public String getCustomfield_10505() { return customfield_10505; }
    public void setCustomfield_10505(String customfield_10505) { this.customfield_10505 = customfield_10505; }

    public JiraCompanyFusebill getCustomfield_10506(){return customfield_10506;}
    public void setCustomfield_10506(  JiraCompanyFusebill customfield_10506){this.customfield_10506 = customfield_10506;}

    public String getCustomfield_10318(){return customfield_10318;}
    public void setCustomfield_10318(String customfield_10318){ this.customfield_10318 = customfield_10318; }

    public String getCustomfield_10800(){return customfield_10318;}
    public void setCustomfield_18800(String customfield_10800) { this.customfield_10800 = customfield_10800; }

    public JiraCompanyFusebill getCustomfield_11407() { return customfield_11407; }
    public void setCustomfield_11407(JiraCompanyFusebill customfield_11407) { this.customfield_11407 = customfield_11407; }

    public String getCustomfield_11134() { return customfield_11134; }
    public void setCustomfield_11134(String customfield_11134) { this.customfield_11134 = customfield_11134; }

    public List<String> getLabels() {
        return labels;
    }
    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public Integer getTimeestimate() {
        return timeestimate;
    }
    public void setTimeestimate(Integer timeestimate) {
        this.timeestimate = timeestimate;
    }

    public String getAggregatetimeoriginalestimate() {
        return aggregatetimeoriginalestimate;
    }
    public void setAggregatetimeoriginalestimate(String aggregatetimeoriginalestimate) {
        this.aggregatetimeoriginalestimate = aggregatetimeoriginalestimate;
    }

    public List<String> getVersions() {
        return versions;
    }
    public void setVersions(List<String> versions) {
        this.versions = versions;
    }

    public List<String> getIssuelinks() {
        return issuelinks;
    }
    public void setIssuelinks(List<String> issuelinks) {
        this.issuelinks = issuelinks;
    }

    public String getUpdated() {
        return updated;
    }
    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public JiraStatus getStatus() {
        return status;
    }
    public void setStatus(JiraStatus status) {
        this.status = status;
    }
}
