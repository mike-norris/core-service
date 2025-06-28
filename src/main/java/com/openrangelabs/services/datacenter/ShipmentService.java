package com.openrangelabs.services.datacenter;

import com.openrangelabs.middleware.config.WebClientConfig;
import com.openrangelabs.services.datacenter.model.bloxops.dao.shipments.AddUserRequest;
import com.openrangelabs.services.datacenter.model.bloxops.dao.shipments.AddUserResponse;
import com.openrangelabs.services.datacenter.model.bloxops.dao.shipments.ShipmentsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class ShipmentService {
    private final WebClient webClient;

    @Value("${tomcatDatacenterURL}")
    public static String tomcatDatacenterURL;
    public static String acceptHeader = "Accept";
    public static String acceptValue = "application/json";

    ShipmentService(){
        this.webClient = WebClientConfig.build(tomcatDatacenterURL);
    }

    public ShipmentsResponse getShipmentsForDelivery(String deliveryId) {
        log.info("Attempting to get shipments for delivery id "+ deliveryId);
        ParameterizedTypeReference<ShipmentsResponse> responseType = new ParameterizedTypeReference<ShipmentsResponse>() {};
        try{

            ResponseEntity<ShipmentsResponse> responseString = this.webClient.get()
                    .uri(tomcatDatacenterURL + "/shipments/delivery/" + deliveryId)
                    .header(HttpHeaders.ACCEPT, String.valueOf(MediaType.APPLICATION_JSON))
                    .retrieve()
                    .toEntity(responseType)
                    .block();
                    //.exchange(url, HttpMethod.GET, request, ShipmentsResponse.class);

            return responseString.getBody();
        }catch(Exception e){
            log.error(e.getMessage());
        }
        return new ShipmentsResponse();
    }

    @Deprecated
    public AddUserResponse addUserToShipmentList(AddUserRequest addUserRequest) {
        ParameterizedTypeReference<AddUserResponse> responseType = new ParameterizedTypeReference<AddUserResponse>() {};
        try{
            log.info("Attempting to add user to envoy "+ addUserRequest.getEmail());
            HttpHeaders header = new HttpHeaders();
            header.add(acceptHeader,acceptValue);
            HttpEntity<String> request = new HttpEntity<>(header);
            String url = tomcatDatacenterURL+"/shipments/user/add";
            // TODO: I don't think this works correctly
            ResponseEntity<AddUserResponse> responseString = this.webClient.post()
                    .uri(url)
                    .header(HttpHeaders.ACCEPT, String.valueOf(MediaType.APPLICATION_JSON))
                    .retrieve()
                    .toEntity(responseType)
                    .block();
            return responseString.getBody();
        }catch(Exception e){
            log.info("Error adding user "+ addUserRequest.getEmail());
        }
        return new AddUserResponse();
    }


}
