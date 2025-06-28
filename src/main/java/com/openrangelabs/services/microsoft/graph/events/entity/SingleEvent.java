package com.openrangelabs.services.microsoft.graph.events.entity;

import com.microsoft.graph.models.extensions.Attendee;
import com.microsoft.graph.models.extensions.DateTimeTimeZone;

import lombok.Data;

import java.util.List;

@Data
public class SingleEvent {
    String id;
    String subject;
    String bodyPreview;
    String importance;
    String status;
    DateTimeTimeZone startDateTime;
    DateTimeTimeZone endDateTime;
    List<Attendee> attendees;
}
