package ru.lighthouse.auth.logic.service;

public interface OtpService {

    void createOtp(String phoneNumber);

    boolean checkOtp(String phoneNumber, String otp);
}
