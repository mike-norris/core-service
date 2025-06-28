package com.openrangelabs.services.authenticate.model;

import lombok.Data;

@Data
public class Cookies {
    String tokenCookie;
    String sessionCookie;
}
