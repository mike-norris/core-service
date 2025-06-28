package com.openrangelabs.services.ticket.metadefender;

import com.openrangelabs.middleware.config.WebClientConfig;
import com.openrangelabs.services.ticket.metadefender.model.ScanResponse;
import com.openrangelabs.services.ticket.metadefender.model.ScanResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Service
public class MetaDefenderAPIService {
    private final WebClient webClient;

    @Value("${metaDefenderURL}")
    public String API_URL;

    @Value("${metaDefenderAPIKey}")
    public String API_KEY;

    public MetaDefenderAPIService() {
        this.webClient = WebClientConfig.build(API_URL);
    }

    public ScanResult getSanatizedFile(String dataId) {
        ResponseEntity<ScanResult> response = this.webClient.get()
                .uri(API_URL  + "v4/file/" + dataId)
                .header("apikey", API_KEY)
                .header("file_metadata", "1")
                .header(HttpHeaders.USER_AGENT, "mcl-metadefender-message-sanitize-disabled-unarchive")
                .retrieve()
                .toEntity(ScanResult.class)
                .block();
        return response.getBody();
    }

    public ScanResponse scanFile(MultipartFile file) throws IOException {
        ResponseEntity<ScanResponse> response = this.webClient.post()
                .uri(API_URL  + "v4/file/")
                .header(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.MULTIPART_FORM_DATA))
                .header(HttpHeaders.USER_AGENT, "mcl-metadefender-message-sanitize-disabled-unarchive")
                .header("apikey", API_KEY)
                .header("rule", "sanitize")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(file)
                .retrieve()
                .toEntity(ScanResponse.class)
                .block();
        return response.getBody();
    }

    public byte[] getFile(String url) throws URISyntaxException {
        ResponseEntity<byte[]> response = this.webClient.get()
                .uri(new URI(url))
                .header("apikey", API_KEY)
                .header(HttpHeaders.USER_AGENT, "mcl-metadefender-message-sanitize-disabled-unarchive")
                .retrieve()
                .toEntity(byte[].class)
                .block();
        return response.getBody();
    }
}
