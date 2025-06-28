package com.openrangelabs.services.ticket.model;
import lombok.Data;


@Data
public class Comment {
    Long persistenceId;
    String persistenceId_string;
    Long persistenceVersion;
    String persistenceVersion_string;
    Long user_id;
    String user_id_string;
    String reference_id;
    String created_date_time;
    Boolean public_comment;
    Boolean removed;
    String comment;

}
