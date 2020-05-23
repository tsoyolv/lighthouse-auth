package ru.lighthouse.auth.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.lighthouse.auth.api.entity.Otp;

import java.util.Optional;

@Repository
public interface OtpRepository extends CrudRepository<Otp, Long> {
    Optional<Otp> findByPhoneNumberOtp(String phoneNumberOtp);
}
