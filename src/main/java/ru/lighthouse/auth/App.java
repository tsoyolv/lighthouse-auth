package ru.lighthouse.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Value("${service.instance.id}")
    private int instanceId;

    @GetMapping("/instance-id")
    public Integer getServiceInstanceId() {
        return instanceId;
    }

    @GetMapping("/testservice")
    public String helloGradle() {
        return "Hello auth!";
    }
}