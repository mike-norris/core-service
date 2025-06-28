package com.openrangelabs.services.ticket.jira.model;


import com.openrangelabs.services.ticket.jira.model.fields.Creator;
import com.openrangelabs.services.ticket.jira.model.fields.Fields;
import com.openrangelabs.services.ticket.jira.model.fields.JiraStatus;

import java.util.List;

public class JiraResponse {

    JiraStatus status;
    String expand;
    String id;
    String self;
    String key;
    Fields fields;
    String watchCount;
    String isWatching;
    String displayName;
    Boolean active;
    String timeZone;
    List<String> components;
    String timeeoriginalestimate;
    String description;
    Object timetracking;
    List<String> attachments;
    Integer aggregatetimeestimate;
    List<String> subtasks;
    String environment;
    String duedate;
    Creator creator;

    public void setTimetracking(Object timetracking) {
        this.timetracking = timetracking;
    }

    public Creator getCreator() {
        return creator;
    }

    public void setCreator(Creator creator) {
        this.creator = creator;
    }

    public Fields getFields() {
        return fields;
    }

    public void setFields(Fields fields) {
        this.fields = fields;
    }

    public Boolean getActive(){return active;}
    public void setActive(Boolean active){this.active = active;}

    public JiraStatus getStatus(){return status;}
    public void setStatus(JiraStatus status){this.status = status;}

    public String getExpand(){return expand;}
    public void setExpand(String expand){this.expand = expand;}

    public String getTimeZone(){return timeZone;}
    public void setTimeZone(String timeZone){this.timeZone = timeZone;}

    public String getEnvironment(){return environment;}
    public void setEnvironment(String environment){this.environment = environment;}

    public String getDuedate(){return duedate;}
    public void setDuedate(String duedate){this.duedate = duedate;}

    public String getDisplayName(){return displayName;}
    public void setDisplayName(String displayName){this.displayName = displayName;}

    public String getWatchCount(){return watchCount;}
    public void setWatchCount(String watchCount){this.watchCount = watchCount;}

    public String getIsWatching(){return isWatching;}
    public void setIsWatching(String isWatching){this.isWatching = isWatching;}

    public String getId(){return id;}
    public void setId(String id){this.id = id;}

    public String getSelf(){return self;}
    public void setSelf(String self){this.self = self;}

    public List<String> getAttachments(){return attachments;}
    public void setAttachments(List<String> attachments){this.attachments = attachments;}

    public String getKey(){return key;}
    public void setKey(String key){this.key = key;}


    public Integer getAggregatetimeestimate(){return aggregatetimeestimate;}
    public void setAggregatetimeestimate(Integer aggregatetimeestimate){this.aggregatetimeestimate = aggregatetimeestimate;}

    public Object getTimetracking(){return timetracking;}
    public void setTimetracking(String timetracking){this.timetracking = timetracking;}


    public List<String> getComponents(){return components;}
    public void setComponents( List<String> components){this.components = components;}

    public String getTimeeoriginalestimate(){return timeeoriginalestimate;}
    public void setTimeeoriginalestimate(String timeeoriginalestimate){this.timeeoriginalestimate = timeeoriginalestimate;}

    public String getDescription(){return description;}
    public void setDescription(String description){this.description = description;}

    public List<String> getSubtasks(){return subtasks;}
    public void setSubtasks(List<String> subtasks){this.subtasks = subtasks;}

}
