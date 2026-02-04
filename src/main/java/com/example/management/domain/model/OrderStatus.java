package com.example.management.domain.model;

/**
 * Estado del pedido en el flujo de gestión.
 */
public enum OrderStatus {
    DRAFT,
    CONFIRMED,
    SHIPPED,
    CANCELLED
}
