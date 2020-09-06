package ru.lighthouse.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class App {
    public static final String HEALTH_URI = "/health-check";
    public static final String HEALTH_RESPONSE = "I AM AUTH! I AM FINE!";
    public static final String AUTH_CHECK = "Authorized data!";
    public static final String CHECK_AUTH_URI = "/check-auth";

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @GetMapping(HEALTH_URI)
    public String helloGradle() {
        return HEALTH_RESPONSE;
    }

    @GetMapping(CHECK_AUTH_URI)
    public String authCheck() {
        return AUTH_CHECK;
    }
}