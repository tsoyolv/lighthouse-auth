package ru.lighthouse.auth.security;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.lighthouse.auth.api.service.UserService;

import java.util.List;
import java.util.Optional;

import static ru.lighthouse.auth.security.UserRole.IOS;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        Optional<ru.lighthouse.auth.api.entity.User> userOptional = userService.findByPhoneNumber(phoneNumber);
        if (userOptional.isPresent()) {
            return convertUserToUserDetails(userOptional.get());
        }
        throw new UsernameNotFoundException("Username: " + phoneNumber + " not found");
    }

    private UserDetails convertUserToUserDetails(ru.lighthouse.auth.api.entity.User user) {
        String phoneNumber = user.getPhoneNumber();
        String lastOtp = user.getLastOtp();
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(IOS.getSpringRole());
        return new User(phoneNumber, lastOtp, grantedAuthorities);
    }
}