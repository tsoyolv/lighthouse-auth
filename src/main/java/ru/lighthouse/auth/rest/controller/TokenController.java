package ru.lighthouse.auth.rest.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.lighthouse.auth.logic.entity.User;
import ru.lighthouse.auth.logic.service.DefaultUserService;

@RestController
public class TokenController {

    private final DefaultUserService userService;

    public TokenController(DefaultUserService userService) {
        this.userService = userService;
    }


    @GetMapping(value = "/api/users/user/{id}",produces = "application/json")
    public User getUserDetail(@PathVariable Long id){
        return userService.findById(id);
    }

    @PostMapping("/token")
    public String getToken(@RequestParam("username") final String username, @RequestParam("password") final String password){
        String token= userService.auth(username,password);
        if(StringUtils.isEmpty(token)){
            return "no token found";
        }
        return token;
    }
}