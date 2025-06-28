package com.openrangelabs.services.authenticate.bonita;

import com.openrangelabs.services.authenticate.bonita.model.BonitaForgotPasswordRequest;
import com.openrangelabs.services.authenticate.bonita.model.BonitaCaseResponse;
import com.openrangelabs.services.ticket.model.ProcessResponse;
import com.openrangelabs.services.user.bonita.model.BonitaUserContactDetails;
import com.openrangelabs.services.user.bonita.model.BonitaUserCustomDetail;
import com.openrangelabs.services.user.bonita.model.BonitaUserDetails;
import com.openrangelabs.services.authenticate.model.SessionInfo;
import com.openrangelabs.services.user.model.UserIdentificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Component
public class BonitaAuthenticateAPIService {
    private final WebClient webClient;

    @Value("${bonitaURL}")
    public String apiUrl;
    public static final String LOGIN_URL = "loginservice";
    public static final String AUTHENTICATE_URL = "API/system/session/unusedid";
    public static final String BONITA_API_TOKEN_NAME = "X-Bonita-API-Token";
    public static final String SESSION_ID_NAME = "JSESSIONID";
    String cookieHeader = "Cookie";

    public BonitaAuthenticateAPIService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(apiUrl).build();
    }

    public SessionInfo loginUser(String userName, String password) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("username", userName);
        formData.add("password", password);
        ResponseEntity<String> responseString = webClient.post()
                .uri(apiUrl + LOGIN_URL)
                .bodyValue(formData)
                .retrieve()
                .toEntity(String.class)
                .block();
        return new SessionInfo(extractToken(responseString.toString()),extractSessionId(responseString.toString()));
    }

    public void logout(SessionInfo sessionInfo) {
        try {
            ResponseEntity<String>  responseString = webClient.get()
                    .uri(apiUrl + "/logoutservice?redirect=false")
                    .header(cookieHeader, SESSION_ID_NAME + "=" + sessionInfo.getSessionId())
                    .header(cookieHeader, BONITA_API_TOKEN_NAME + "=" + sessionInfo.getSessionToken())
                    .retrieve()
                    .toEntity(String.class)
                    .block();
            sessionInfo.setSessionToken(null);
            sessionInfo.setSessionId(null);
            log.info("Successful logout " + responseString);
        } catch (Exception e){
            log.error(e.getMessage());
        }
    }

    public UserIdentificationResponse getUserIdentification (SessionInfo sessionInfo) {
        UserIdentificationResponse userIdentificationResponse = new UserIdentificationResponse();
        try {
            userIdentificationResponse = getUserIdentificationResponse(sessionInfo).getBody();
        } catch (Exception e) {
            if(e.getMessage().contains("401")) {
                userIdentificationResponse.setError("You are not logged in");
            }
        }
        return userIdentificationResponse;
    }

    public ResponseEntity<UserIdentificationResponse>  getUserIdentificationResponse (SessionInfo sessionInfo) {
        return webClient.get()
                .uri(apiUrl + AUTHENTICATE_URL)
                .header(cookieHeader, SESSION_ID_NAME + "=" + sessionInfo.getSessionId())
                .header(cookieHeader, BONITA_API_TOKEN_NAME + "=" + sessionInfo.getSessionToken())
                .retrieve()
                .toEntity(UserIdentificationResponse.class)
                .block();
//        request, UserIdentificationResponse.class);
    }

    public BonitaUserDetails getUserDetails(String userId, SessionInfo sessionInfo) {
        return webClient.get()
                .uri(apiUrl + "API/identity/user/"+userId)
                .header(cookieHeader, SESSION_ID_NAME + "=" + sessionInfo.getSessionId())
                .header(cookieHeader, BONITA_API_TOKEN_NAME + "=" + sessionInfo.getSessionToken())
                .header(BONITA_API_TOKEN_NAME,sessionInfo.getSessionToken())
                .retrieve()
                .toEntity(BonitaUserDetails.class)
                .block()
                .getBody();
    }

    public List<BonitaUserCustomDetail> getCustomUserDetails(String userId, SessionInfo sessionInfo) {
        ParameterizedTypeReference<List<BonitaUserCustomDetail>> responseType = new ParameterizedTypeReference<List<BonitaUserCustomDetail>>() {};
        return webClient.get()
                .uri(apiUrl + "API/customuserinfo/user?f=userId="+userId)
                .header(cookieHeader, SESSION_ID_NAME + "=" + sessionInfo.getSessionId())
                .header(cookieHeader, BONITA_API_TOKEN_NAME + "=" + sessionInfo.getSessionToken())
                .header(BONITA_API_TOKEN_NAME,sessionInfo.getSessionToken())
                .retrieve()
                .toEntity(responseType)
                .block()
                .getBody();
    }

    public BonitaUserContactDetails getUserProfessionalDetails(String userId, SessionInfo sessionInfo) {
        return webClient.get()
                .uri(apiUrl + "API/identity/professionalcontactdata/"+userId)
                .header(cookieHeader, SESSION_ID_NAME + "=" + sessionInfo.getSessionId())
                .header(cookieHeader, BONITA_API_TOKEN_NAME + "=" + sessionInfo.getSessionToken())
                .header(BONITA_API_TOKEN_NAME, sessionInfo.getSessionToken())
                .retrieve()
                .toEntity(BonitaUserContactDetails.class)
                .block()
                .getBody();
    }

    public BonitaCaseResponse requestForgotPasswordEmail(BonitaForgotPasswordRequest requestBody, String processId, SessionInfo sessionInfo) {
        return webClient.post()
                .uri(apiUrl + "API/bpm/process/"+processId+"/instantiation")
                .header(cookieHeader, SESSION_ID_NAME + "=" + sessionInfo.getSessionId())
                .header(cookieHeader, BONITA_API_TOKEN_NAME + "=" + sessionInfo.getSessionToken())
                .header(BONITA_API_TOKEN_NAME, sessionInfo.getSessionToken())
                .header(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_JSON))
                .bodyValue(requestBody)
                .retrieve()
                .toEntity(BonitaCaseResponse.class)
                .block()
                .getBody();
    }

    public BonitaUserDetails findUserByUserName(String userName, SessionInfo sessionInfo) {
        ParameterizedTypeReference<List<BonitaUserDetails>> responseType = new ParameterizedTypeReference<List<BonitaUserDetails>>() {};

        List<BonitaUserDetails> response = webClient.get()
                .uri(apiUrl + "API/identity/user?f=userName="+userName)
                .header(cookieHeader, SESSION_ID_NAME + "=" + sessionInfo.getSessionId())
                .header(cookieHeader, BONITA_API_TOKEN_NAME + "=" + sessionInfo.getSessionToken())
                .header(BONITA_API_TOKEN_NAME, sessionInfo.getSessionToken())
                .retrieve()
                .toEntity(responseType)
                .block()
                .getBody();

        if( response != null  ){
            return response.get(0);
        }
        return new BonitaUserDetails();
    }

    public ProcessResponse getNewUserProcess(SessionInfo sessionInfo) throws Exception {
        ParameterizedTypeReference<List<ProcessResponse>> responseType = new ParameterizedTypeReference<List<ProcessResponse>>() {};

        List<ProcessResponse> response = webClient.get()
                .uri(apiUrl + "API/bpm/process?f=name=UserResetLogin&p0&c=1&f=activationState=ENABLED&o=version desc")
                .header(cookieHeader, SESSION_ID_NAME + "=" + sessionInfo.getSessionId())
                .header(cookieHeader, BONITA_API_TOKEN_NAME + "=" + sessionInfo.getSessionToken())
                .header(BONITA_API_TOKEN_NAME, sessionInfo.getSessionToken())
                .retrieve()
                .toEntity(responseType)
                .block()
                .getBody();

        if(response != null) {
            return response.get(0);
        }
        return new ProcessResponse();
    }

    String extractToken(String response) {
        String startString =  response.substring(response.lastIndexOf(BONITA_API_TOKEN_NAME) + BONITA_API_TOKEN_NAME.length()+1);
        int endIndex = startString.indexOf("; ");
        return startString.substring(0,endIndex);
    }

    String extractSessionId(String response) {

        String startString =  response.substring(response.lastIndexOf(SESSION_ID_NAME) + SESSION_ID_NAME.length()+1);
        int endIndex = startString.indexOf("; ");
        return startString.substring(0,endIndex);
    }

    public String extractGUID(List<BonitaUserCustomDetail> bonitaUserCustomDetails) throws Exception {
        for (BonitaUserCustomDetail detail : bonitaUserCustomDetails) {
            if("GUID".equals(detail.getDefinitionId().getName())) {
                return detail.getValue();
            }
        }
        throw new Exception("Unable to find Guid from Bonita Custom Details");
    }

}


