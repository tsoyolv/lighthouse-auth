package ru.lighthouse.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static ru.lighthouse.auth.Uri.TEST_SERVICE_URI;

@RestController
@SpringBootApplication
@EnableFeignClients("ru.lighthouse.auth.integration")
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @GetMapping(TEST_SERVICE_URI)
    public String helloGradle() {
        return "Hello auth!";
    }
}