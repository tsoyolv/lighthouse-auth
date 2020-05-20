package ru.lighthouse.auth.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.lighthouse.auth.logic.entity.User;
import ru.lighthouse.auth.logic.service.OtpService;
import ru.lighthouse.auth.logic.service.UserService;

import java.util.Base64;
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

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String phoneNumber, @RequestParam String otp) {
        boolean valid = otpService.checkOtp(phoneNumber, otp);
        if (valid) {
            String token = userService.createToken(phoneNumber, otp);
            User newUser = new User(phoneNumber, token);
            userService.saveUser(newUser);
            String tokenBase64 = Base64.getEncoder().encodeToString(token.getBytes());
            return new ResponseEntity<>(tokenBase64, HttpStatus.OK);
        }
        return new ResponseEntity<>("SMS_CODE_INVALID", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @GetMapping(value = "/api/users/user/{id}",produces = "application/json")
    public User getUserDetail(@PathVariable Long id){
        Optional<User> userOptional = userService.findById(id);
        return userOptional.orElse(null);
    }
}