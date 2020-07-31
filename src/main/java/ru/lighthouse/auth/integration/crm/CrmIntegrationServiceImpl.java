package ru.lighthouse.auth.integration.crm;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.lighthouse.auth.integration.dto.UserDto;
import ru.lighthouse.auth.security.JWTService;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;
import static ru.lighthouse.auth.integration.IntegrationConfig.ROLE_INTEGRATION;

@Service
@RequiredArgsConstructor
public class CrmIntegrationServiceImpl implements CrmIntegrationService {
    private final CrmRoute route;
    private final JWTService jwtService;
    private final RestTemplate restTemplate;

    @Override
    public UserDto getUser(UserDto user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(jwtService.createJWTToken("admin", createAuthorityList(ROLE_INTEGRATION), null));
        HttpEntity<UserDto> entity = new HttpEntity<>(user, headers);
        return restTemplate.postForObject(route.getBaseUrl() + route.getUserUri(), entity, UserDto.class);
    }
}
