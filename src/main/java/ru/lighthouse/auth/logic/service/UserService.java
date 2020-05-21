package ru.lighthouse.auth.logic.service;

import ru.lighthouse.auth.logic.entity.User;

import java.util.Optional;

public interface UserService {
    String createToken(String phoneNumber, String otp);
    void saveUser(User user);
    Optional<User> findById(Long id);
    Optional<User> findByToken(String token);
    Optional<User> findByPhoneNumber(String phoneNumber);
}
