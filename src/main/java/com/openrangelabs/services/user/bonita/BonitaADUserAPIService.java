package com.openrangelabs.services.user.bonita;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.openrangelabs.middleware.config.WebClientConfig;
import com.openrangelabs.services.authenticate.model.SessionInfo;
import com.openrangelabs.services.user.bonita.model.*;
import com.openrangelabs.services.user.bonita.model.BonitaADAddUserRequest;
import com.openrangelabs.services.user.bonita.model.BonitaADAddUserResponse;
import com.openrangelabs.services.user.bonita.model.BonitaADUserCheck;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import static com.openrangelabs.services.authenticate.bonita.BonitaAuthenticateAPIService.BONITA_API_TOKEN_NAME;
import static com.openrangelabs.services.authenticate.bonita.BonitaAuthenticateAPIService.SESSION_ID_NAME;

@Service
public class BonitaADUserAPIService {
    private final WebClient webClient;

    @Value("${bonitaURL}")
    public String API_URL;

    String COOKIE ="Cookie";

    public BonitaADUserAPIService() {
        this.webClient = WebClientConfig.build(API_URL);
    }

    public BonitaADUserCheck checkIfEmailAndNameIsUsed(String email, String firstName, String lastName, SessionInfo sessionInfo) {
        ParameterizedTypeReference<BonitaADUserCheck> responseType = new ParameterizedTypeReference<BonitaADUserCheck>() {};

        ResponseEntity<BonitaADUserCheck> response = webClient.get()
                .uri(API_URL + "API/extension/ldap/exists?email_address=" + email + "&lastname=" + lastName + "&firstname=" + firstName)
                .header(COOKIE, SESSION_ID_NAME + "=" + sessionInfo.getSessionId())
                .header(COOKIE, BONITA_API_TOKEN_NAME + "=" + sessionInfo.getSessionToken())
                .header(BONITA_API_TOKEN_NAME, sessionInfo.getSessionToken())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .toEntity(responseType)
                .block();
        return response.getBody();
    }

    public BonitaADAddUserResponse addUser(BonitaADAddUserRequest bonitaADAddUserRequest, SessionInfo sessionInfo) throws JsonProcessingException {
        ParameterizedTypeReference<BonitaADAddUserResponse> responseType = new ParameterizedTypeReference<BonitaADAddUserResponse>() {};

        ResponseEntity<BonitaADAddUserResponse> response = webClient.post()
                .uri(API_URL + "API/extension/ldap/portalUser")
                .header(COOKIE, SESSION_ID_NAME + "=" + sessionInfo.getSessionId())
                .header(COOKIE, BONITA_API_TOKEN_NAME + "=" + sessionInfo.getSessionToken())
                .header(BONITA_API_TOKEN_NAME, sessionInfo.getSessionToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(bonitaADAddUserRequest)
                .retrieve()
                .toEntity(responseType)
                .block();
        return response.getBody();
    }
}
