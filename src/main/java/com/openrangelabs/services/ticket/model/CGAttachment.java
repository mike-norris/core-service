package com.openrangelabs.services.ticket.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CGAttachment {
    @JsonProperty("File")
    String File;
    @JsonProperty("Type")
    String Type;
    @JsonProperty("FileContent")
    String FileContent;
    @JsonProperty("ID")
    String ID;
}
