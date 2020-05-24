package ru.lighthouse.auth.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ru.lighthouse.auth.api.entity.User;
import ru.lighthouse.auth.api.service.OtpService;
import ru.lighthouse.auth.api.service.UserService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authManager;
    private final JWTService jwtService;
    private final OtpService otpService;
    private final UserService userService;
    private final PasswordEncoder encoder;

    public JwtAuthenticationFilter(AuthenticationManager authManager, JWTService jwtService, OtpService otpService, UserService userService, PasswordEncoder encoder) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.otpService = otpService;
        this.userService = userService;
        this.encoder = encoder;
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(jwtService.getConfiguration().getAuthUri(), HttpMethod.POST.name()));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            String phoneNumber = obtainPhoneNumber(request);
            String otp = obtainOtp(request);
            if (otpService.isOtpNotValid(phoneNumber, otp)) {
                throw new InvalidOtpAuthenticationException("SMS_CODE_INVALID");
            }
            Optional<User> userOptional = userService.findByPhoneNumber(phoneNumber);
            String encodedOtp = encoder.encode(otp);
            User user = userOptional.orElse(new User(phoneNumber, encodedOtp));
            user.setLastOtp(encodedOtp);
            userService.saveUser(user);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(phoneNumber, otp, Collections.emptyList());
            return authManager.authenticate(authToken);
        } catch (IOException e) {
            throw new AuthenticationServiceException("", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication auth) {
        List<String> authorities = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        String jwtToken = jwtService.createJWTToken(auth.getName(), authorities);
        response.addHeader(jwtService.getConfiguration().getHeader(), jwtToken);
        // create user
    }

    private String obtainPhoneNumber(HttpServletRequest request) throws IOException {
        String phoneNumber = request.getParameter("phoneNumber");
        if (StringUtils.isEmpty(phoneNumber)) {
            UserCredentialsDto credentialsDto = new ObjectMapper().readValue(request.getInputStream(), UserCredentialsDto.class);
            phoneNumber = credentialsDto.getPhoneNumber();
        }
        return phoneNumber;
    }

    private String obtainOtp(HttpServletRequest request) throws IOException {
        String otp = request.getParameter("otp");
        if (StringUtils.isEmpty(otp)) {
            UserCredentialsDto credentialsDto = new ObjectMapper().readValue(request.getInputStream(), UserCredentialsDto.class);
            otp = credentialsDto.getOtp();
        }
        return otp;
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

    private static class UserCredentialsDto {
        private String phoneNumber, otp;

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
        public String getOtp() {
            return otp;
        }
        public void setOtp(String otp) {
            this.otp = otp;
        }
    }
}