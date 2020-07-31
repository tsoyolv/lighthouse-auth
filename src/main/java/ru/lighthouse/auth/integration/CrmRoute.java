package ru.lighthouse.auth.integration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class CrmRoute {
    public CrmRoute(@Value("${crm-main-service.url}") String baseUrl,
                    @Value("${crm-main-service.context-path}") String contextPath) {
        this.baseUrl = baseUrl + contextPath;
    }

    private final String baseUrl;

    @Value("${crm-main-service.integration.uri.user}")
    private String userUri;
}
