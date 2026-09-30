package com.mohammadshoubash.ccp_api.dto;

public record CustomerResponse(
    Long id,
    String name,
    String email,
    String phone,
    String createdAt,
    String updatedAt
) {}
