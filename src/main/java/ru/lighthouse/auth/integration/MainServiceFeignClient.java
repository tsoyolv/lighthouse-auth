package ru.lighthouse.auth.integration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="${main-service.service-id}", url="${main-service.url}")
public interface MainServiceFeignClient {
    String USER_URI = "/user";
    @PostMapping(USER_URI)
    UserDto createOrUpdateUser(@RequestBody UserDto userDto);
}
