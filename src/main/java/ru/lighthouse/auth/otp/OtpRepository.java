package ru.lighthouse.auth.otp;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends CrudRepository<Otp, Long> {
    Optional<Otp> findByPhoneNumberOtp(String phoneNumberOtp);
}
