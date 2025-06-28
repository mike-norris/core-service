package com.openrangelabs.services.message.model.messaging;

import com.openrangelabs.services.ticket.changegear.model.Comment;
import lombok.Data;

import java.util.List;

@Data
public class EmailRequest {
    String firstname;
    String lastname;
    String submitted;
    String submitter;
    int ticketID;
    String url;
    String subject;
    String body;
    List<Comment> comments;
    String status;
    String templateName;
    String emailTo;
    String body1;
    String body2;
    String body3;
    String question;
    String title;
    String from;

}
