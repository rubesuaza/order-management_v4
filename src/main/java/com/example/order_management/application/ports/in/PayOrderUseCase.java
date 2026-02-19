package com.example.order_management.application.ports.in;

import com.example.order_management.application.ports.out.dto.OrderOutputDto;

import java.util.UUID;

/**
 * Input port for paying orders.
 */
public interface PayOrderUseCase {

    /**
     * Pays an order, changing its status to PAID.
     * @param orderId The order ID
     * @return The paid order as output DTO
     * @throws com.example.order_management.domain.exception.InvalidOrderStateException if the order cannot be paid
     * @throws com.example.order_management.domain.exception.OrderNotFoundException if the order is not found
     */
    OrderOutputDto payOrder(UUID orderId);
}
