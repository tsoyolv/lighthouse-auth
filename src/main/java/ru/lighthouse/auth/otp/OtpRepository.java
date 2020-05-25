package ru.lighthouse.auth.otp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByPhoneNumberOtp(String phoneNumberOtp);
    @Transactional
    void deleteByPhoneNumberOtpIsStartingWith(String phoneNumberOtp);
}
