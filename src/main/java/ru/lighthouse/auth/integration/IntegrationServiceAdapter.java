package ru.lighthouse.auth.integration;

import ru.lighthouse.auth.integration.dto.UserDto;

import java.util.concurrent.FutureTask;

public interface IntegrationServiceAdapter {
    FutureTask<UserDto> getOrCreateUser(UserDto userDto);
}
