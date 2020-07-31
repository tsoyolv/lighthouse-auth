package ru.lighthouse.auth.security;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.AuthenticationEntryPoint;
import ru.lighthouse.auth.integration.adapter.UserAdapter;
import ru.lighthouse.auth.integration.dto.AuthorityDto;
import ru.lighthouse.auth.integration.dto.UserDto;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

@RequiredArgsConstructor
public class OTPAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    private final JWTService jwtService;
    private final UserAdapter userAdapter;

    public interface ExceptionMessage {
        String NO_OTP = "NO_USER_OR_OTP";
        String PHONE_NUMBER_INVALID = "PHONE_NUMBER_INVALID";
        String SMS_CODE_INVALID = "SMS_CODE_INVALID";
        String DISABLED = "DISABLED";
        String LOCKED = "LOCKED";
        String INTEGRATION_ERROR = "INTEGRATION_ERROR";
    }

    @Override
    protected UserDetails retrieveUser(String phoneNumber, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null || StringUtils.isEmpty(phoneNumber)) {
            throw new AuthenticationServiceException(ExceptionMessage.NO_OTP);
        }
        String otp = String.valueOf(authentication.getCredentials());
        LinkedHashMap<String, Object> details = (LinkedHashMap<String, Object>) authentication.getDetails();
        String userAgent = (String) details.get(HttpHeaders.USER_AGENT);
        UserDto user = userAdapter.retrieveUser(phoneNumber, otp, userAgent, null);
        addAuthenticationDetails(authentication, user);
        return convertIntegrationDtoToUser(user, otp);
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (userDetails.isEnabled() == Boolean.FALSE) {
            throw new AuthenticationServiceException(ExceptionMessage.DISABLED);
        }
        if (userDetails.isAccountNonLocked() == Boolean.FALSE) {
            throw new AuthenticationServiceException(ExceptionMessage.LOCKED);
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

    private UserDetails convertIntegrationDtoToUser(UserDto authUser, String otp) {
        final List<String> authorities = authUser.getAuthorities().stream().map(AuthorityDto::getSystemName).collect(Collectors.toList());
        final List<GrantedAuthority> grantedAuthorities = createAuthorityList(authorities.toArray(String[]::new));
        return new User(authUser.getPhoneNumber(), otp, authUser.getEnabled(), true,
                true, authUser.getAccountNonLocked(), grantedAuthorities);
    }

    public static class FailedAuthenticationEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
