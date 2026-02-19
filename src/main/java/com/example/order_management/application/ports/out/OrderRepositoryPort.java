package com.example.order_management.application.ports.out;

import com.example.order_management.domain.model.Order;

import java.util.Optional;
import java.util.UUID;

/**
 * Port for persisting and retrieving Order aggregates.
 * This is an output port (driven by the application).
 */
public interface OrderRepositoryPort {
    
    /**
     * Saves an order aggregate.
     * @param order The order to save
     * @return The saved order
     */
    Order save(Order order);
    
    /**
     * Finds an order by its ID.
     * @param orderId The order ID
     * @return Optional containing the order if found
     */
    Optional<Order> findById(UUID orderId);
    
    /**
     * Checks if an order exists by ID.
     * @param orderId The order ID
     * @return true if the order exists
     */
    boolean existsById(UUID orderId);
}
