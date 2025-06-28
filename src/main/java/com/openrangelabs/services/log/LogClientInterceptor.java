package com.openrangelabs.services.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.time.OffsetDateTime;

@Slf4j
public class LogClientInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        BufferingClientHttpResponseWrapper bufferResponse = new BufferingClientHttpResponseWrapper(response);
        logResponse(request, bufferResponse);
        return bufferResponse;
    }

    private void logRequest(HttpRequest request, byte[] body) throws IOException {
        String path = request.getURI().getPath();
        String verb = request.getMethod().toString();
        String pid = ManagementFactory.getRuntimeMXBean().getName();
        String requestBody = new String(body, "UTF-8");
        log.info(OffsetDateTime.now().toString()+" | "+"Request"+" | "+verb+" | "+path+" | "+pid+" | "+requestBody);
    }

    private void logResponse(HttpRequest request, ClientHttpResponse response) throws IOException {
        int status = response.getStatusCode().value();
        String path = request.getURI().getPath();
        String verb = request.getMethod().toString();
        String pid = ManagementFactory.getRuntimeMXBean().getName();
        String body = StreamUtils.copyToString(response.getBody(), Charset.defaultCharset());
        log.info(OffsetDateTime.now().toString()+" | "+"Response"+" | "+verb+" | "+path+" | "+status+" | "+pid+" | "+body);
    }
}