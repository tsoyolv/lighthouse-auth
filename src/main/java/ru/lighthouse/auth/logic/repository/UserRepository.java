package ru.lighthouse.auth.logic.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.lighthouse.auth.logic.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    @Query(value = "SELECT u FROM User u where u.phonenumber = ?1")
    Optional<User> auth(String username);

    Optional<User> findByToken(String token);
}
