package com.mohammadshoubash.ccp_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerRequest(
    @NotBlank(message = "Name is required")
    String name,

    @Email(message = "Email is required and should be valid")
    @NotBlank(message = "Email is required")
    String email,

    @NotBlank(message = "Phone is required")
    String phone
) {
}