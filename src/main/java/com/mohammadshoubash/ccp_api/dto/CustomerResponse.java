package com.mohammadshoubash.ccp_api.dto;

import com.mohammadshoubash.ccp_api.entity.Customer;

public record CustomerResponse(
    Long id,
    String name,
    String email,
    String phone,
    String createdAt,
    String updatedAt
) {

    public CustomerResponse(Customer customer) {
        this(customer.getId(), customer.getName(), customer.getEmail(), customer.getPhone(), customer.getCreatedAt(), customer.getUpdatedAt());
    }
}
