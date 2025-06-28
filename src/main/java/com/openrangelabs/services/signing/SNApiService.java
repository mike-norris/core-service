package com.openrangelabs.services.signing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;

@Slf4j
@Service
public class SNApiService {
    @Value("${signNowApiUrl}")
    private String apiUrl;

    @Value("${signNowClientId}")
    private String clientId;

    @Value("${signNowClientSecret}")
    private String clientSecret;

    @PostConstruct
    private void initClientBuilder()
    {
        SNClientBuilder.get(apiUrl, clientId, clientSecret);
    }
}
