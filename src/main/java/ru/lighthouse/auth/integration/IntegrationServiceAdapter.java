package ru.lighthouse.auth.integration;

import java.util.concurrent.FutureTask;

public interface IntegrationServiceAdapter {
    FutureTask<UserDto> getOrCreateUser(UserDto userDto);
}
