package com.example.secondarymicroservice.config;

import com.example.secondarymicroservice.filter.TransactionIdFilter;
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
}
