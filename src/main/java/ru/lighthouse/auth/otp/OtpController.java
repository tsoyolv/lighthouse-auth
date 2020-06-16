package ru.lighthouse.auth.otp;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.lighthouse.auth.otp.logic.OtpService;

import java.util.regex.Pattern;

@RestController
@RequiredArgsConstructor
public class OtpController {
    private final OtpService otpService;
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("7\\d{10}");

    @PostMapping("${otp.uri}")
    public ResponseEntity<String> requestOtp(@RequestParam String phoneNumber) {
        if (isValidPhoneNumber(phoneNumber)) {
            otpService.createAndSendOtp(phoneNumber);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>("PHONE_NUMBER_INVALID", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches();
    }
}