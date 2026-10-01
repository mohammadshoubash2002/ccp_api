package com.mohammadshoubash.ccp_api.controller;

import com.mohammadshoubash.ccp_api.dto.RegisterRequest;
import com.mohammadshoubash.ccp_api.dto.UserResponse;
import com.mohammadshoubash.ccp_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return userService.register(request);
    }

}
