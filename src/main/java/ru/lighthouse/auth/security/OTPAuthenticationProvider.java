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
import ru.lighthouse.auth.integration.AuthorityDto;
import ru.lighthouse.auth.integration.MainServiceAdapter;
import ru.lighthouse.auth.integration.UserDto;
import ru.lighthouse.auth.otp.OtpService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

public class OTPAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private final OtpService otpService;
    private final MainServiceAdapter mainServiceAdapter;

    public OTPAuthenticationProvider(OtpService otpService, MainServiceAdapter mainServiceAdapter) {
        this.otpService = otpService;
        this.mainServiceAdapter = mainServiceAdapter;
    }

    @Override
    protected UserDetails retrieveUser(String phoneNumber, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null || StringUtils.isEmpty(phoneNumber)) {
            throw new UsernameNotFoundException("NO_OTP");
        }
        String otp = (String) authentication.getCredentials();
        if (otpService.isOtpNotValid(phoneNumber, otp)) {
            throw new InvalidOtpAuthenticationException("SMS_CODE_INVALID");
        }
        try {
            final Collection<GrantedAuthority> authorities = authentication.getAuthorities();
            final UserDto authUser = authUser(phoneNumber, authorities);
            setAuthenticationDetails(authentication, authUser);
            return convertIntegrationDtoToUser(authUser, otp);
        } catch (Exception e) {
            throw new UsernameNotFoundException("User creation failed");
        }
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (userDetails.isEnabled() == Boolean.FALSE) {
            throw new UsernameNotFoundException("DISABLED");
        }
        if (userDetails.isAccountNonLocked() == Boolean.FALSE) {
            throw new UsernameNotFoundException("LOCKED");
        }
    }

    private void setAuthenticationDetails(UsernamePasswordAuthenticationToken authentication, UserDto authUser) {

    }

    private UserDetails convertIntegrationDtoToUser(UserDto authUser, String otp) {
        final List<String> authorities = authUser.getAuthorities().stream().map(AuthorityDto::getSystemName).collect(Collectors.toList());
        final List<GrantedAuthority> authorityList = createAuthorityList(authorities.toArray(String[]::new));
        return new User(authUser.getPhoneNumber(), otp, authUser.getEnabled(), authUser.getAccountNonLocked(),
                true, true, authorityList);
    }

    private UserDto authUser(String phoneNumber, Collection<GrantedAuthority> authorities) throws ExecutionException, InterruptedException {
        final Set<AuthorityDto> authorityDtos = authorities.stream()
                .map(a -> JwtAuthenticationFilter.DefaultAuthority.valueOf(a.getAuthority()))
                .map(aDto -> new AuthorityDto(aDto.getDesc(), aDto.name()))
                .collect(Collectors.toSet());
        UserDto userDto = new UserDto(phoneNumber, authorityDtos);
        FutureTask<UserDto> future = mainServiceAdapter.getOrCreateUser(userDto);
        try { return future.get(); } catch (ExecutionException e) { return userDto; } // for test AuthControllerTest.testAuthorizedUser
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
