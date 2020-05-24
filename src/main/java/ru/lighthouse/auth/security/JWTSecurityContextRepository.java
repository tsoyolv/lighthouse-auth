package ru.lighthouse.auth.security;


import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SaveContextOnUpdateOrErrorResponseWrapper;
import org.springframework.security.web.context.SecurityContextRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

import static ru.lighthouse.auth.security.JWTConfiguration.AUTHORITIES_CLAIM_NAME;

public class JWTSecurityContextRepository implements SecurityContextRepository {

    private final JWTService jwtService;
    private final JWTConfiguration jwtConfiguration;

    public JWTSecurityContextRepository(JWTService jwtService) {
        this.jwtService = jwtService;
        this.jwtConfiguration = jwtService.getConfiguration();
    }

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        HttpServletRequest request = requestResponseHolder.getRequest();
        HttpServletResponse response = requestResponseHolder.getResponse();
        requestResponseHolder.setResponse(new SaveToAuthorizationHeaderResponseWrapper(response));
        return readContextFromRequest(request);
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        SaveToAuthorizationHeaderResponseWrapper responseWrapper = (SaveToAuthorizationHeaderResponseWrapper) response;
        if (!responseWrapper.isContextSaved()) {
            responseWrapper.saveContext(context);
        }
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        String header = request.getHeader(jwtConfiguration.getHeader());
        return header != null && header.startsWith(jwtConfiguration.getPrefix());
    }

    private SecurityContext readContextFromRequest(HttpServletRequest request) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        try {
            String header = request.getHeader(jwtConfiguration.getHeader());
            String token = header.replace(jwtConfiguration.getPrefix(), "");
            Claims claims = jwtService.validateAndGetClaims(token);
            String phoneNumber = claims.getSubject();
            if (phoneNumber != null) {
                @SuppressWarnings("unchecked")
                List<String> authorities = (List<String>) claims.get(AUTHORITIES_CLAIM_NAME);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(phoneNumber, null, authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
                context.setAuthentication(auth);
            }
        } catch (Exception e) {
            // log something
            return context;
        }
        return context;
    }

    private class SaveToAuthorizationHeaderResponseWrapper extends SaveContextOnUpdateOrErrorResponseWrapper {
        private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

        public SaveToAuthorizationHeaderResponseWrapper(HttpServletResponse response) {
            super(response, true);
        }

        @Override
        protected void saveContext(SecurityContext context) {
            HttpServletResponse response = (HttpServletResponse) getResponse();
            Authentication auth = context.getAuthentication();
            if (auth == null || trustResolver.isAnonymous(auth)) {
                return;
            }
            List<String> authorities = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
            String jwtToken = jwtService.createJWTToken(auth.getName(), authorities);
            response.addHeader(jwtService.getConfiguration().getHeader(), jwtToken);
        }
    }
}