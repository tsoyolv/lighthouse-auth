package ru.lighthouse.auth.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.lighthouse.auth.api.entity.User;
import ru.lighthouse.auth.api.service.OtpService;
import ru.lighthouse.auth.api.service.UserService;

import java.util.Optional;

import static ru.lighthouse.auth.message.AuthUtils.isValidPhoneNumber;

@RestController
public class AuthController {

    private final OtpService otpService;
    private final UserService userService;

    public AuthController(OtpService otpService, UserService userService) {
        this.otpService = otpService;
        this.userService = userService;
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

    @GetMapping(value = "/api/users/user/{id}",produces = "application/json")
    public User getUserDetail(@PathVariable Long id){
        Optional<User> userOptional = userService.findById(id);
        return userOptional.orElse(null);
    }
}