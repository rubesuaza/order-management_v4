package com.example.order_management.application.ports.in;

import com.example.order_management.application.ports.out.dto.OrderOutputDto;

import java.util.Optional;
import java.util.UUID;

/**
 * Input port for retrieving orders.
 */
public interface GetOrderUseCase {

    /**
     * Gets an order by ID.
     * @param orderId The order ID
     * @return Optional containing the order output DTO if found
     */
    Optional<OrderOutputDto> getOrderById(UUID orderId);
}
