package ru.lighthouse.auth.integration;

import eu.bitwalker.useragentutils.UserAgent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.lighthouse.auth.integration.dto.UserDto;
import ru.lighthouse.auth.security.JWTService;

import java.util.concurrent.FutureTask;

import static eu.bitwalker.useragentutils.DeviceType.MOBILE;
import static eu.bitwalker.useragentutils.Manufacturer.APPLE;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

@Service
@RequiredArgsConstructor
public class IntegrationServiceAdapterImpl implements IntegrationServiceAdapter {
    private final RestTemplate restTemplate = new RestTemplate();
    private final JWTService jwtService;
    private static final String ROLE_INTEGRATION = "ROLE_INTEGRATION";

    @Value("${mobile-service.url}")
    private String mobileUrl;
    @Value("${mobile-service.prefix}")
    private String mobilePrefix;

    @Value("${crm-service.url}")
    private String crmUrl;
    @Value("${crm-service.prefix}")
    private String crmPrefix;

    @Value("${integration.uri.user}")
    private String integrationUserUri;

    @Override
    public FutureTask<UserDto> getOrCreateUser(UserDto userDto) {
        FutureTask<UserDto> future;
        UserAgent userAgent = UserAgent.parseUserAgentString(userDto.getUserAgent());
        if (MOBILE == userAgent.getOperatingSystem().getDeviceType()) {
            if (APPLE == userAgent.getOperatingSystem().getManufacturer()) {
                future = createFuture(userDto, mobileUrl + mobilePrefix);
            } else {
                future = createFuture(userDto, mobileUrl + mobilePrefix);
            }
        } else {
            future = createFuture(userDto, crmUrl + crmPrefix);
        }
        new Thread(future).start();
        return future;
    }

    private FutureTask<UserDto> createFuture(UserDto userDto, String webUrl) {
        return new FutureTask<>(() -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(jwtService.createJWTToken("admin", createAuthorityList(ROLE_INTEGRATION), null));
            HttpEntity<UserDto> entity = new HttpEntity<>(userDto, headers);
            return restTemplate.postForObject(webUrl + integrationUserUri, entity, UserDto.class);
        });
    }
}
