package ru.lighthouse.auth.integration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.lighthouse.auth.security.JWTService;
import ru.lighthouse.auth.security.JwtAuthenticationFilter;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import static ru.lighthouse.auth.integration.IntegrationProperties.INTEGRATION_ROLE;
import static ru.lighthouse.auth.integration.IntegrationProperties.USER_URI;

@Service
@RequiredArgsConstructor
public class IntegrationServiceAdapterImpl implements IntegrationServiceAdapter {
    private final RestTemplate restTemplate = new RestTemplate();
    private final JWTService jwtService;

    @Value("${mobile-service.url}")
    private String mobileUrl;
    @Value("${web-service.url}")
    private String webUrl;
    @Value("${crm-service.url}")
    private String crmUrl;

    @Override
    public FutureTask<UserDto> getOrCreateUser(UserDto userDto) {
        final Optional<AuthorityDto> role = userDto.getAuthorities().stream().findFirst();
        final JwtAuthenticationFilter.DefaultAuthority defaultAuthority = JwtAuthenticationFilter.DefaultAuthority.valueOf(role.get().getSystemName());
        Callable<UserDto> task;
        switch (defaultAuthority) {
            case ROLE_WEB -> task = getOrCreateCallable(userDto, webUrl);
            case ROLE_CRM -> task = getOrCreateCallable(userDto, crmUrl);
            default -> task = getOrCreateCallable(userDto, mobileUrl);
        }
        return getFuture(task);
    }

    private Callable<UserDto> getOrCreateCallable(UserDto userDto, String webUrl) {
        return () -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(jwtService.createJWTToken("79779873676", Collections.singletonList(INTEGRATION_ROLE), null));
            HttpEntity<UserDto> entity = new HttpEntity<>(userDto, headers);
            return restTemplate.postForObject(webUrl + USER_URI, entity, UserDto.class);
        };
    }

    private FutureTask<UserDto> getFuture(Callable<UserDto> task) {
        FutureTask<UserDto> future = new FutureTask<>(task);
        new Thread(future).start();
        return future;
    }
}
