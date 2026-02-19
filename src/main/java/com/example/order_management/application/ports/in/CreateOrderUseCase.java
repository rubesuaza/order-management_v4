package com.example.order_management.application.ports.in;

import com.example.order_management.domain.model.Order;

import java.util.List;
import java.util.UUID;

/**
 * Input port for creating orders.
 */
public interface CreateOrderUseCase {
    
    /**
     * Creates a new order.
     * @param customerId The customer ID
     * @param items The order items
     * @return The created order
     */
    Order createOrder(UUID customerId, List<OrderItemRequest> items);
    
    /**
     * Request DTO for order items.
     */
    record OrderItemRequest(
        UUID productId,
        int quantity,
        java.math.BigDecimal unitPrice,
        String currency
    ) {}
}
