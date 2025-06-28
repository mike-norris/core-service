package com.openrangelabs.services.ticket.jira.model.fields;

import lombok.Getter;

import java.util.List;

public class Comment {
    @Getter
    String self;
    @Getter
    String id;
    @Getter
    Object author;
    @Getter
    String body;
    @Getter
    Object updateAuthor;
    @Getter
    String created;
    @Getter
    String updated;
    @Getter
    List<Property> properties;
    Visibility visibility;

    public void setSelf(String self) {
        this.self = self;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAuthor(Object author) {
        this.author = author;
    }

    public void setBody(String body) { this.body = body; }

    public void setUpdateAuthor(Object updateAuthor) {
        this.updateAuthor = updateAuthor;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public Visibility getVisibility() {
        try {
            return this.visibility;
        } catch (Exception e) {
            return new Visibility();
        }
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }
}
