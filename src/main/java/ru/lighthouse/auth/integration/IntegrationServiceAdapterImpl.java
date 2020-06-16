package ru.lighthouse.auth.integration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.lighthouse.auth.integration.dto.AuthorityDto;
import ru.lighthouse.auth.integration.dto.UserDto;
import ru.lighthouse.auth.security.JWTService;
import ru.lighthouse.auth.security.JwtAuthenticationFilter;

import java.util.concurrent.FutureTask;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;
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
        final AuthorityDto role = userDto.getAuthorities().iterator().next();
        final JwtAuthenticationFilter.DefaultAuthority defaultAuthority = JwtAuthenticationFilter.DefaultAuthority.valueOf(role.getSystemName());
        FutureTask<UserDto> future;
        switch (defaultAuthority) {
            case ROLE_WEB -> future = createFuture(userDto, webUrl);
            case ROLE_CRM -> future = createFuture(userDto, crmUrl);
            default -> future = createFuture(userDto, mobileUrl);
        }
        new Thread(future).start();
        return future;
    }

    private FutureTask<UserDto> createFuture(UserDto userDto, String webUrl) {
        return new FutureTask<>(() -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(jwtService.createJWTToken("79779873676", createAuthorityList(INTEGRATION_ROLE), null));
            HttpEntity<UserDto> entity = new HttpEntity<>(userDto, headers);
            return restTemplate.postForObject(webUrl + USER_URI, entity, UserDto.class);
        });
    }
}
