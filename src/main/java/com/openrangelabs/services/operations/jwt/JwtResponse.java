package com.openrangelabs.services.operations.jwt;

import lombok.Data;

import java.io.Serializable;
@Data
public class JwtResponse implements Serializable
{

    private final String jwttoken;
    String error;

    public JwtResponse(String jwttoken) {
        this.jwttoken = jwttoken;
    }

    public String getToken() {
        return this.jwttoken;
    }
}