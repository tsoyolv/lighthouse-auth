package ru.lighthouse.auth.integration.mobile;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class MobileRoute {
    public MobileRoute(@Value("${mobile-main-service.url}") String baseUrl,
                       @Value("${mobile-main-service.context-path}") String contextPath) {
        this.baseUrl = baseUrl + contextPath;
    }
    
    private final String baseUrl;

    @Value("${mobile-main-service.integration.uri.user}")
    private String userUri;
}
