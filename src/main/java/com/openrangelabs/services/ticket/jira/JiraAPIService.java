package com.openrangelabs.services.ticket.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.net.URI;
import java.util.*;

@Slf4j
@Configuration
@Service
public class JiraAPIService {

    @Value("${app.environment}")
    String ENVIRONMENT;

    @Value("${jira.username}")
    String USERNAME;

    @Value("${jira.password}")
    String PASSWORD;

    @Value("${jira.url}")
    String JIRA_SERVER;

    @Value("${jira.sdo}")
    String JIRA_SDO;

    //    RestTemplate restTemplate;

    public JiraAPIService() {
    }

    public JiraRestClient getJiraRestClient() {
        return new AsynchronousJiraRestClientFactory()
                .createWithBasicHttpAuthentication(URI.create(JIRA_SERVER), USERNAME, PASSWORD);
    }

    public Issue getIssue(String issueKey) {
        JiraRestClient restClient = getJiraRestClient();
        return restClient.getIssueClient().getIssue(issueKey).claim();
    }

    public Iterable<Issue> getPdoTickets(Long organizationId) {
        if (!ENVIRONMENT.toLowerCase().contains("prod")) {
            // TODO - Setting this organization ID this way is a problem
            organizationId = 4450154L;
        }

        Set<String> fieldSet = new HashSet<String>();
        fieldSet.add("*all");
        String jql = "project="+JIRA_SDO+" and status not in (Canceled,Cancelled) and \"Fusebill ID\"="+organizationId;//+"+ORDER+BY+updated+DESC";// project=TTA+and+status+not+in(Cancelled,Resolved,Backlog)+order+by+createdDate+desc";
        if (ENVIRONMENT.equalsIgnoreCase("production")) {
            jql = "search?jql=project="+JIRA_SDO+"+and+status+not+in(Canceled,Cancelled,Resolved,completed,Backlog)+and+%22Customer+Name%3A%3AID%22+is+not+empty+and+createdDate>=2020-01-01+order+by+createdDate+desc";
        }
        log.info("URL: " +jql );
        JiraRestClient jiraRestClient = getJiraRestClient();
        SearchResult searchResults = jiraRestClient.getSearchClient().searchJql(jql, 20, 0, fieldSet).claim();
        log.info("Total Records Returned " + searchResults.getTotal());
        return searchResults.getIssues();
    }

    public Issue getSDOTicket(String key) {
        return this.getIssue(key);
    }

}
