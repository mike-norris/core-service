package com.openrangelabs.services.signing.model;

import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class CreateDocumentFromTemplateRequest {

    public CreateDocumentFromTemplateRequest() {
        this.escort = false;
        this.guest = false;
        this.vendor = false;
    }

    public String documentName;

    public String templateName;

    @Nullable
    public Long rosterId;

    @Nullable
    public String email;

    @Nullable
    public Long organizationId;

    @Nullable
    public Long requestorId;

    @Nullable
    public Long datacenterId;

    @Nullable
    public Boolean escort;

    @Nullable
    public Boolean vendor;

    @Nullable
    public Boolean guest;

    public void setGuest(boolean guest) {
        if (guest) {
            this.escort = true;
        }
        this.guest = guest;
    }
}
