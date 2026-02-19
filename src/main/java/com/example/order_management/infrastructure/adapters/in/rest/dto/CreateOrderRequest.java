package com.example.order_management.infrastructure.adapters.in.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * DTO for creating an order.
 */
public record CreateOrderRequest(
    @NotNull(message = "customerId is required")
    UUID customerId,
    
    @NotEmpty(message = "items list cannot be empty")
    @Valid
    List<OrderItemRequest> items
) {
    public record OrderItemRequest(
        @NotNull(message = "productId is required")
        UUID productId,
        
        @NotNull(message = "quantity is required")
        Integer quantity,
        
        @NotNull(message = "unitPrice is required")
        java.math.BigDecimal unitPrice
    ) {}
}
