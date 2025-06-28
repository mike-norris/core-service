package com.openrangelabs.services.signing.modelNew;

import lombok.Data;

@Data
public class Field {
    String field_name;
    String prefilled_text;

    public Field(String field_name, String prefilled_text) {
        this.field_name = field_name;
        this.prefilled_text = prefilled_text;
    }
}
