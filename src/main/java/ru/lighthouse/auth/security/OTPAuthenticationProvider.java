package ru.lighthouse.auth.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.lighthouse.auth.otp.OtpService;

import java.util.List;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

public class OTPAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private final OtpService otpService;

    public static final String DEFAULT_AUTH_ROLE = "ROLE_IOS";

    public OTPAuthenticationProvider(OtpService otpService) {
        this.otpService = otpService;
    }

    @Override
    protected UserDetails retrieveUser(String phoneNumber, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null || StringUtils.isEmpty(phoneNumber)) {
            throw new UsernameNotFoundException("No OTP");
        }
        String otp = (String) authentication.getCredentials();
        if (otpService.isOtpNotValid(phoneNumber, otp)) {
            throw new JwtAuthenticationFilter.InvalidOtpAuthenticationException("SMS_CODE_INVALID");
        }
        try {
            createUserIfNotExist(phoneNumber, otp);
        } catch (Exception e) {
            throw new UsernameNotFoundException("User creation failed");
        }
        List<GrantedAuthority> authorities = createAuthorityList(DEFAULT_AUTH_ROLE);
        return new User(phoneNumber, otp, authorities);
    }

    private void createUserIfNotExist(String phoneNumber, String otp) {

    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        // there is no need additional checks
    }
}
