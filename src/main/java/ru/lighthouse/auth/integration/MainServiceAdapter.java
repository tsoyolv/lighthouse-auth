package ru.lighthouse.auth.integration;

import java.util.concurrent.FutureTask;

public interface MainServiceAdapter {
    FutureTask<UserDto> createOrUpdateUser(UserDto userDto);
}
