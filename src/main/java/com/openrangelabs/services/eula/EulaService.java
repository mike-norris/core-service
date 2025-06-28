package com.openrangelabs.services.eula;

import com.openrangelabs.services.eula.dao.EulaBloxopsDAO;
import com.openrangelabs.services.eula.entity.Eula;
import com.openrangelabs.services.eula.model.EulaStatusUpdateRequest;
import com.openrangelabs.services.eula.model.EulaStatusUpdateResponse;
import com.openrangelabs.services.eula.model.EulaUserDetails;
import com.openrangelabs.services.eula.model.EulaVersionResponse;
import com.openrangelabs.services.log.model.LogRecord;
import com.openrangelabs.services.message.ErrorMessageService;
import com.openrangelabs.services.tools.Commons;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class EulaService {
    EulaBloxopsDAO eulaBloxopsDAO;
    ErrorMessageService errorMessageService;
    RabbitTemplate rabbitTemplate;

    @Value("${latestEulaVersion}")
    String latestEulaVersion;


    @Autowired
    public EulaService(EulaBloxopsDAO eulaBloxopsDAO, ErrorMessageService errorMessageService , RabbitTemplate rabbitTemplate) {
        this.errorMessageService = errorMessageService;
        this.eulaBloxopsDAO = eulaBloxopsDAO;
        this.rabbitTemplate = rabbitTemplate;
    }

    public EulaUserDetails getEulaUserDetails(Integer userId) {
        try {
            log.info("Getting EULA details for user id :" + userId);
            List<EulaUserDetails> eulaUserDetailsResponseList = eulaBloxopsDAO.getEulaUserDetails(userId, latestEulaVersion);
            return eulaUserDetailsResponseList.get(0);
        }catch(Exception e){
            log.error("Error Getting EULA details for user id :" + userId);
            return null;
        }

    }

    public EulaStatusUpdateResponse statusUpdateRequest(EulaStatusUpdateRequest eulaStatusUpdateRequest) {
        try {
            EulaUserDetails eulaUserDetails = getEulaUserDetails(Integer.parseInt(eulaStatusUpdateRequest.getUser_id()));
            int updated = 0;
            if (eulaUserDetails != null && eulaUserDetails.getVersion().contains(eulaStatusUpdateRequest.getVersion())) {
                log.info("Updating EULA status.");
                updated = eulaBloxopsDAO.updateStatus(eulaStatusUpdateRequest.getVersion(), eulaStatusUpdateRequest.getStatus(), eulaStatusUpdateRequest.getBrowserInfo(), Integer.parseInt(eulaStatusUpdateRequest.getUser_id()));
            } else {
                log.info("Adding EULA record to DB.");
                updated = eulaBloxopsDAO.saveStatus(eulaStatusUpdateRequest.getVersion(), eulaStatusUpdateRequest.getStatus(), eulaStatusUpdateRequest.getBrowserInfo(), Integer.parseInt(eulaStatusUpdateRequest.getUser_id()), eulaStatusUpdateRequest.getEula_id());
            }
            log.info("Sending log record for Eula Update");
            LogRecord logRecord = new LogRecord(Integer.parseInt(eulaStatusUpdateRequest.getUser_id()) , 0, "Eula Status Updated " + eulaStatusUpdateRequest.getStatus(), "Eula Update");
            rabbitTemplate.convertAndSend(Commons.LOGGING_EXCHANGE, "logs-system", logRecord);
            rabbitTemplate.convertAndSend(Commons.LOGGING_EXCHANGE, "logs-user", logRecord);
            if (updated == 1) {
                log.info("EULA Status Updated.");
                return new EulaStatusUpdateResponse(true, null);
            } else {
                return new EulaStatusUpdateResponse(false, "Error saving eula status.");
            }
        }catch(Exception e){
            log.error("Error saving EULA status.");
            return new EulaStatusUpdateResponse(false, "Error saving eula status.");
        }
    }

    public EulaVersionResponse getLatestEULAVersion() {
        try{
            log.info("Retrieving latest version of EULA.");
            Eula eula = eulaBloxopsDAO.getEulaByVersion(latestEulaVersion);
            return new EulaVersionResponse(latestEulaVersion ,eula.getUpdatedDateTime(), null);
        }catch(Exception e){
            log.error("Error Retrieving latest version of EULA.");
            return new EulaVersionResponse("Error getting latest version.");
        }
    }

    public List<Eula> getEulas() {
        try{
            log.info("Retrieving all EULA's in DB.");
            return eulaBloxopsDAO.getAllEulas();
        }catch(Exception e){
            log.error("Error Retrieving all EULA's in DB.");
            return new ArrayList<>();
        }
    }
}
