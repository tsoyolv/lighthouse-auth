package ru.lighthouse.auth.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import ru.lighthouse.auth.integration.MainServiceAdapter;
import ru.lighthouse.auth.integration.UserDto;
import ru.lighthouse.auth.otp.OtpService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

public class OTPAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private final OtpService otpService;
    private final MainServiceAdapter mainServiceAdapter;

    public static final String DEFAULT_AUTH_ROLE = "ROLE_IOS";

    public OTPAuthenticationProvider(OtpService otpService, MainServiceAdapter mainServiceAdapter) {
        this.otpService = otpService;
        this.mainServiceAdapter = mainServiceAdapter;
    }

    @Override
    protected UserDetails retrieveUser(String phoneNumber, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null || StringUtils.isEmpty(phoneNumber)) {
            throw new UsernameNotFoundException("No OTP");
        }
        String otp = (String) authentication.getCredentials();
        if (otpService.isOtpNotValid(phoneNumber, otp)) {
            throw new InvalidOtpAuthenticationException("SMS_CODE_INVALID");
        }
        try {
            createUserIfNotExist(phoneNumber);
        } catch (Exception e) {
            throw new UsernameNotFoundException("User creation failed");
        }
        List<GrantedAuthority> authorities = createAuthorityList(DEFAULT_AUTH_ROLE);
        return new User(phoneNumber, otp, authorities);
    }
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        // there is no need additional checks
    }

    private void createUserIfNotExist(String phoneNumber) throws ExecutionException, InterruptedException {
        UserDto userDto = new UserDto(phoneNumber, Collections.singletonList(DEFAULT_AUTH_ROLE));
        FutureTask<UserDto> future = mainServiceAdapter.createOrUpdateUser(userDto);
        //future.get();
    }

    public static class FailedAuthenticationEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
            if (authException instanceof InvalidOtpAuthenticationException) {
                response.sendError(HttpStatus.UNPROCESSABLE_ENTITY.value(), authException.getMessage());
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
    }

    private static class InvalidOtpAuthenticationException extends AuthenticationException {
        public InvalidOtpAuthenticationException(String msg) {
            super(msg);
        }
    }
}
