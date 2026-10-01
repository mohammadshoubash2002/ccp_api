package com.mohammadshoubash.ccp_api.dto;

import com.mohammadshoubash.ccp_api.entity.AppUser;

public record UserResponse(Long id, String username, String role, boolean enabled) {
    public static UserResponse from(AppUser user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getRole().name(), user.isEnabled());
    }
}
