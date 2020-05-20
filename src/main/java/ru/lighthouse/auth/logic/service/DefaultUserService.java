package ru.lighthouse.auth.logic.service;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;
import ru.lighthouse.auth.logic.entity.User;
import ru.lighthouse.auth.logic.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service("userService")
public class DefaultUserService {

    final UserRepository userRepository;

    public DefaultUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String auth(String username, String password) {
        Optional<User> customer = userRepository.auth(username);
        if(customer.isPresent()){
            String token = UUID.randomUUID().toString();
            User custom = customer.get();
            custom.setToken(token);
            userRepository.save(custom);
            return token;
        }
        return "";
    }

    public Optional<org.springframework.security.core.userdetails.User> findByToken(String token) {
        Optional<User> user= userRepository.findByToken(token);
        if(user.isPresent()){
            User user1 = user.get();
            org.springframework.security.core.userdetails.User userD = new org.springframework.security.core.userdetails.User (user1.getName(), user1.getPhonenumber(), AuthorityUtils.createAuthorityList("USER"));
            return Optional.of(userD);
        }
        return  Optional.empty();
    }
}