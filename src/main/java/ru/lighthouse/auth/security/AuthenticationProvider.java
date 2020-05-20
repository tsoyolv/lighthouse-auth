package ru.lighthouse.auth.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.lighthouse.auth.logic.entity.User;
import ru.lighthouse.auth.logic.service.UserService;

import java.util.Base64;
import java.util.Optional;

@Component
public class AuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private final UserService userService;

    public AuthenticationProvider(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
        //
    }

    @Override
    protected UserDetails retrieveUser(String userName, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
        Object token = usernamePasswordAuthenticationToken.getCredentials();
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(String.valueOf(token));
            String decodedToken = new String(decodedBytes);
            Optional<User> byToken = userService.findByToken(decodedToken);
            return byToken.map(u ->
                    new org.springframework.security.core.userdetails.User(
                            u.getPhonenumber(),
                            u.getToken(),
                            AuthorityUtils.createAuthorityList("USER"))).orElseThrow();
        } catch (Exception e) {
            throw new UsernameNotFoundException("Cannot find user with authentication token=" + token);
        }
    }
}