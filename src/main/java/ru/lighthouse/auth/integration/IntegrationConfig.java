package ru.lighthouse.auth.integration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class IntegrationConfig {
    public static final String ROLE_INTEGRATION = "ROLE_INTEGRATION";

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
