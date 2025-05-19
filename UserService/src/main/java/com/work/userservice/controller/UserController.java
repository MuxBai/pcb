package com.work.userservice.controller;

import com.work.userservice.dto.UserGetDTO;
import com.work.userservice.dto.UserLoginDTO;
import com.work.userservice.entity.Users;
import com.work.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/getUserById/{id}")
    public UserGetDTO getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }

    @PostMapping("/login")
    public UserLoginDTO login(@RequestBody Map<String,String> request) {
        return userService.login(request.get("id"), request.get("password"));
    }

    @PostMapping("/register")
    public Boolean register(@RequestBody Users users) {
        return userService.register(users);
    }
}
