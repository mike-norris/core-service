package com.openrangelabs.services;

import com.openrangelabs.services.signing.session.UserData;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.context.WebApplicationContext;

@EnableScheduling
@SpringBootApplication(scanBasePackages = {"com.openrangelabs.services"})
public class UserServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServicesApplication.class, args);
	}
	@Bean
	@Scope(WebApplicationContext.SCOPE_SESSION)
	public UserData getUserData() {
		return new UserData();
	}
}
