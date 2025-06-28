package com.openrangelabs.services.ticket.jira.model;

import com.openrangelabs.services.ticket.jira.model.fields.Author;
import lombok.Data;

@Data
public class JiraCommentDetails {
    String self;
    Long id;
    Author author;
    String body;
    Object updateAuthor;
    String created;
    String updated;

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Object getUpdateAuthor() {
        return updateAuthor;
    }

    public void setUpdateAuthor(Object updateAuthor) {
        this.updateAuthor = updateAuthor;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }
}
