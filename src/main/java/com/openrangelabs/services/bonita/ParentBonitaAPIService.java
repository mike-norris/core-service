package com.openrangelabs.services.bonita;

import com.openrangelabs.middleware.config.WebClientConfig;
import com.openrangelabs.services.authenticate.model.SessionInfo;
import com.openrangelabs.services.bonita.model.BdmActiveMessageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.openrangelabs.services.authenticate.bonita.BonitaAuthenticateAPIService.BONITA_API_TOKEN_NAME;
import static com.openrangelabs.services.authenticate.bonita.BonitaAuthenticateAPIService.SESSION_ID_NAME;

@Service
public class ParentBonitaAPIService {
    @Value("${bonitaURL}")
    private static String apiUrl;

    private static String cookieHeader = "Cookie";

    private final WebClient webClient;

    public ParentBonitaAPIService() {
        this.webClient = WebClientConfig.build(apiUrl);
    }

    public List<BdmActiveMessageResponse> getActiveMessages(SessionInfo sessionInfo) {
        OffsetDateTime currentDt = OffsetDateTime.now();
        String currentDtSubStr = currentDt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String formattedCurrentDt = currentDtSubStr.substring(0,currentDtSubStr.indexOf("."))+"Z";
        ParameterizedTypeReference<List<BdmActiveMessageResponse>> responseType = new ParameterizedTypeReference<List<BdmActiveMessageResponse>>() {};
        try {
            ResponseEntity<List<BdmActiveMessageResponse>> response = this.webClient.get()
                    .uri(apiUrl + "API/bdm/businessData/com.company.model.Alerts?&p=0&c=100&q=findActive&f=current_dt=" + formattedCurrentDt)
                    .header(cookieHeader , SESSION_ID_NAME + "=" + sessionInfo.getSessionId())
                    .header(cookieHeader , BONITA_API_TOKEN_NAME + "=" + sessionInfo.getSessionToken())
                    .header(BONITA_API_TOKEN_NAME, sessionInfo.getSessionToken())
                    .retrieve()
                    .toEntity(responseType)
                    .block();
            return response.getBody();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

}
