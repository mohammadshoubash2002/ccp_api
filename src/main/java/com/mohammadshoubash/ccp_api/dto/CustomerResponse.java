package com.mohammadshoubash.ccp_api.dto;

import com.mohammadshoubash.ccp_api.entity.AppUser;

public record CustomerResponse(
    Long id,
    String name,
    String email,
    String phone,
    String createdAt,
    String updatedAt
) {

    public CustomerResponse(AppUser user) {
        this(user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getCreatedAt(), user.getUpdatedAt());
    }
}
