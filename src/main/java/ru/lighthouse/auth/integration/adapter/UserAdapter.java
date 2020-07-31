package ru.lighthouse.auth.integration.adapter;

import org.springframework.security.core.AuthenticationException;
import ru.lighthouse.auth.integration.dto.UserDto;
import ru.lighthouse.auth.integration.dto.UserType;

public interface UserAdapter {
    UserDto retrieveUser(String phoneNumber, String otp, String userAgent, UserType userType) throws AuthenticationException;
}
