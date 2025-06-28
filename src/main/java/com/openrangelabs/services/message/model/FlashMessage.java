package com.openrangelabs.services.message.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.openrangelabs.services.message.FlashMessageService;
import lombok.Data;

@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = FlashMessageService.class)
public class FlashMessage {
    private Long organizationId;
    private Long userId;
    private String fromName;
    private String expiration;
    private String type;
    private int level;
    private String message;

    public Long getOrganizationId() {
        return this.organizationId;
    }



    public Long getUserId() {
        return this.userId;
    }

    public String getFromName() {
        return this.fromName;
    }

    public String getExpiration() {
        return this.expiration;
    }

    public int getLevel() {
        return this.level;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type.toLowerCase();
    }

    public String getMessage() {
        return this.message;
    }
}
