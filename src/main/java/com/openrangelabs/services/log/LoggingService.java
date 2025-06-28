package com.openrangelabs.services.log;

import com.openrangelabs.services.log.dao.LoggingDAO;
import com.openrangelabs.services.log.model.LogRecord;
import com.openrangelabs.services.tools.Commons;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LoggingService {

    private final RabbitTemplate rabbitTemplate;
    LoggingDAO loggingDAO;

    @Autowired
    public LoggingService(RabbitTemplate rabbitTemplate , LoggingDAO loggingDAO) {
        this.rabbitTemplate = rabbitTemplate;
        this.loggingDAO = loggingDAO;
    }


    @RabbitListener(queues = {Commons.USER_LOGS_QUEUE}, containerFactory = "ListenerContainerFactory")
    public void addUserLog(LogRecord record) {
        log.info("Log record received for user logs.");
        try{
            int success = loggingDAO.addUserLog(record);
            if(success != 1){
                rabbitTemplate.convertAndSend(Commons.LOGGING_DLX_EXCHANGE, "logs-user-dlq", record);
            }
        }catch(Exception e){
            log.error("Error adding user log record to the database.");
            log.error(e.getMessage());
            rabbitTemplate.convertAndSend(Commons.LOGGING_DLX_EXCHANGE, "logs-user-dlq", record);
        }
    }

    @RabbitListener(queues = {Commons.SYSTEM_LOGS_QUEUE}, containerFactory = "ListenerContainerFactory")
    public void addSystemLog(LogRecord record) {
        log.info("Log record received for system logs.");
        try{
            int success = loggingDAO.addSystemLog(record);
            if(success != 1){
                rabbitTemplate.convertAndSend(Commons.LOGGING_DLX_EXCHANGE, "logs-system-dlq", record);
            }
        }catch(Exception e){
            log.error("Error adding system log record to the database.");
            log.error(e.getMessage());
            rabbitTemplate.convertAndSend(Commons.LOGGING_DLX_EXCHANGE, "logs-system-dlq", record);
        }
    }


}
