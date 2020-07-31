package ru.lighthouse.auth.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authManager;
    private final JWTService jwtService;

    public JwtAuthenticationFilter(AuthenticationManager authManager, JWTService jwtService) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(jwtService.getAuthUri(), HttpMethod.POST.name()));
        this.setAuthenticationFailureHandler(new AuthenticationFailureHandler());
    }

    public interface RequestParams {
        String OTP = "otp";
        String PHONE_NUMBER = "phoneNumber";
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String phoneNumber = obtainRequestParameter(request, RequestParams.PHONE_NUMBER);
        String otp = obtainRequestParameter(request, RequestParams.OTP);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(phoneNumber, otp);
        authToken.setDetails(addUserAgentToDetails(request));
        return authManager.authenticate(authToken);
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

    private static class AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
            Map<String, Object> data = new HashMap<>();
            data.put("timestamp", Calendar.getInstance().getTime());
            data.put("status", HttpStatus.UNAUTHORIZED.value());
            data.put("error", exception.getMessage());
            response.getOutputStream().println(objectMapper.writeValueAsString(data));
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
    }
}