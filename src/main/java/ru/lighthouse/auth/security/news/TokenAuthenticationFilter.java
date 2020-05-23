package ru.lighthouse.auth.security.news;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class TokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String SPLITTER = ":";

    private final String headerTypeName = "Basic";

    private boolean postOnly = true;

    protected TokenAuthenticationFilter(RequestMatcher requestMatcher) {
        super(requestMatcher);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        try {
            if (StringUtils.isEmpty(authorizationHeader)) {
                throw new BadCredentialsException("AuthorizationHeader must not be empty");
            }
            String encodedToken = StringUtils.removeStart(authorizationHeader, headerTypeName).trim();
            String token = new String(Base64.getDecoder().decode(encodedToken));
            String principal = obtainPrincipalFromToken(token);
            String credentials = obtainCredentialsFromToken(token);
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(principal, credentials);
            setDetails(request, authRequest);
            return getAuthenticationManager().authenticate(authRequest);
        } catch (Exception e) {
            throw new AuthenticationServiceException("Error", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
    }

    protected void setDetails(HttpServletRequest request,
                              UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    private String obtainPrincipalFromToken(String token) {
        String[] usernameOtp = token.split(SPLITTER);
        return usernameOtp[0];
    }

    private String obtainCredentialsFromToken(String token) {
        String[] usernameOtp = token.split(SPLITTER);
        return usernameOtp[1];
    }
}
