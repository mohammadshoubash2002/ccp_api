package com.mohammadshoubash.ccp_api.dto;

public record WebhookEventPayload(
        String event,
        Long orderId,
        String previousStatus,
        String newStatus,
        String occurredAt) {
}