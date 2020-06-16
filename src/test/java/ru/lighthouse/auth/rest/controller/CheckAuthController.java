package ru.lighthouse.auth.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CheckAuthController {
    public static final String AUTH_CHECK = "Authorized data!";
    public static final String CHECK_AUTH_URI = "/check-auth";

    @GetMapping(CHECK_AUTH_URI)
    public String authCheck() {
        return AUTH_CHECK;
    }
}
