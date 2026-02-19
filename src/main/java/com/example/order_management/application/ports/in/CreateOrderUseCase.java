package com.example.order_management.application.ports.in;

import com.example.order_management.application.ports.in.dto.CreateOrderCommand;
import com.example.order_management.application.ports.out.dto.OrderOutputDto;

/**
 * Input port for creating orders.
 */
public interface CreateOrderUseCase {

    /**
     * Creates a new order.
     * @param command The create order command
     * @return The created order as output DTO
     */
    OrderOutputDto createOrder(CreateOrderCommand command);
}
