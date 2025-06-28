package com.openrangelabs.services.config;


import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.web.client.BonitaClient;
import org.bonitasoft.web.client.log.LogContentLevel;
import org.bonitasoft.web.client.model.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
public class BonitaConfig {
    @Value("${bonitaURL}")
    private String bonitaUrl;

    @Value("${bonitaAdminUser}")
    private String bonitaUsername;

    @Value("${bonitaAdminPassword}")
    private String bonitaPassword;

    public BonitaClient client;

    public Session session;

    public String version;

    BonitaConfig() {
    }

    public void init() {
        // Create a client
        this.client = BonitaClient.builder(bonitaUrl).disableCertificateCheck(true).logContentLevel(LogContentLevel.FULL).build();
        this.session = this.client.login(bonitaUsername, bonitaPassword);
    }

    public Session getSession() {
        this.version = this.session.getVersion();
        return session;
    }

    public void refresh() {
        this.session = this.client.login(bonitaUsername, bonitaPassword);
    }

    public void logout() {
        // Logout when done
        this.client.logout();
    }

}
