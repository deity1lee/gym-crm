package org.example.config;

import org.example.filter.AuthFilter;
import org.example.filter.TransactionIdFilter;
import org.example.service.AuthService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public TransactionIdFilter transactionIdFilterBean() {
        return new TransactionIdFilter();
    }

    @Bean
    public FilterRegistrationBean<TransactionIdFilter> transactionIdFilter(
            TransactionIdFilter filter
    ) {
        FilterRegistrationBean<TransactionIdFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(filter);
        reg.addUrlPatterns("/*");
        reg.setOrder(1);
        return reg;
    }

    @Bean
    public AuthFilter authFilter(AuthService authService) {
        return new AuthFilter(authService);
    }

    @Bean
    public FilterRegistrationBean<AuthFilter> authFilterRegistration(
            AuthFilter authFilter
    ) {
        FilterRegistrationBean<AuthFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(authFilter);
        reg.addUrlPatterns("/*");
        reg.setOrder(2);
        return reg;
    }
}
