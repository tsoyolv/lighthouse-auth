package ru.lighthouse.auth.api.service;

public interface OtpService {
    void createOtp(String phoneNumber);
    boolean isOtpValid(String phoneNumber, String otp);
    boolean isOtpNotValid(String phoneNumber, String otp);
}
