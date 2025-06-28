package com.openrangelabs.services.user.bonita.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openrangelabs.services.user.bonita.model.BonitaUserDetails;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class BonitaUserDetailsMapper {
    ObjectMapper mapper;
    public BonitaUserDetailsMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }
    public BonitaUserDetails mapUser(String responseString) {
        BonitaUserDetails response = new BonitaUserDetails();
        String responseError;
        try {
            response = mapper.readValue(responseString, BonitaUserDetails.class);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return response;
    }
}
