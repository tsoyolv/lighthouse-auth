package ru.lighthouse.auth.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authManager;
    private final JWTService jwtService;

    public JwtAuthenticationFilter(AuthenticationManager authManager, JWTService jwtService) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(jwtService.getAuthUri(), HttpMethod.POST.name()));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            String phoneNumber = obtainPhoneNumber(request);
            String otp = obtainOtp(request);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(phoneNumber, otp);
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
        response.addHeader(jwtService.getHeader(), jwtToken);
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