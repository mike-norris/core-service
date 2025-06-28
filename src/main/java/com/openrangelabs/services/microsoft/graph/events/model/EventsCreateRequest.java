package com.openrangelabs.services.microsoft.graph.events.model;

import com.openrangelabs.services.microsoft.graph.events.entity.Attendee;
import com.openrangelabs.services.microsoft.graph.events.entity.Date;
import com.openrangelabs.services.microsoft.graph.events.entity.Recurrence;
import com.openrangelabs.services.microsoft.graph.events.entity.Reminder;
import lombok.Data;

import java.util.List;

@Data
public class EventsCreateRequest {
    String dataCenter;
    List<Attendee> attendees;
    Reminder reminder;
    Recurrence recurrence;
    Date startDate;
    Date endDate;
    String eventDescription;
    String eventSubject;
    String timeZone;
    String id;
}
