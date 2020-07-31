package ru.lighthouse.auth.integration.mobile;

import ru.lighthouse.auth.integration.dto.UserDto;

public interface MobileIntegrationService {
    UserDto getOrCreateUser(UserDto user);
}
