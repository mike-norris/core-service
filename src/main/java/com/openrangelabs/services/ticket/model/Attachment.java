package com.openrangelabs.services.ticket.model;

import lombok.Data;
@Data
public class Attachment {
    String created_date_time;
    String attachment_location;
    Boolean public_attachment;
    String filename;
    String name;
    Boolean removed;
    String user_id;
    AttachmentResponse details;

    public void setName(String name) {
        this.name = name;
        this.setFilename();
    }
    public void setFilename() { this.filename = this.name; }
}

