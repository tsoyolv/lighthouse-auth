package ru.lighthouse.auth.otp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.lighthouse.auth.otp.Otp;
import ru.lighthouse.auth.otp.OtpService;

import java.util.List;
import java.util.regex.Pattern;

import static ru.lighthouse.auth.Uri.CHECK_AUTH_URI;
import static ru.lighthouse.auth.Uri.OTP_URI;
import static ru.lighthouse.auth.Uri.OTP_VIEW_URI;

@RestController
public class OtpController {
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("7\\d{10}");

    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @GetMapping(CHECK_AUTH_URI)
    public String api() {
        return "Api";
    }

    @PostMapping(OTP_URI)
    public ResponseEntity<String> requestOtp(@RequestParam String phoneNumber) {
        if (isValidPhoneNumber(phoneNumber)) {
            otpService.createAndSendOtp(phoneNumber);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>("PHONE_NUMBER_INVALID", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @GetMapping(OTP_VIEW_URI)
    public List<Otp> getOtps() {
        return otpService.getAll();
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches();
    }
}