package ru.lighthouse.auth.integration;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class IntegrationConfig {
    public static final String ROLE_INTEGRATION = "ROLE_INTEGRATION";

    @LoadBalanced // for Spring Cloud (delete if used kubernetes)
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
