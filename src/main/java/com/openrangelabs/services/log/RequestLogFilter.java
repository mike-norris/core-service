package com.openrangelabs.services.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class RequestLogFilter implements Filter {
        protected FilterConfig filterConfig;

    @Value("${MyOrlDomain}")
    String myOrlDomain;

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse responseImp = (HttpServletResponse) response;
        HttpServletRequest requestImp = new BufferingHttpServletRequest((HttpServletRequest) request);
        String path = requestImp.getRequestURI();
        String verb = requestImp.getMethod();
        String pid = ManagementFactory.getRuntimeMXBean().getName();
        String clientIP = "No IP Found";
        if (null != requestImp.getHeader("X-Forwarded-For")) {
            String clientAndProxies = requestImp.getHeader("X-Forwarded-For");
            try {
                String regex = "\\s*,\\s*";
                String[] clientAndProxiesList = clientAndProxies.split(regex);

                List<String> items = Arrays.asList(clientAndProxiesList);
                clientIP = items.get(0);
            } catch (Exception e) {
                log.info(e.getMessage());
            }
        }
        requestImp.getHeader("clientip");
        String requestBody = getBody(requestImp);
        log.info( OffsetDateTime.now().toString()+" | "+"Request"+" | "+verb+" | "+path+" | "+pid+" | "+clientIP+" | "+requestBody );
        chain.doFilter(requestImp, responseImp);
        int status = responseImp.getStatus();
        log.info( OffsetDateTime.now().toString()+" | "+"Response"+" | "+verb+" | "+path+" | "+status+" | "+pid+" | "+clientIP);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("Initialize filter: {}", getClass().getSimpleName());
        this.filterConfig = filterConfig;
    }

    @Override
    public void destroy() {
        log.info("Destroy filter: {}", getClass().getSimpleName());
    }

    @Bean
    public FilterRegistrationBean<RequestLogFilter> loggingFilter(){
        FilterRegistrationBean<RequestLogFilter> registrationBean
                = new FilterRegistrationBean<>();

        registrationBean.setFilter(new RequestLogFilter());
        registrationBean.addUrlPatterns("/ticket/**");
        registrationBean.addUrlPatterns("/authenticate/**");
        registrationBean.addUrlPatterns("/user/**");
        registrationBean.addUrlPatterns("/storage/**");
        registrationBean.addUrlPatterns("/roster/**");
        registrationBean.addUrlPatterns("/payment/**");
        registrationBean.addUrlPatterns("/organization/**");
        registrationBean.addUrlPatterns("/companyservice/**");

        return registrationBean;
    }

    private String getBody(HttpServletRequest request) {
        String body = "";
        if (request.getMethod().equals("POST") )
        {
            StringBuilder sb = new StringBuilder();
            BufferedReader bufferedReader = null;

            try {
                bufferedReader =  request.getReader();
                char[] charBuffer = new char[128];
                int bytesRead;
                while ((bytesRead = bufferedReader.read(charBuffer)) != -1) {
                    sb.append(charBuffer, 0, bytesRead);
                }
            } catch (IOException ex) {
                log.error(ex.getMessage());
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        // swallow silently -- can't get body, won't
                        log.info("request has no request body. URI: "+request.getRequestURI());
                        log.error(e.getMessage());
                    }
                }
            }
            body = sb.toString();
        }
        return body;
    }
}