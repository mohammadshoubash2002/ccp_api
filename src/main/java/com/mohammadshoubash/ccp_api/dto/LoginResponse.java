package com.mohammadshoubash.ccp_api.dto;

public record LoginResponse(String token, String type, long expiresInMs) {
}
