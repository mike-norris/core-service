package com.openrangelabs.services.message;

import com.openrangelabs.services.message.model.messaging.EmailRequest;
import com.openrangelabs.services.message.sendGrid.SendGridAPIService;
import com.openrangelabs.services.signing.dao.SigningBloxopsDAO;
import com.openrangelabs.services.ticket.model.TicketUpdateRequest;
import com.openrangelabs.services.tools.Commons;
import com.openrangelabs.services.user.model.Communication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessagingService {

    private static final Logger log = LoggerFactory.getLogger(MessagingService.class);
    private final RabbitTemplate rabbitTemplate;
    SendGridAPIService sendGridAPIService;
    SigningBloxopsDAO signingBloxopsDAO;


    @Autowired
    public MessagingService(SigningBloxopsDAO signingBloxopsDAO , RabbitTemplate rabbitTemplate , SendGridAPIService sendGridAPIService) {
        this.signingBloxopsDAO = signingBloxopsDAO;
        this.sendGridAPIService = sendGridAPIService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = {Commons.EMAIL_QUEUE}, containerFactory = "ListenerContainerFactory")
    public void sendEmail(EmailRequest emailRequest) {
        log.info("Email request received.");
        try{
            Communication communication = signingBloxopsDAO.getCommunication(emailRequest.getTemplateName());
            sendGridAPIService.sendCommunicationEmail(emailRequest , communication.getChannel());

        }catch(Exception e){
            log.error(e.getMessage());
            rabbitTemplate.convertAndSend(Commons.MESSAGING_EXCHANGE_DLX, "email-dlq", emailRequest);
        }
    }

    public void sendTicketUpdate(String comment , String ticketId) {
        log.info("Ticket Update Request Received.");
        try{
            TicketUpdateRequest ticketUpdateRequest = new TicketUpdateRequest();
            ticketUpdateRequest.setJiraId(ticketId);
            ticketUpdateRequest.setComment(comment);
            rabbitTemplate.convertAndSend(Commons.SUPPORT_EXCHANGE, "support-update",ticketUpdateRequest);
        }catch(Exception e){
            log.error(e.getMessage());
            rabbitTemplate.convertAndSend(Commons.SUPPORT_DLX_EXCHANGE, "support-update-dlq", comment + " " + ticketId);
        }
    }

}
