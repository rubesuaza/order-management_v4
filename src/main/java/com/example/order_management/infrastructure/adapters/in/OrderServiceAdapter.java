package com.example.order_management.infrastructure.adapters.in;

import com.example.order_management.application.ports.in.CreateOrderUseCase;
import com.example.order_management.application.ports.in.GetOrderUseCase;
import com.example.order_management.application.ports.in.PayOrderUseCase;
import com.example.order_management.application.ports.in.dto.CreateOrderCommand;
import com.example.order_management.application.ports.out.OrderRepositoryPort;
import com.example.order_management.application.ports.out.dto.OrderOutputDto;
import com.example.order_management.application.services.OrderService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Infrastructure adapter that wraps the application OrderService and applies
 * transaction management. Keeps the application layer framework-agnostic.
 */
@Component
@Transactional
public class OrderServiceAdapter implements CreateOrderUseCase, GetOrderUseCase, PayOrderUseCase {

    private final OrderService orderService;

    public OrderServiceAdapter(OrderRepositoryPort orderRepository) {
        this.orderService = new OrderService(orderRepository);
    }

    @Override
    public OrderOutputDto createOrder(CreateOrderCommand command) {
        return orderService.createOrder(command);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderOutputDto> getOrderById(UUID orderId) {
        return orderService.getOrderById(orderId);
    }

    @Override
    public OrderOutputDto payOrder(UUID orderId) {
        return orderService.payOrder(orderId);
    }
}
