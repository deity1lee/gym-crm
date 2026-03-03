package com.example.config;

import com.example.filter.ApiKeyFilter;
import com.example.filter.TransactionIdFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;

@Configuration
public class SecurityConfig {

    private final AuthenticationEntryPoint customAuthenticationEntryPoint;
    private final TransactionIdFilter transactionIdFilter;

    public SecurityConfig(AuthenticationEntryPoint customAuthenticationEntryPoint, TransactionIdFilter transactionIdFilter) {
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        this.transactionIdFilter = transactionIdFilter;
    }

    @Bean
    public ApiKeyFilter apiKeyFilter(@Value("${security.api-key}") String apiKey) {
        return new ApiKeyFilter(apiKey);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ApiKeyFilter apiKeyFilter) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                .exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer
                        .authenticationEntryPoint(customAuthenticationEntryPoint))
                .addFilterBefore(transactionIdFilter, ExceptionTranslationFilter.class)
                .addFilterAfter(apiKeyFilter, ExceptionTranslationFilter.class)
                .build();
    }
}
