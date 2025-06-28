package com.openrangelabs.services.operations;

import com.openrangelabs.services.datacenter.bloxops.dao.mapper.DatacenterBloxopsDAO;
import com.openrangelabs.services.datacenter.model.bloxops.dao.DatacenterAccessResponse;
import com.openrangelabs.services.log.dao.LoggingDAO;
import com.openrangelabs.services.log.model.LogRecord;
import com.openrangelabs.services.message.sendGrid.SendGridAPIService;
import com.openrangelabs.services.operations.dao.OperationsBloxopsDAO;
import com.openrangelabs.services.operations.jwt.JWTValidateRequest;
import com.openrangelabs.services.operations.model.*;
import com.openrangelabs.services.operations.model.*;
import com.openrangelabs.services.organization.OrganizationService;
import com.openrangelabs.services.organization.bloxops.dao.BloxopsOrganizationDAO;
import com.openrangelabs.services.organization.model.OrganizationContact;
import com.openrangelabs.services.roster.RosterUserService;
import com.openrangelabs.services.roster.entity.RosterUser;
import com.openrangelabs.services.roster.model.GetBadgeDetailsRequest;
import com.openrangelabs.services.roster.model.GetBadgeDetailsResponse;
import com.openrangelabs.services.roster.model.RosterUserUpdateRequest;
import com.openrangelabs.services.roster.model.RosterUserUpdateResponse;
import com.openrangelabs.services.tools.Commons;
import com.sendgrid.helpers.mail.objects.Personalization;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class OperationsService {
    OrganizationService organizationService;
    RosterUserService rosterUserService;
    DatacenterBloxopsDAO datacenterBloxopsDAO;
    OperationsBloxopsDAO operationsBloxopsDAO;
    LoggingDAO loggingDAO;
    RabbitTemplate rabbitTemplate;
    SendGridAPIService sendGridAPIService;
    BloxopsOrganizationDAO bloxopsOrganizationDAO;


    @Autowired
    public void organizationController(OrganizationService organizationService, RosterUserService rosterUserService,
                                       DatacenterBloxopsDAO datacenterBloxopsDAO, OperationsBloxopsDAO operationsBloxopsDAO,
                                       RabbitTemplate rabbitTemplate , LoggingDAO loggingDAO,
                                       SendGridAPIService sendGridAPIService, BloxopsOrganizationDAO bloxopsOrganizationDAO) {
        this.organizationService = organizationService;
        this.rosterUserService = rosterUserService;
        this.datacenterBloxopsDAO = datacenterBloxopsDAO;
        this.operationsBloxopsDAO = operationsBloxopsDAO;
        this.sendGridAPIService = sendGridAPIService;
        this.rabbitTemplate = rabbitTemplate;
        this.loggingDAO = loggingDAO;
        this.bloxopsOrganizationDAO = bloxopsOrganizationDAO;
    }

    public GetBadgeDetailsResponse getBadges(JWTValidateRequest jwtValidateRequest , int rosterUserId){
        try {
                GetBadgeDetailsRequest getBadgeDetailsRequest = new GetBadgeDetailsRequest();
                getBadgeDetailsRequest.setRosterUserId(rosterUserId);
                return rosterUserService.getBadgeDetails( getBadgeDetailsRequest);

        }catch(Exception e){
            return new GetBadgeDetailsResponse(e.getMessage());
        }
    }

    public DatacenterAccessResponse getBadgeAccessLogs( JWTValidateRequest jwtValidateRequest ,  int cardNumber) {
        try {
            return organizationService.getDataCenterUserAccessLogsAdmin(cardNumber);

        }catch(Exception e){
            return new DatacenterAccessResponse(e.getMessage());
        }
    }

    public RosterUserUpdateResponse updateRosterUser( RosterUserUpdateRequest rosterUserUpdateRequest ) {
        try {
            RosterUser rosterUser = rosterUserUpdateRequest.getRosterUser();
            LogRecord logRecord = new LogRecord(0 , 0,"Roster User Update " + rosterUser.getFirstName() + " " +rosterUser.getLastName(),"Roster User Update");
            rabbitTemplate.convertAndSend(Commons.LOGGING_EXCHANGE, "logs-system",logRecord);
            return rosterUserService.updateRosterUser(rosterUserUpdateRequest);

        }catch(Exception e){
            return new RosterUserUpdateResponse(e.getMessage());
        }
    }

    public GetLinksResponse getLinks(HttpServletRequest request) {
        GetLinksResponse getLinksResponse = new GetLinksResponse();
        try{
            log.info("Getting all operations links");
            List<Link> links = operationsBloxopsDAO.getLinks();
            for(Link link : links){
                link.setTags(getLinkTags(link.getId()));
            }
            getLinksResponse.setLinks(links);

        }catch(Exception e){
            e.printStackTrace();
            log.error("Error Getting all operations links");
            getLinksResponse.setError(e.getMessage());

        }
        return getLinksResponse;
    }

    public List<String> getLinkTags(int linkId) {
        List<String> tags = new ArrayList<>();
        try{
            log.info("Getting all operations links");
            List<Tag> linkTags = operationsBloxopsDAO.getTags(linkId);
            for(Tag tag : linkTags){
               tags.add(tag.getTerm().toLowerCase());
            }

        }catch(Exception e){
            log.error("Error Getting all tags for link " + linkId);
        }
        return tags;
    }

    public GetFeedResponse getFeed() {
        GetFeedResponse getFeedResponse = new GetFeedResponse();
        List<Entry> entries = new ArrayList<>();
        try{
            log.info("Getting operations feed");
            URL feedSource = new URL("https://www.openrangelabs.com/feed/");
            //SyndFeedInput input = new SyndFeedInput();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(feedSource.openStream());
            doc.getDocumentElement().normalize();
            Element channel = (Element) doc.getElementsByTagName("channel").item(0);
            String title = channel.getElementsByTagName("title").item(0).getTextContent();
            String description = channel.getElementsByTagName("description").item(0).getTextContent();
            NodeList items = channel.getElementsByTagName("item");
            for (int i = 0; i < items.getLength(); i++) {
                Entry entry1 = new Entry();
                Element item = (Element) items.item(i);
                entry1.setTitle(item.getElementsByTagName("title").item(0).getTextContent());
                entry1.setLink(item.getElementsByTagName("link").item(0).getTextContent());
                entries.add(entry1);
            }
            getFeedResponse.setTitle(title);
            getFeedResponse.setDescription(description);
            getFeedResponse.setEntries(entries);

        }catch(Exception e){
            log.error("Error Getting all operations links");
        }
        return getFeedResponse;
    }

    public GetLogsResponse getLogs(String logType) {
        GetLogsResponse getLogsResponse = new GetLogsResponse();
        List<LogRecord> logRecords = new ArrayList<>();
        try{
            log.info("Getting logs " + logType);
            if(logType.contains("user")) {
              logRecords =  loggingDAO.getUserLogs();
            }else {
              logRecords =  loggingDAO.getSystemLogs();
            }
            getLogsResponse.setLogs(logRecords);
            return getLogsResponse;
        }catch(Exception e){
            log.error("Error Getting logs "+ logType);
            getLogsResponse.setError(e.getMessage());
        }
        return getLogsResponse;
    }

    public GetLogsResponse getUserLogs(int userId) {
        GetLogsResponse getLogsResponse = new GetLogsResponse();
        List<LogRecord> logRecords = new ArrayList<>();
        try{
            log.info("Getting User logs " + userId);

                logRecords =  loggingDAO.getUserLogs(userId);

            getLogsResponse.setLogs(logRecords);
            return getLogsResponse;
        }catch(Exception e){
            log.error("Error Getting logs "+ userId);
            getLogsResponse.setError(e.getMessage());
        }
        return getLogsResponse;
    }

    public LinksResponse updateLinks(LinksUpdateRequest linksUpdateRequest) {
        LinksResponse linksResponse = new LinksResponse();
        try{
            log.info("Update links");
            int updated = operationsBloxopsDAO.updateLink(linksUpdateRequest);
            if(updated != 0) {
                linksResponse.setSuccess(true);
                linksResponse.setError(null);
            }else{
                linksResponse.setSuccess(false);
                linksResponse.setError("Error updating link.");
            }
            LogRecord logRecord = new LogRecord(0 , 0,"Link updated " + linksUpdateRequest.getUrl(),"Link Updated");
            rabbitTemplate.convertAndSend(Commons.LOGGING_EXCHANGE, "logs-system",logRecord);

        }catch(Exception e){
            log.error(e.getMessage());
            log.error("Error updating links");
            linksResponse.setSuccess(false);
            linksResponse.setError(e.getMessage());

        }
        return linksResponse;
    }

    public LinksResponse addLink(LinksUpdateRequest linksUpdateRequest) {
        LinksResponse linksResponse = new LinksResponse();
        try{
            log.info("Add links");
            int updated = operationsBloxopsDAO.addLink(linksUpdateRequest);
            if(updated != 0) {
                linksResponse.setSuccess(true);
                linksResponse.setError(null);
            }else{
                linksResponse.setSuccess(false);
                linksResponse.setError("Error adding link.");
            }
            LogRecord logRecord = new LogRecord(0 , 0,"Link added" + linksUpdateRequest.getUrl(),"Link Added");
            rabbitTemplate.convertAndSend(Commons.LOGGING_EXCHANGE, "logs-system",logRecord);

        }catch(Exception e){
            log.error(e.getMessage());
            log.error("Error adding links");
            linksResponse.setSuccess(false);
            linksResponse.setError(e.getMessage());

        }
        return linksResponse;
    }

    public LinksResponse deleteLink(LinksUpdateRequest linksUpdateRequest) {
        LinksResponse linksResponse = new LinksResponse();
        try{
            log.info("Delete link");
            int updated = operationsBloxopsDAO.deleteLink(linksUpdateRequest);
            if(updated != 0) {
                linksResponse.setSuccess(true);
                linksResponse.setError(null);
            }else{
                linksResponse.setSuccess(false);
                linksResponse.setError("Error removing link.");
            }
            LogRecord logRecord = new LogRecord(0 , 0,"Link deleted " + linksUpdateRequest.getUrl(),"Link Deleted");
            rabbitTemplate.convertAndSend(Commons.LOGGING_EXCHANGE, "logs-system",logRecord);
        }catch(Exception e){
            log.error(e.getMessage());
            log.error("Error removing links");
            linksResponse.setSuccess(false);
            linksResponse.setError(e.getMessage());

        }
        return linksResponse;
    }

    public AlertsResponse getAlerts() {
        AlertsResponse alertsResponse = new AlertsResponse();
        List<Alert> alerts = new ArrayList<>();
        try{
            log.info("Get alerts");
            alerts = operationsBloxopsDAO.getAlerts();
            alertsResponse.setAlerts(alerts);
        }catch(Exception e){
            log.error(e.getMessage());
            log.error("Error getting alerts");
            alertsResponse.setError(e.getMessage());
        }
        return alertsResponse;
    }

    public AlertsResponse getAlert() {
        AlertsResponse alertsResponse = new AlertsResponse();
        List<Alert> alerts = new ArrayList<>();
        try{
            log.info("Get alert");
            alerts = operationsBloxopsDAO.getAlert();
            alertsResponse.setAlerts(alerts);
        }catch(Exception e){
            log.error(e.getMessage());
            log.error("Error getting alert");
            alertsResponse.setError(e.getMessage());
        }
        return alertsResponse;
    }

    public AlertsResponse createAlert(Alert alert) {
        AlertsResponse alertsResponse = new AlertsResponse();
        try{
            log.info("Adding Alert");
            int updated = operationsBloxopsDAO.addAlert(alert);
            if(updated != 0) {
                alertsResponse = getAlerts();
            }else{
                alertsResponse.setError("Error adding alert.");
            }

        }catch(Exception e){
            log.error(e.getMessage());
            log.error("Error adding alert");
            alertsResponse.setError(e.getMessage());

        }
        return alertsResponse;
    }

    public AlertsResponse updateAlert(Alert alert) {
        AlertsResponse alertsResponse = new AlertsResponse();
        try{
            log.info("Updating Alert");
            int updated = operationsBloxopsDAO.updateAlert(alert);
            if(updated != 0) {
                alertsResponse = getAlerts();
            }else{
                alertsResponse.setError("Error updating alert.");
            }

        }catch(Exception e){
            log.error(e.getMessage());
            log.error("Error adding alert");
            alertsResponse.setError(e.getMessage());

        }
        return alertsResponse;
    }

    public SubscriptionResponse getSubscriptions() {
        SubscriptionResponse subscriptionResponse = new SubscriptionResponse();
        List<Subscription> subscriptions = new ArrayList<>();
        try{
            log.info("Get subscriptions");
            subscriptions = operationsBloxopsDAO.getSubscriptions();
            subscriptionResponse.setSubscriptions(subscriptions);
        }catch(Exception e){
            log.error(e.getMessage());
            log.error("Error getting subscriptions");
            subscriptionResponse.setError(e.getMessage());
        }
        return subscriptionResponse;
    }

    public SubscriptionResponse createSubscription(Subscription subscription) {
        SubscriptionResponse subscriptionResponse = new SubscriptionResponse();
        try{
            log.info("Adding Subscriptions");
            int updated = operationsBloxopsDAO.addSubscription(subscription);
            if(updated != 0) {
                subscriptionResponse = getSubscriptions();
            }else{
                subscriptionResponse.setError("Error adding subscription.");
            }

        }catch(Exception e){
            log.error(e.getMessage());
            log.error("Error adding subscription");
            subscriptionResponse.setError(e.getMessage());

        }
        return subscriptionResponse;
    }

    public SubscriptionResponse updateSubscription(Subscription subscription) {
        SubscriptionResponse subscriptionResponse = new SubscriptionResponse();
        try{
            log.info("Updating Subscription");
            int updated = operationsBloxopsDAO.updateSubscription(subscription);
            if(updated != 0) {
                subscriptionResponse = getSubscriptions();
            }else{
                subscriptionResponse.setError("Error adding subscription.");
            }

        }catch(Exception e){
            log.error(e.getMessage());
            log.error("Error adding subscription");
            subscriptionResponse.setError(e.getMessage());

        }
        return subscriptionResponse;
    }

    public SubscriptionResponse deleteSubscription(Subscription subscription) {
        SubscriptionResponse subscriptionResponse = new SubscriptionResponse();
        try{
            log.info("Delete subscription");
            int updated = operationsBloxopsDAO.deleteSubscription(subscription);
            if(updated != 0) {
                subscriptionResponse.setSubscriptions(getSubscriptions().getSubscriptions());
            }else{
                subscriptionResponse.setError("Error removing link.");
            }

        }catch(Exception e){
            log.error(e.getMessage());
            log.error("Error removing subscription");
            subscriptionResponse.setError(e.getMessage());
        }
        return subscriptionResponse;
    }

    public TagResponse getTags(int linkId) {
        TagResponse tagResponse = new TagResponse();
        List<Tag> tags = new ArrayList<>();
        List<String> tagList = new ArrayList<>();
        try{
            log.info("Get Tags for link ID : " + linkId);
            tags = operationsBloxopsDAO.getTags(linkId);
            for(Tag tag : tags){
                tagList.add(tag.getTerm());
            }
            tagResponse.setTags(tagList);
        }catch(Exception e){
            log.error(e.getMessage());
            log.error("Error getting tags");
            tagResponse.setError(e.getMessage());
        }
        return tagResponse;
    }

    public TagResponse updateTag(TagRequest tagRequest) {
        TagResponse tagResponse = new TagResponse();
        try{
            log.info("Updating Tags for Link ID : " + tagRequest.getId() );
            if(tagRequest.getAction().equalsIgnoreCase("add")) {
                int updated = operationsBloxopsDAO.addTag(tagRequest);
                if (updated != 0) {
                    tagResponse = getTags(tagRequest.getId());
                } else {
                    tagResponse.setError("Error adding tag.");
                }
            }else if(tagRequest.getAction().equalsIgnoreCase("remove")){
                int updated = operationsBloxopsDAO.removeTag(tagRequest);
                if (updated != 0) {
                    tagResponse = getTags(tagRequest.getId());
                } else {
                    tagResponse.setError("Error removing tag.");
                }
            }

        }catch(Exception e){
            log.error(e.getMessage());
            log.error("Error adding tag");
            tagResponse.setError(e.getMessage());
            
        }
        return tagResponse;
    }

    public List<Timesheet> getTimesheets(String date) {
        List<Timesheet> timesheets = new ArrayList<>();
        try{
            log.info("Get timesheets");
            // Get the first date of the current month
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            if(date.contains("default")){
                LocalDate currentDate = LocalDate.now();

                // Getting the last 60 days
                LocalDate past3Months = currentDate.minusMonths(3);
                timesheets = operationsBloxopsDAO.getTimesheets(past3Months.format(formatter), currentDate.format(formatter));
            }else{
                LocalDate currentDate = LocalDate.parse(date);
                LocalDate endDate = currentDate.plusMonths(1);

                timesheets = operationsBloxopsDAO.getTimesheets(currentDate.format(formatter), endDate.format(formatter));
            }
        timesheets.sort(Comparator.comparing(Timesheet::getEntry_date).reversed());

        }catch(Exception e){
            log.error(e.getMessage());
            log.error("Error getting timesheets");

        }
        return timesheets;
    }

    public SignatureEmailResponse sendSignatureEmail(SignatureEmailRequest signatureEmailRequest) throws IOException {
        SignatureEmailResponse signatureEmailResponse = new SignatureEmailResponse();
        try {
            Personalization personalization = new Personalization();
            personalization.addDynamicTemplateData("html_content", signatureEmailRequest.getHtmlContent());
            sendGridAPIService.sendEmail(signatureEmailRequest.getEmailTo(), null, personalization, "email_signature");
            signatureEmailResponse.setSuccess(true);
        }catch(Exception e){
            log.error(e.getMessage());
            log.error("Error sending signature email");
            signatureEmailResponse.setError(e.getMessage());
        }
        return signatureEmailResponse;
    }

    public GetServicesResponse getServices() {
        GetServicesResponse getServicesResponse = new GetServicesResponse();
        try{
            log.info("Getting all locations");
            List<com.openrangelabs.services.operations.model.Service> services = datacenterBloxopsDAO.getAllServices();
            getServicesResponse.setServices(services);

        }catch(Exception e){
            log.error("Error Getting all locations");
            getServicesResponse.setError(e.getMessage());
        }
        return getServicesResponse;
    }

    public UploadContactsResponse processFile(MultipartFile file, String fileType) {
        List<OrganizationContact> insertContactList = new ArrayList<>();
        // Read CSV using FileReader and Apache Commons CSV
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            // Parse CSV with proper configuration
            CSVParser csvParser = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim()
                    .parse(reader);

            List<CSVRecord> records = csvParser.getRecords();
            for (CSVRecord record : records) {
                String organizationTempName = record.get("Name");
                String phone = "";
                String desc = "";
                String email = "";
                for (int j = 1; j <= 6; j++) {
                    try {
                        //Adding because there is not an Email 6 section but a 6 for Phone Desc , etc
                        phone = record.get("Phone " + j);
                        desc = record.get("Desc " + j);
                        email = record.get("Email " + j);
                    }catch(Exception e){
                        log.info("Mapping not found in csv file " + e.getMessage());
                    }

                    if (phone != null && !phone.isEmpty() ||  email != null && !email.isEmpty()) {
                        // Split Desc into first name and last name
                        String[] names = desc.split(" ", 2);
                        String firstName = names[0];
                        String lastName = names.length > 1 ? names[1] : "";
                        OrganizationContact contact = new OrganizationContact();
                        contact.setOrganizationTempName(organizationTempName);
                        contact.setFirstName(firstName);
                        contact.setLastName(lastName);
                        contact.setEmail(email);
                        contact.setPhone(phone);
                        contact.setCreatedAt(LocalDateTime.now());
                        contact.setUpdatedAt(LocalDateTime.now());
                        insertContactList.add(contact);
                    }
                }
            }
            // Save all contacts in batch
            organizationService.saveAllContacts(insertContactList);

            return new UploadContactsResponse(true, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new UploadContactsResponse(false, null);
    }

    public GetContactsResponse getEmergencyContacts(String orgId) {
        List<OrganizationContact> contacts;
        try {
            if (orgId != null && !orgId.isEmpty()) {
                contacts = bloxopsOrganizationDAO.findEmergencyContacts(orgId);
            } else {
                contacts = bloxopsOrganizationDAO.findEmergencyContacts(null);
            }
            return new GetContactsResponse(contacts, null);
        }catch(Exception e){
            e.printStackTrace();
            return new GetContactsResponse(null, e.getMessage());
        }
    }
}
