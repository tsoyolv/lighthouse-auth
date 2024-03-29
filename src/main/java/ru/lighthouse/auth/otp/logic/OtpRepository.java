package ru.lighthouse.auth.otp.logic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.stream.Stream;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    Stream<Otp> findAllByPhoneNumberOtp(String phoneNumberOtp);
    @Transactional
    void deleteByPhoneNumberOtpIsStartingWith(String phoneNumberOtp);
}
