package com.openrangelabs.services.billing;

import com.openrangelabs.middleware.config.WebClientConfig;
import com.openrangelabs.services.billing.model.IntacctCustomer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
public class BillingAPIService {

    private final WebClient webClient;

    @Value("${servicesURL}")
    String servicesURL;

    List<?> responseListType;

    @Autowired
    BillingAPIService(List<?> responseListType){
        this.responseListType = responseListType;
        this.webClient = WebClientConfig.build(servicesURL);
    }

    public List<IntacctCustomer> getCustomers() {
        ParameterizedTypeReference<List<IntacctCustomer>> responseType = new ParameterizedTypeReference<List<IntacctCustomer>>() {};
        List <IntacctCustomer> intacctCustomers = new ArrayList<>();
        try{
            ResponseEntity<List<IntacctCustomer>> response = this.webClient.get()
                    .uri(servicesURL + "/billing/admin/accounts?key=16126fa811abc33241920a9d0ba1facc")
                    .header(HttpHeaders.ACCEPT, String.valueOf(MediaType.APPLICATION_JSON))
                    .header(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_JSON))
                    .retrieve()
                    .toEntity(responseType)
                    .block();
            try {
                intacctCustomers = response.getBody();

            } catch (Exception e1) {
                log.error(e1.getMessage());
            }
            return intacctCustomers;
        } catch(Exception e) {
            log.error(e.getMessage());
            return intacctCustomers;
        }

    }
}
