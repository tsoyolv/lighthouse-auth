package ru.lighthouse.auth.logic.service;

import org.springframework.stereotype.Service;
import ru.lighthouse.auth.logic.entity.User;
import ru.lighthouse.auth.logic.repository.UserRepository;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String createToken(String phoneNumber, String otp) {
        String rawToken = phoneNumber + otp;
        return rawToken;
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByToken(String token) {
        return userRepository.findByToken(token);
    }

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhonenumber(phoneNumber);
    }

    @Override
    public void authenticate(String phoneNumber, String token) {
        Optional<User> userOptional = findByPhoneNumber(phoneNumber);
        User user = userOptional.orElse(new User(phoneNumber, token));
        user.setToken(token);
        saveUser(user);
    }
}
