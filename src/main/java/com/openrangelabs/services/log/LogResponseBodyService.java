package com.openrangelabs.services.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Slf4j
@Service
public class LogResponseBodyService {

    ObjectMapper mapper;

    public LogResponseBodyService() {
        this.mapper = new ObjectMapper();
    }

    public Object logResponse(Object object, String verb, String path, String pid){
        try {
            String responseBody = mapper.writeValueAsString(object);
            log.info(OffsetDateTime.now().toString()+" | "+"Response"+" | "+verb+" | "+path+" | "+pid+" | "+responseBody);
            return object;
        } catch (JsonProcessingException e) {
            log.warn("unable to log response:"+object+" | "+"ResponseError"+" | "+verb+" | "+path+" | "+pid);
            log.error(e.getMessage());
            return object;
        }
    }
}
