package com.openrangelabs.services.config;

import com.openrangelabs.services.authenticate.interceptor.AuthenticateInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Configuration
@ComponentScan("com.openrangelabs.services.authenticate.interceptor")
public class WebConfig implements WebMvcConfigurer  {

    @Value("${bonitaEnvironment}")
    String environment;

    @Value("${app.excludeURLs}")
    private String[] excludedURLs;

    @Autowired
    AuthenticateInterceptor authenticateInterceptor;

    List<String> excludeURLs = new ArrayList<>();

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        Stream.of(excludedURLs).forEach(
                url -> excludeURLs.add(url)
        );

        if(!environment.contains("prod")){
            excludeURLs.add("/authenticate/login/test");
        }

        registry.addInterceptor(authenticateInterceptor).excludePathPatterns(excludeURLs);
    }

    @Bean
    CharacterEncodingFilter characterEncodingFilter() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        return filter;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
