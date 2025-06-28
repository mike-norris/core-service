package com.openrangelabs.services.signing.modelNew;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CreateDocumentResponse {
    @JsonProperty("id")
    String id;

    @JsonProperty("document_name")
    String document_name;

    @JsonProperty("errors")
    List<SignNowError> errors;
}
