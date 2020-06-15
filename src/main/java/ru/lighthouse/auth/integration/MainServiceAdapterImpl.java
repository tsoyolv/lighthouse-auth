package ru.lighthouse.auth.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.lighthouse.auth.security.JwtAuthenticationFilter;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

@Service
public class MainServiceAdapterImpl implements MainServiceAdapter {
    private final RestTemplate restTemplate = new RestTemplate();
    public static final String USER_URI = "/integration/user";

    @Value("${main-service.url}")
    private String mobileUrl;

    @Value("${web-service.url}")
    private String webUrl;

    @Override
    public FutureTask<UserDto> getOrCreateUser(UserDto userDto) {
        final Optional<AuthorityDto> role = userDto.getAuthorities().stream().findFirst();
        final JwtAuthenticationFilter.DefaultAuthority defaultAuthority = JwtAuthenticationFilter.DefaultAuthority.valueOf(role.get().getSystemName());
        Callable<UserDto> task;
        switch (defaultAuthority) {
            case ROLE_WEB -> task = () -> restTemplate.postForEntity(webUrl + USER_URI, userDto, UserDto.class).getBody();
            default -> task = () -> restTemplate.postForEntity(mobileUrl + USER_URI, userDto, UserDto.class).getBody();
        }
        return getFuture(task);
    }

    private FutureTask<UserDto> getFuture(Callable<UserDto> task) {
        FutureTask<UserDto> future = new FutureTask<>(task);
        new Thread(future).start();
        return future;
    }
}
