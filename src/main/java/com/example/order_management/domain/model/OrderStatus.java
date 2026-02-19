package com.example.order_management.domain.model;

/**
 * Represents the possible states of an Order.
 */
public enum OrderStatus {
    PENDING,
    PAID,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
