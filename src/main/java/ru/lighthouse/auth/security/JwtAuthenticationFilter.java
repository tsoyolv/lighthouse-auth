package ru.lighthouse.auth.security;


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
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

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
            String phoneNumber = obtainRequestParameter(request, "phoneNumber");
            String otp = obtainRequestParameter(request, "otp");
            List<GrantedAuthority> authorities = obtainAuthorities(request);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(phoneNumber, otp, authorities);
            return authManager.authenticate(authToken);
        } catch (Exception e) {
            throw new AuthenticationServiceException("Error in authentication", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication auth) {
        List<String> authorities = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        String jwtToken = jwtService.createJWTToken(auth.getName(), authorities, auth.getDetails());
        response.addHeader(jwtService.getHeader(), jwtToken);
    }

    private List<GrantedAuthority> obtainAuthorities(HttpServletRequest request) {
        String role = obtainRequestParameter(request, "role");
        if (StringUtils.isEmpty(role)) {
            return createAuthorityList(DefaultAuthority.ROLE_IOS.name());
        }
        return createAuthorityList(DefaultAuthority.valueOf(role).name());
    }

    private String obtainRequestParameter(HttpServletRequest request, String paramName) {
        return request.getParameter(paramName);
    }

    public enum DefaultAuthority {
        ROLE_IOS("IOS пользователь"), ROLE_ANDROID("Андроид пользователь"), ROLE_WEB("WEB пользователь");
        private String desc;
        DefaultAuthority(String desc) {
            this.desc = desc;
        }
        public String getDesc() {
            return desc;
        }
    }
}