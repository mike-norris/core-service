package com.openrangelabs.services.bonita.model;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class BdmActiveMessageResponse {
    Long persistenceId;
    String name;
    String message;
    boolean approved;
    boolean enabled;
    OffsetDateTime start_dt;
    OffsetDateTime end_dt;
    String classification;
    OffsetDateTime deleted_dt;

}
