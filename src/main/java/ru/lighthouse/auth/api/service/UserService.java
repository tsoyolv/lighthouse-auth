package ru.lighthouse.auth.api.service;

import ru.lighthouse.auth.api.entity.User;

import java.util.Optional;

public interface UserService {
    void saveUser(User user);
    Optional<User> findById(Long id);
    Optional<User> findByPhoneNumber(String phoneNumber);
}
