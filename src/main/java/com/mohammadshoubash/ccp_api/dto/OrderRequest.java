package com.mohammadshoubash.ccp_api.dto;

import java.math.BigDecimal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record OrderRequest(
        @Valid @NotNull Long customer_id,
        @Valid @NotNull String status,
        @Valid @NotNull BigDecimal total) {
}