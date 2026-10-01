package com.mohammadshoubash.ccp_api.dto;

import java.math.BigDecimal;
import com.mohammadshoubash.ccp_api.entity.OrderStatus;
import com.mohammadshoubash.ccp_api.entity.Order;

public record OrderResponse(
    Long id,
    Long customer_id,
    BigDecimal total,
    OrderStatus status
) {
    public OrderResponse (Order order) {
        this(
            order.getId(),
            order.getCustomer().getId(),
            order.getTotal(),
            order.getStatus()
        );
    }
}
