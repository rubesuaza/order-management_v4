package com.example.order_management.application.ports.in.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Application command DTO for creating an order.
 */
public record CreateOrderCommand(
    UUID customerId,
    List<CreateOrderItemCommand> items
) {
    public record CreateOrderItemCommand(
        UUID productId,
        int quantity,
        BigDecimal unitPrice,
        String currency
    ) {}
}
