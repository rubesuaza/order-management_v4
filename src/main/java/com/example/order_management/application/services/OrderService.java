package com.example.order_management.application.services;

import com.example.order_management.application.ports.in.CreateOrderUseCase;
import com.example.order_management.application.ports.in.GetOrderUseCase;
import com.example.order_management.application.ports.in.PayOrderUseCase;
import com.example.order_management.application.ports.out.OrderRepositoryPort;
import com.example.order_management.domain.model.Money;
import com.example.order_management.domain.model.Order;
import com.example.order_management.domain.model.OrderItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Application service implementing use cases for order management.
 */
@Service
@Transactional
public class OrderService implements CreateOrderUseCase, GetOrderUseCase, PayOrderUseCase {
    
    private final OrderRepositoryPort orderRepository;
    
    public OrderService(OrderRepositoryPort orderRepository) {
        this.orderRepository = orderRepository;
    }
    
    @Override
    public Order createOrder(UUID customerId, List<CreateOrderUseCase.OrderItemRequest> items) {
        List<OrderItem> domainItems = items.stream()
            .map(item -> new OrderItem(
                item.productId(),
                item.quantity(),
                new Money(item.unitPrice(), item.currency())
            ))
            .collect(Collectors.toList());
        
        Order order = new Order(customerId, domainItems);
        return orderRepository.save(order);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(UUID orderId) {
        return orderRepository.findById(orderId);
    }
    
    @Override
    public Order payOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        
        order.pay();
        return orderRepository.save(order);
    }
}
