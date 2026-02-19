package com.example.order_management.application.services;

import com.example.order_management.application.ports.in.CreateOrderUseCase;
import com.example.order_management.application.ports.in.GetOrderUseCase;
import com.example.order_management.application.ports.in.PayOrderUseCase;
import com.example.order_management.application.ports.in.dto.CreateOrderCommand;
import com.example.order_management.application.ports.out.OrderRepositoryPort;
import com.example.order_management.application.ports.out.dto.OrderOutputDto;
import com.example.order_management.domain.exception.InvalidOrderDataException;
import com.example.order_management.domain.exception.OrderNotFoundException;
import com.example.order_management.domain.model.Money;
import com.example.order_management.domain.model.Order;
import com.example.order_management.domain.model.OrderItem;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Application service implementing use cases for order management.
 * Framework-agnostic; no Spring annotations. Wired and transaction management
 * are handled by the infrastructure layer (OrderServiceAdapter).
 */
public class OrderService implements CreateOrderUseCase, GetOrderUseCase, PayOrderUseCase {

    private final OrderRepositoryPort orderRepository;

    public OrderService(OrderRepositoryPort orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderOutputDto createOrder(CreateOrderCommand command) {
        if (command.items() == null || command.items().isEmpty()) {
            throw new InvalidOrderDataException("Order must have at least one item");
        }
        List<OrderItem> domainItems = command.items().stream()
            .map(item -> new OrderItem(
                item.productId(),
                item.quantity(),
                new Money(item.unitPrice(), item.currency())
            ))
            .toList();

        Order order = new Order(command.customerId(), domainItems);
        Order saved = orderRepository.save(order);
        return toOutputDto(saved);
    }

    @Override
    public Optional<OrderOutputDto> getOrderById(UUID orderId) {
        return orderRepository.findById(orderId).map(this::toOutputDto);
    }

    @Override
    public OrderOutputDto payOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.pay();
        Order saved = orderRepository.save(order);
        return toOutputDto(saved);
    }

    private OrderOutputDto toOutputDto(Order order) {
        List<OrderOutputDto.OrderItemOutputDto> items = order.getItems().stream()
            .map(item -> new OrderOutputDto.OrderItemOutputDto(
                item.getProductId(),
                item.getQuantity(),
                item.getUnitPrice().getAmount()
            ))
            .toList();
        return new OrderOutputDto(
            order.getId(),
            order.getCustomerId(),
            order.getStatus().name(),
            items,
            order.getTotalAmount().getAmount(),
            order.getTotalAmount().getCurrency(),
            order.getCreatedAt()
        );
    }
}
