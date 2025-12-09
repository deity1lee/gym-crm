package org.example.config;

import org.example.filter.TransactionIdFilter;
import org.example.service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

@Configuration
public class FilterConfig {

    @Bean
    public TransactionIdFilter transactionIdFilterBean() {
        return new TransactionIdFilter();
    }

    @Bean
    public FilterRegistrationBean<TransactionIdFilter> transactionIdFilter(TransactionIdFilter filter) {
        FilterRegistrationBean<TransactionIdFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(filter);
        reg.addUrlPatterns("/*");
        reg.setOrder(1);
        return reg;
    }
}
