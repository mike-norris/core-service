package com.openrangelabs.services.authenticate.permission.bonita;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


@Component
public class BonitaPermissionAPIService {
    private final WebClient webClient;
    private static final ObjectMapper mapper = new ObjectMapper();

    @Value("${bonitaURL}")
    public String API_URL;

    private static final String BONITA_API_TOKEN_NAME = "X-Bonita-API-Token";
    private static final String SESSION_ID_NAME = "JSESSIONID";

    public BonitaPermissionAPIService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(API_URL).build();
    }
}


