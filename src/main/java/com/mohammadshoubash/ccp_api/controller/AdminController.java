package com.mohammadshoubash.ccp_api.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.mohammadshoubash.ccp_api.service.UserService;

import com.mohammadshoubash.ccp_api.dto.UserResponse;
import com.mohammadshoubash.ccp_api.entity.Role;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private UserService userService;
    public AdminController(UserService userService){
        this.userService = userService;
    }
    
    @GetMapping("/users")
    public List<UserResponse> getAllUsers(){
        return userService.findAll();
    }

    @PutMapping("/users/{id}/role")
    public UserResponse changeRole(@PathVariable Long id, @RequestBody Role role){
        return userService.changeRole(id, role);
    }
}
