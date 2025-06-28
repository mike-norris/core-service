package com.openrangelabs.services.message.sendGrid;
import com.openrangelabs.services.message.model.messaging.EmailRequest;
import com.openrangelabs.services.signing.dao.SigningBloxopsDAO;
import com.openrangelabs.services.user.model.Communication;
import com.openrangelabs.services.user.model.UserCreate;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class SendGridAPIService {

    @Value("${sendGrid.apiKey}")
    public String apiKey;

    @Value("${sendGrid.senderEmail}")
    public String senderEmail;

    @Value("${app.environment")
    public String environment;

    SigningBloxopsDAO signingBloxopsDAO;

    @Autowired
    public SendGridAPIService(SigningBloxopsDAO signingBloxopsDAO) {

    this.signingBloxopsDAO = signingBloxopsDAO;

    }

    public  void sendWelcomeEmail(String email ,String templateId, UserCreate userCreate, String url) throws IOException {
        Personalization personalization = new Personalization();

        Email from = new Email(senderEmail);
        Email to = new Email(email);
        personalization.addTo(to);
        personalization.addDynamicTemplateData("first_name", userCreate.getFirstName());
        personalization.addDynamicTemplateData("last_name", userCreate.getLastName());
        personalization.addDynamicTemplateData("uri_link", url);
        Mail mail = new Mail();

        mail.addPersonalization(personalization);
        mail.setFrom(from);
        mail.setTemplateId(templateId);
        SendGrid sg = new SendGrid(apiKey);

        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException ex) {
            throw ex;
        }
    }

    public void sendEmail(String toEmail , String sender , Personalization personalization , String communicationType) throws IOException {
        Communication communication = signingBloxopsDAO.getCommunication(communicationType);
        String fromEmail = sender == null ? senderEmail : sender;

        Email from = new Email(fromEmail);
        Email to = new Email(toEmail);
        personalization.addTo(to);

        Mail mail = new Mail();
        mail.addPersonalization(personalization);
        mail.setFrom(from);
        mail.setTemplateId(communication.getChannel());
        SendGrid sg = new SendGrid(apiKey);

        Request request = new Request();

        try {
            log.info("Sending welcome email.");
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException ex) {

            throw ex;
        }
    }

    public  void sendCommunicationEmail(EmailRequest emailRequest , String template) throws IOException {
        Personalization personalization = new Personalization();

        Email from = new Email(emailRequest.getFrom());
        Email to = new Email(emailRequest.getEmailTo());
        personalization.addTo(to);

        Mail mail = new Mail();
        personalization = setPersonalizations(emailRequest , personalization);
        mail.addPersonalization(personalization);
        mail.setFrom(from);
        mail.setTemplateId(template);

        SendGrid sg = new SendGrid(apiKey);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

        } catch (IOException ex) {
            throw ex;
        }
    }

    public Personalization setPersonalizations(EmailRequest emailRequest ,  Personalization personalization){
        String template = emailRequest.getTemplateName();
        personalization.addDynamicTemplateData("first_name", emailRequest.getFirstname());
        personalization.addDynamicTemplateData("last_name", emailRequest.getLastname());
        personalization.addDynamicTemplateData("ticket_id", emailRequest.getTicketID());
        if (environment.contains("dev")) {
            personalization.addDynamicTemplateData("env", "Lab - ");
        } else {
            personalization.addDynamicTemplateData("env", "");
        }

        switch(template) {
            case "ticket_new":
                personalization.addDynamicTemplateData("uri_link", emailRequest.getUrl());
                personalization.addDynamicTemplateData("submitted", emailRequest.getSubmitted());
                break;
            case "ticket_update":
                personalization.addDynamicTemplateData("uri_link", emailRequest.getUrl());
                personalization.addDynamicTemplateData("submitted", emailRequest.getSubmitted());
                personalization.addDynamicTemplateData("submitter", emailRequest.getSubmitter());
                personalization.addDynamicTemplateData("status", emailRequest.getStatus());
                personalization.addDynamicTemplateData("comments",emailRequest.getComments());
                break;
            case "ticket_waiting":
                personalization.addDynamicTemplateData("subject", emailRequest.getSubject());
                personalization.addDynamicTemplateData("body1",emailRequest.getBody1());
                personalization.addDynamicTemplateData("body2",emailRequest.getBody2());
                personalization.addDynamicTemplateData("body3",emailRequest.getBody3());
                personalization.addDynamicTemplateData("question",emailRequest.getQuestion());
                personalization.addDynamicTemplateData("title",emailRequest.getTitle());
                break;
            default:
                // code block
        }

        return personalization;

    }


}
