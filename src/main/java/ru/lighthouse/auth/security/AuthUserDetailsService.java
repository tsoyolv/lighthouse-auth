package ru.lighthouse.auth.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;


public class AuthUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if ("tsoyolv".equals(username)) {
            return new User("tsoyolv", "", Collections.emptyList());
        }
        throw new UsernameNotFoundException(username);
    }
}
