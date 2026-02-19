package com.example.order_management.application.ports.in;

import com.example.order_management.domain.model.Order;

import java.util.UUID;

/**
 * Input port for paying orders.
 */
public interface PayOrderUseCase {
    
    /**
     * Pays an order, changing its status to PAID.
     * @param orderId The order ID
     * @return The paid order
     * @throws com.example.order_management.domain.exception.InvalidOrderStateException if the order cannot be paid
     */
    Order payOrder(UUID orderId);
}
