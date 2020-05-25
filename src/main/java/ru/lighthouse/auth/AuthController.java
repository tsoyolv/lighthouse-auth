package ru.lighthouse.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.lighthouse.auth.otp.OtpService;

import java.util.regex.Pattern;

@RestController
public class AuthController {
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("7\\d{10}");

    private final OtpService otpService;

    public AuthController(OtpService otpService) {
        this.otpService = otpService;
    }

    @GetMapping("/api")
    public String api() {
        return "Api";
    }

    @PostMapping("/otp")
    public ResponseEntity<String> requestOtp(@RequestParam String phoneNumber) {
        if (isValidPhoneNumber(phoneNumber)) {
            otpService.createOtp(phoneNumber);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>("PHONE_NUMBER_INVALID", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches();
    }
}