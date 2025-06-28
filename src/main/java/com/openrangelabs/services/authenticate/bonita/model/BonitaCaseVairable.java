package com.openrangelabs.services.authenticate.bonita.model;

import lombok.Data;

@Data
public class BonitaCaseVairable {
    String name;
    String value;

    public BonitaCaseVairable(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public BonitaCaseVairable() {
    }
}
