package ru.lighthouse.auth.security.news;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.lighthouse.auth.logic.entity.User;
import ru.lighthouse.auth.logic.repository.UserRepository;

import java.util.Optional;

@Service("userDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        try {
            Optional<User> byPhoneNumber = userRepository.findByPhonenumber(phoneNumber);
            return byPhoneNumber.map(u ->
                    new org.springframework.security.core.userdetails.User(
                            u.getPhonenumber(),
                            u.getToken(),
                            AuthorityUtils.createAuthorityList("USER"))).orElse(null);
        } catch (Exception e) {
            throw new UsernameNotFoundException("Cannot find user with authentication token=" + phoneNumber);
        }
    }
}
