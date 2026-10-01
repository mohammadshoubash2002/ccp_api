package com.mohammadshoubash.ccp_api.dto;

import jakarta.validation.constraints.NotBlank;

public record WebhookSubscriptionRequest(
        @NotBlank String url, 
        @NotBlank String event) {
}