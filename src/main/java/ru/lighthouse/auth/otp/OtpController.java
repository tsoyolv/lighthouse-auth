package ru.lighthouse.auth.otp;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.lighthouse.auth.otp.logic.OtpService;

@RestController
@RequiredArgsConstructor
public class OtpController {
    private final OtpService otpService;

    @PostMapping("${otp.uri}")
    public ResponseEntity<String> requestOtp(@RequestParam String phoneNumber) {
        if (otpService.isValidPhoneNumber(phoneNumber)) {
            otpService.createAndSendOtp(phoneNumber);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>("PHONE_NUMBER_INVALID", HttpStatus.UNPROCESSABLE_ENTITY);
    }
}