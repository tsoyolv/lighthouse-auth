package ru.lighthouse.auth.otp;

public interface OtpService {
    void createAndSendOtp(String phoneNumber);
    boolean isOtpValid(String phoneNumber, String otp);
    boolean isOtpNotValid(String phoneNumber, String otp);
}
