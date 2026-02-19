package com.example.order_management.application.ports.out.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Application output DTO for order (query result).
 */
public record OrderOutputDto(
    UUID orderId,
    UUID customerId,
    String status,
    List<OrderItemOutputDto> items,
    BigDecimal totalAmount,
    String currency,
    LocalDateTime createdAt
) {
    public record OrderItemOutputDto(
        UUID productId,
        int quantity,
        BigDecimal unitPrice
    ) {}
}
