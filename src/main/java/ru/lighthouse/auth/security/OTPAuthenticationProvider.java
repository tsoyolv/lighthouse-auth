package ru.lighthouse.auth.security;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import ru.lighthouse.auth.integration.IntegrationServiceAdapter;
import ru.lighthouse.auth.integration.dto.AuthorityDto;
import ru.lighthouse.auth.integration.dto.UserDto;
import ru.lighthouse.auth.otp.logic.OtpService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

@RequiredArgsConstructor
public class OTPAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    private final JWTService jwtService;
    private final OtpService otpService;
    private final IntegrationServiceAdapter integrationServiceAdapter;

    public interface ExceptionMessage {
        String NO_OTP = "NO_OTP";
        String SMS_CODE_INVALID = "SMS_CODE_INVALID";
        String USER_CREATION_FAILED = "User creation failed";
        String DISABLED = "DISABLED";
        String LOCKED = "LOCKED";
    }

    @Override
    protected UserDetails retrieveUser(String phoneNumber, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null || StringUtils.isEmpty(phoneNumber)) {
            throw new UsernameNotFoundException(ExceptionMessage.NO_OTP);
        }
        String otp = String.valueOf(authentication.getCredentials());
        if (otpService.isOtpNotValid(phoneNumber, otp)) {
            throw new InvalidOtpAuthenticationException(ExceptionMessage.SMS_CODE_INVALID);
        }
        try {
            final UserDto user = getOrCreateUser(phoneNumber, authentication);
            addAuthenticationDetails(authentication, user);
            return convertIntegrationDtoToUser(user, otp);
        } catch (Exception e) {
            throw new UsernameNotFoundException(ExceptionMessage.USER_CREATION_FAILED);
        }
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (userDetails.isEnabled() == Boolean.FALSE) {
            throw new UsernameNotFoundException(ExceptionMessage.DISABLED);
        }
        if (userDetails.isAccountNonLocked() == Boolean.FALSE) {
            throw new UsernameNotFoundException(ExceptionMessage.LOCKED);
        }
    }

    private void addAuthenticationDetails(UsernamePasswordAuthenticationToken authentication, UserDto user) {
        LinkedHashMap<String, Object> details = (LinkedHashMap<String, Object>) authentication.getDetails();
        details.put(jwtService.getClaimDetailsUserId(), user.getId());
        details.put(jwtService.getClaimDetailsUserBirthDate(), user.getBirthDate());
        details.put(jwtService.getClaimDetailsUserFirstName(), user.getFirstName());
        details.put(jwtService.getClaimDetailsUserSecondName(), user.getSecondName());
        details.put(jwtService.getClaimDetailsUserLastName(), user.getLastName());
        authentication.setDetails(details);
    }

    private UserDto getOrCreateUser(String phoneNumber, UsernamePasswordAuthenticationToken auth) throws ExecutionException, InterruptedException {
        LinkedHashMap<String, Object> details = (LinkedHashMap<String, Object>) auth.getDetails();
        UserDto userDto = new UserDto(phoneNumber, (String) details.get(HttpHeaders.USER_AGENT));
        FutureTask<UserDto> future = integrationServiceAdapter.getOrCreateUser(userDto);
        return future.get();
    }

    private UserDetails convertIntegrationDtoToUser(UserDto authUser, String otp) {
        final List<String> authorities = authUser.getAuthorities().stream().map(AuthorityDto::getSystemName).collect(Collectors.toList());
        final List<GrantedAuthority> grantedAuthorities = createAuthorityList(authorities.toArray(String[]::new));
        return new User(authUser.getPhoneNumber(), otp, authUser.getEnabled(), true,
                true, authUser.getAccountNonLocked(), grantedAuthorities);
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
