package com.openrangelabs.services.ticket.jira.model.fields;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Assignee {
    String self;
    String name;
    String key ;
    String emailAddress;
    String displayName;
    Boolean active;

    String timeZone;
}
