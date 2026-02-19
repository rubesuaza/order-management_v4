package com.example.order_management.domain.exception;

import java.util.UUID;

/**
 * Thrown when an order is not found by ID.
 */
public class OrderNotFoundException extends DomainException {

    public OrderNotFoundException(UUID orderId) {
        super("Order not found: " + orderId);
    }

    public OrderNotFoundException(String message) {
        super(message);
    }
}
