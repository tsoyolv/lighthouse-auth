package ru.lighthouse.auth.logic.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.lighthouse.auth.logic.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByPhonenumber(String username);

    Optional<User> findByToken(String token);
}
