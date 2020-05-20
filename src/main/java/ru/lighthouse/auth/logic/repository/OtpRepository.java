package ru.lighthouse.auth.logic.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.lighthouse.auth.logic.entity.Otp;

import java.util.Optional;

@Repository
public interface OtpRepository extends CrudRepository<Otp, Long> {
    Optional<Otp> findByPhoneNumberOtp(String phoneNumberOtp);
}
