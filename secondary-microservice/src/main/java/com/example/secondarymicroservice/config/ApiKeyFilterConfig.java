package com.example.secondarymicroservice.config;

import com.example.secondarymicroservice.filter.ApiKeyFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiKeyFilterConfig {

    @Bean
    public ApiKeyFilter apiKeyFilter() {
        return new ApiKeyFilter();
    }

    @Bean
    public FilterRegistrationBean<ApiKeyFilter> apiKeyFilterRegistration(ApiKeyFilter filter) {
        FilterRegistrationBean<ApiKeyFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter); // Injecting the bean from above
        registrationBean.addUrlPatterns("/workload", "/workload/*");

        // This prevents the filter from running twice (once globally and once for /workload)
        registrationBean.setEnabled(true);

        return registrationBean;
    }
}
