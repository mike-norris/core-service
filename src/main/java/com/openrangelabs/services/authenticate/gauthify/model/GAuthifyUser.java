package com.openrangelabs.services.authenticate.gauthify.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GAuthifyUser {
    @JsonProperty("created_at")
    String createdAt;
    @JsonProperty("display_name")
    String displayName;
    @JsonProperty("email")
    String email;
    @JsonProperty("groups")
    String groups;
    @JsonProperty("meta")
    String meta;
    @JsonProperty("sms_number")
    String smsNumber;
    @JsonProperty("unique_id")
    String uniqueId;
    @JsonProperty("voice_number")
    String voiceNumber;
}
