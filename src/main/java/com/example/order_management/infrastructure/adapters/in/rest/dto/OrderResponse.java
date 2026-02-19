package com.example.order_management.infrastructure.adapters.in.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for order responses.
 */
public record OrderResponse(
    UUID orderId,
    UUID customerId,
    String status,
    List<OrderItemResponse> items,
    BigDecimal totalAmount,
    String currency,
    LocalDateTime createdAt
) {
    public record OrderItemResponse(
        UUID productId,
        Integer quantity,
        BigDecimal unitPrice
    ) {}
}
