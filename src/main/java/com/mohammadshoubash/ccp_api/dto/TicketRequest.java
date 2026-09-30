package com.mohammadshoubash.ccp_api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record TicketRequest(
        @Valid @NotNull Long customer_id,
        @NotNull String subject,
        @NotNull String status,
        @NotNull String priority) {
}