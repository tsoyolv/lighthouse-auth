package ru.lighthouse.auth.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.lighthouse.auth.service.SMSMessageService;

import static ru.lighthouse.auth.rest.controller.AuthUtils.isValidPhoneNumber;

@RestController
public class AuthController {

    private static final String AUTH_PATH = "";
    private static final String OTP_PATH = "";

    private final SMSMessageService smsMessageService;

    public AuthController(SMSMessageService smsMessageService) {
        this.smsMessageService = smsMessageService;
    }

    @PostMapping("/otp/request")
    public HttpStatus getOtpRequest(@RequestParam String phoneNumber) {
        if (isValidPhoneNumber(phoneNumber)) {
            String password = generateOtpPassword(phoneNumber);
            String message = String.format("Ваш пароль от LightHouse: %s. С праздничком!", password);
            smsMessageService.sendSmsImmediately(phoneNumber, message, 0);
            return HttpStatus.OK;
        }
        return HttpStatus.BAD_REQUEST;
    }

    // TODO
    private String generateOtpPassword(String phoneNumber) {
        return "12345";
    }
}