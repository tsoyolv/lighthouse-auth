package ru.lighthouse.auth.otp;

import java.util.List;

public interface OtpService {
    void createAndSendOtp(String phoneNumber);
    boolean isOtpValid(String phoneNumber, String otp);
    boolean isOtpNotValid(String phoneNumber, String otp);
    List<Otp> getAll();
}
