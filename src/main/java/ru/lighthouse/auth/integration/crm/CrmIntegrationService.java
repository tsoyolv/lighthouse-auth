package ru.lighthouse.auth.integration.crm;

import ru.lighthouse.auth.integration.dto.UserDto;

public interface CrmIntegrationService {
    UserDto getUser(UserDto user);
}
