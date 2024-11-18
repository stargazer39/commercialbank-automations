package com.dehemi.combank;

import com.dehemi.combank.config.PublicEndpoints;
import com.dehemi.combank.config.UsersConfig;
import com.dehemi.combank.filters.AuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableScheduling
public class AutomationApplication {
	public static void main(String[] args) {
		SpringApplication.run(AutomationApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("*");
			}
		};
	}

	@Bean
	public FilterRegistrationBean<AuthFilter> authFilter(JwtUtil jwtUtil, UsersConfig usersConfig, PublicEndpoints publicEndpoints) {
		FilterRegistrationBean<AuthFilter> registrationBean
				= new FilterRegistrationBean<>();

		registrationBean.setFilter(new AuthFilter(jwtUtil, usersConfig, publicEndpoints));
		registrationBean.addUrlPatterns("*");
		registrationBean.setOrder(1);

		return registrationBean;
	}
}
