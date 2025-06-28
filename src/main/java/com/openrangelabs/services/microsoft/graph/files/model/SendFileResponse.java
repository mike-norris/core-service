package com.openrangelabs.services.microsoft.graph.files.model;

import lombok.Data;

@Data
public class SendFileResponse {
    boolean fileSent;
    String error;

    public SendFileResponse(boolean fileSent, String error) {
        this.fileSent = fileSent;
        this.error = error;
    }
}
