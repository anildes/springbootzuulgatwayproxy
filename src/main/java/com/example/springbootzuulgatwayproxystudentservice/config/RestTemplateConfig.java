package com.example.springbootzuulgatwayproxystudentservice.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
public class RestTemplateConfig {

    private final ZuulAuthInterceptor zuulAuthInterceptor;

    // Spring injects the @Component ZuulAuthInterceptor automatically
    public RestTemplateConfig(ZuulAuthInterceptor zuulAuthInterceptor) {
        this.zuulAuthInterceptor = zuulAuthInterceptor;
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        
        // Add the custom interceptor to the RestTemplate's interceptors list
        restTemplate.setInterceptors(Collections.singletonList(zuulAuthInterceptor));
        
        return restTemplate;
    }
}