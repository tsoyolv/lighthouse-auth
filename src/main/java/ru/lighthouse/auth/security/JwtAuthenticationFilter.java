package ru.lighthouse.auth.security;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final AuthenticationManager authManager;
    private final JWTService jwtService;

    public JwtAuthenticationFilter(AuthenticationManager authManager, JWTService jwtService) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(jwtService.getAuthUri(), HttpMethod.POST.name()));
    }

    public interface RequestParams {
        String OTP = "otp";
        String PHONE_NUMBER = "phoneNumber";
        String ROLE = "role";
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            String phoneNumber = obtainRequestParameter(request, RequestParams.PHONE_NUMBER);
            String otp = obtainRequestParameter(request, RequestParams.OTP);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(phoneNumber, otp);
            authToken.setDetails(addUserAgentToDetails(request));
            return authManager.authenticate(authToken);
        } catch (Exception e) {
            logger.error("Error in authentication: {}", e.getMessage());
            throw new AuthenticationServiceException("Error in authentication", e);
        }
    }

    private Object addUserAgentToDetails(HttpServletRequest request) {
        LinkedHashMap<String, Object> details = new LinkedHashMap<>();
        details.put(HttpHeaders.USER_AGENT, request.getHeader(HttpHeaders.USER_AGENT));
        return details;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication auth) {
        String jwtToken = jwtService.createJWTToken(auth.getName(), auth.getAuthorities(), auth.getDetails());
        response.addHeader(jwtService.getHeader(), jwtToken);
    }

    private String obtainRequestParameter(HttpServletRequest request, String paramName) {
        return request.getParameter(paramName);
    }
}