package com.openrangelabs.services.signing.modelNew;

import lombok.Data;

import java.util.List;

@Data
public class CreateDocumentTextsResponse {
    String id;
    List signatures;
    List texts;
    List checks;
    List views;
    List attachments;
    List radiobuttons;
    List hyperlinks;
    List lines;
}
