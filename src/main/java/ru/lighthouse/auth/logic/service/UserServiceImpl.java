package ru.lighthouse.auth.logic.service;

import org.springframework.stereotype.Service;
import ru.lighthouse.auth.logic.entity.User;
import ru.lighthouse.auth.logic.repository.UserRepository;

import java.util.Base64;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String createToken(String phoneNumber, String otp) {
        return phoneNumber + otp;
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
}
