package com.mohammadshoubash.ccp_api.dto;

public record TicketEventPayload(
    Long ticketId,
    Long customerId,
    String status,
    String updatedAt) {
}
