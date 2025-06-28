package com.openrangelabs.services.authenticate.permission;
import com.openrangelabs.middleware.config.WebClientConfig;
import com.openrangelabs.services.authenticate.permission.model.OpenFga.CheckResponse;
import com.openrangelabs.services.authenticate.permission.model.OpenFga.FGAPermissionRequest;
import com.openrangelabs.services.authenticate.permission.model.OpenFga.ListResponse;
import com.openrangelabs.services.authenticate.permission.model.OpenFga.TupleKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OpenFgaAPIService {

    private final WebClient webClient;

    @Value("${openfga.baseurl}")
    String baseUrl;

    @Value("${openfga.storeId}")
    String storeId;
    String ACCEPT_HEADER = "application/json";

    public OpenFgaAPIService() {
        this.webClient = WebClientConfig.build(baseUrl);
    }

    public boolean writeAuthorizationTuple(FGAPermissionRequest fgaPermissionRequest) {
        boolean success = false;
        log.info("Writing permission (tuple)");
        Map<String, Object> body = Map.of(
                "writes", Map.of(
                        "tuple_keys", List.of(
                                Map.of(
                                        "user", fgaPermissionRequest.getUser(),
                                        "relation", fgaPermissionRequest.getRelation(),
                                        "object", fgaPermissionRequest.getObject()
                                )
                        )
                )
        );

        try {
            ResponseEntity<String> response = webClient.post()
                    .uri(baseUrl + "/stores/" + storeId + "/write")
                    .header(HttpHeaders.ACCEPT, ACCEPT_HEADER)
                    .header(HttpHeaders.CONTENT_TYPE, ACCEPT_HEADER)
                    .bodyValue(body)
                    .retrieve()
                    .toEntity(String.class)
                    .block();
            if(response.getStatusCode().is2xxSuccessful()){
                success = true;
            }
        }catch(Exception e){
            log.error("error adding tuple");
            e.printStackTrace();
        }
        return success;
    }


    public boolean deleteAuthorizationTuple(FGAPermissionRequest fgaPermissionRequest) {
        boolean success = false;
        log.info("Removing permission (tuple)");
        Map<String, Object> body = Map.of(
                "deletes", Map.of(
                        "tuple_keys", List.of(
                                Map.of(
                                        "user", fgaPermissionRequest.getUser(),
                                        "relation", fgaPermissionRequest.getRelation(),
                                        "object", fgaPermissionRequest.getObject()
                                )
                        )
                )
        );

        try {
            ResponseEntity<String> response = webClient.post()
                    .uri(baseUrl + "/stores/" + storeId + "/write")
                    .header(HttpHeaders.ACCEPT, ACCEPT_HEADER)
                    .header(HttpHeaders.CONTENT_TYPE, ACCEPT_HEADER)
                    .bodyValue(body)
                    .retrieve()
                    .toEntity(String.class)
                    .block();

            if(response.getStatusCode().is2xxSuccessful()){
                success = true;
            }
        }catch(Exception e){
            log.error("error adding tuple");
            e.printStackTrace();
        }
        return success;
    }

    public boolean checkPermission(FGAPermissionRequest fgaPermissionRequest) {
        boolean success = false;
        Map<String, Object> body = Map.of(
                "tuple_key", Map.of(
                        "user", fgaPermissionRequest.getUser(),
                        "relation", fgaPermissionRequest.getRelation(),
                        "object", fgaPermissionRequest.getObject()
                )
        );

        try {
            ResponseEntity<CheckResponse> response = webClient.post()
                    .uri(baseUrl + "/stores/" + storeId + "/check")
                    .header(HttpHeaders.ACCEPT, ACCEPT_HEADER)
                    .header(HttpHeaders.CONTENT_TYPE, ACCEPT_HEADER)
                    .bodyValue(body)
                    .retrieve()
                    .toEntity(CheckResponse.class)
                    .block();
            CheckResponse checkResponse = response.getBody();
            if(response.getStatusCode().is2xxSuccessful() && checkResponse.isAllowed()){
                success = true;
            }
        }catch(Exception e){
            log.error("error removing tuple");
            e.printStackTrace();
        }
        return success;
    }

    public List<TupleKey> listUserPermissions(FGAPermissionRequest fgaPermissionRequest) {
        List<TupleKey> permissions = new ArrayList<>();
        Map<String, Object> body = Map.of(
                "tuple_key", Map.of(
                        "user", fgaPermissionRequest.getUser(),
                        "relation", fgaPermissionRequest.getRelation(),
                        "object", fgaPermissionRequest.getObject()
                )
        );

        try {
            ResponseEntity<ListResponse> response = webClient.post()
                    .uri(baseUrl + "/stores/" + storeId + "/read")
                    .header(HttpHeaders.ACCEPT, ACCEPT_HEADER)
                    .header(HttpHeaders.CONTENT_TYPE, ACCEPT_HEADER)
                    .bodyValue(body)
                    .retrieve()
                    .toEntity(ListResponse.class)
                    .block();
            if(response.getStatusCode().is2xxSuccessful()){
                permissions  = response.getBody().getTuples();
            }
        }catch(Exception e){
            log.error("error getting list of permissions");
            e.printStackTrace();
        }
        return permissions;
    }
}
