package com.example.management.application.services;

import com.example.management.application.ports.in.CreateOrderUseCase;
import com.example.management.application.ports.in.GetOrderUseCase;
import com.example.management.application.ports.out.OrderRepository;
import com.example.management.domain.model.Order;
import com.example.management.domain.model.OrderLine;
import com.example.management.domain.model.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio de aplicación que orquesta el dominio y los puertos de salida.
 * Implementa los casos de uso de entrada (CreateOrder, GetOrder).
 */
@Service
public class OrderApplicationService implements CreateOrderUseCase, GetOrderUseCase {

    private final OrderRepository orderRepository;

    public OrderApplicationService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order create(CreateOrderUseCase.CreateOrderCommand command) {
        Order order = buildOrderFromCommand(command);
        return orderRepository.save(order);
    }

    private Order buildOrderFromCommand(CreateOrderUseCase.CreateOrderCommand command) {
        List<OrderLine> lines = command.lines().stream()
                .map(dto -> new OrderLine(dto.productId(), dto.quantity(), dto.unitPrice()))
                .toList();
        return new Order(UUID.randomUUID(), command.customerId(), OrderStatus.DRAFT, lines);
    }

    @Override
    public Optional<Order> getById(UUID id) {
        return orderRepository.findById(id);
    }
}
