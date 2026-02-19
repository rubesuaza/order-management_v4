package com.example.order_management.infrastructure.adapters.in.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for order creation response (simplified).
 */
public record CreateOrderResponse(
    UUID orderId,
    String status,
    BigDecimal totalAmount,
    LocalDateTime createdAt
) {}
