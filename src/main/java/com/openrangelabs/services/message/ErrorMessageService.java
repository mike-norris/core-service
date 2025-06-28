package com.openrangelabs.services.message;

import com.openrangelabs.middleware.config.WebClientConfig;
import com.openrangelabs.services.message.bloxops.dao.ErrorMessageDAO;
import com.openrangelabs.services.message.model.slack.SlackAlertResponse;
import com.openrangelabs.services.message.model.slack.SlackAttachment;
import com.openrangelabs.services.message.model.slack.SlackErrorAlertRequest;
import com.openrangelabs.services.message.model.slack.SlackField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ErrorMessageService {
    private final WebClient webClient;

    ErrorMessageDAO errorMessageDAO;

    @Value("${slackToken}")
    String SLACK_API_TOKEN;

    @Value("${slackURL}")
    String SLACK_API_URL;

    String AuthErrorMessage = "Could not Authenticate.";

    @Autowired
    public ErrorMessageService(ErrorMessageDAO errorMessageDAO) {
        this.errorMessageDAO = errorMessageDAO;
        this.webClient = WebClientConfig.build(SLACK_API_URL);
    }

    public String getBonitaTokenErrorMessage(int entry) {
        try {
            String message = errorMessageDAO.getBonitaTokenErrorMessage(entry);
            if ("".equals(message) || message == null) {
                message = AuthErrorMessage;
            }
            return message;
        } catch (Exception e) {
            log.error("ERRORMESSAGESERVICE: Unable to retrieve Bonita Token error message " + e.getMessage());
            return AuthErrorMessage;
        }
    }

    public String getAuthenicateErrorMessage(int entry) {
        try {
            String message = errorMessageDAO.getAuthenicateErrorMessage(entry);
            if ("".equals(message) || message == null) {
                message = AuthErrorMessage;
            }
            return message;
        } catch (Exception e) {
            log.error("ERRORMESSAGESERVICE: Unable to retrieve Authenticate error message " +e.getMessage());
            return AuthErrorMessage;
        }
    }

    public String getPasswordErrorMessage(int entry) {
        try {
            String message = errorMessageDAO.getPasswordErrorMessage(entry);
            if ("".equals(message) || message == null) {
                message = AuthErrorMessage;
            }
            return message;
        } catch (Exception e) {
            log.error("ERRORMESSAGESERVICE: Unable to retrieve Password error message " +e.getMessage());
            return AuthErrorMessage;
        }
    }

    public String getInactiveOrgErrorMessage(int entry) {
        try {
            String message = errorMessageDAO.getInactiveOrgErrorMessage(entry);
            if ("".equals(message) || message == null) {
                message = AuthErrorMessage;
            }
            return message;
        } catch (Exception e) {
            log.error("ERRORMESSAGESERVICE: Unable to retrieve Organizations. Ensure org is active." + e.getMessage());
            return AuthErrorMessage;
        }
    }

    public String getInactiveUserErrorMessage(int entry) {
        try {
            String message = errorMessageDAO.getInactiveUserErrorMessage(entry);
            if ("".equals(message) || message == null) {
                message = AuthErrorMessage;
            }
            return message;
        } catch (Exception e) {
            log.error("ERRORMESSAGESERVICE: Unable to retrieve User. Ensure user is active." +e.getMessage());
            return AuthErrorMessage;
        }
    }

    public void sendSlackErrorMessage(String errorMessage ){

        try {
            SlackErrorAlertRequest requestBody = new SlackErrorAlertRequest();
            List<SlackField> slackFieldList = new ArrayList<>();
            List<SlackAttachment> slackAttachmentList = new ArrayList<>();
            SlackField slackField = new SlackField();
            slackField.setValue("HIGH");
            slackField.setTitle("PRIORITY");
            slackFieldList.add(slackField);
            SlackAttachment slackAttachment = new SlackAttachment("danger","","Error Found",errorMessage, slackFieldList);
            slackAttachmentList.add(slackAttachment);

            requestBody.setIcon_url("https://www.openrangelabs.com/wp-content/uploads/openrangelabs-logo.png");
            requestBody.setUsername("myError Reporter");
            requestBody.setChannel("openrangelabs-alerts");
            requestBody.setAttachments(slackAttachmentList);
            log.info("Attempting to send slack error alert");

            ParameterizedTypeReference<SlackAlertResponse> responseType = new ParameterizedTypeReference<SlackAlertResponse>() {};
            ResponseEntity<SlackAlertResponse> responseString = webClient.post()
                    .uri(SLACK_API_URL)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + SLACK_API_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .toEntity(responseType)
                    .block();

            if(responseString.getBody() != null && null != responseString.getBody() ) {
                SlackAlertResponse slackAlertResponse = responseString.getBody();
                if(slackAlertResponse != null) {
                    if(slackAlertResponse.isOk()){
                        log.info("Returning delivery details for employee shipment reference id");
                    }
                }else{
                    log.error("Error Alert could not be sent to slack - error returned from slack" );
                }
            }else{
                log.error("Error Alert could not be sent to slack - error returned from slack" );
            }

        } catch (Exception e) {
            log.error("Error Alert could not be sent to slack" +e.getMessage());

        }
    }
}
