package com.example.order_management.application.services;

import com.example.order_management.application.ports.in.CreateOrderUseCase;
import com.example.order_management.application.ports.out.OrderRepositoryPort;
import com.example.order_management.domain.exception.InvalidOrderStateException;
import com.example.order_management.domain.model.Money;
import com.example.order_management.domain.model.Order;
import com.example.order_management.domain.model.OrderItem;
import com.example.order_management.domain.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderService.
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepositoryPort orderRepository;

    @InjectMocks
    private OrderService orderService;

    private UUID customerId;
    private UUID productId1;
    private UUID productId2;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        productId1 = UUID.randomUUID();
        productId2 = UUID.randomUUID();
    }

    @Test
    void shouldCreateOrderWithValidItems() {
        // Arrange
        List<CreateOrderUseCase.OrderItemRequest> itemRequests = List.of(
            new CreateOrderUseCase.OrderItemRequest(productId1, 2, new BigDecimal("25.00"), "USD")
        );

        Order savedOrder = new Order(customerId, List.of(
            new OrderItem(productId1, 2, new Money(new BigDecimal("25.00"), "USD"))
        ));

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // Act
        Order result = orderService.createOrder(customerId, itemRequests);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getCustomerId()).isEqualTo(customerId);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(result.getItems()).hasSize(1);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void shouldCreateOrderWithMultipleItems() {
        // Arrange
        List<CreateOrderUseCase.OrderItemRequest> itemRequests = List.of(
            new CreateOrderUseCase.OrderItemRequest(productId1, 2, new BigDecimal("25.00"), "USD"),
            new CreateOrderUseCase.OrderItemRequest(productId2, 3, new BigDecimal("10.00"), "USD")
        );

        Order savedOrder = new Order(customerId, List.of(
            new OrderItem(productId1, 2, new Money(new BigDecimal("25.00"), "USD")),
            new OrderItem(productId2, 3, new Money(new BigDecimal("10.00"), "USD"))
        ));

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // Act
        Order result = orderService.createOrder(customerId, itemRequests);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(2);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void shouldGetOrderByIdWhenOrderExists() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Order order = new Order(customerId, List.of(
            new OrderItem(productId1, 2, new Money(new BigDecimal("25.00"), "USD"))
        ));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        Optional<Order> result = orderService.getOrderById(orderId);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(order);
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void shouldReturnEmptyWhenOrderNotFound() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act
        Optional<Order> result = orderService.getOrderById(orderId);

        // Assert
        assertThat(result).isEmpty();
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void shouldPayOrderWhenOrderExistsAndIsPending() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Order order = new Order(customerId, List.of(
            new OrderItem(productId1, 2, new Money(new BigDecimal("25.00"), "USD"))
        ));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            return savedOrder;
        });

        // Act
        Order result = orderService.payOrder(orderId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PAID);
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenPayingNonExistentOrder() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> orderService.payOrder(orderId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Order not found");

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenPayingOrderBelowMinimum() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Order order = new Order(customerId, List.of(
            new OrderItem(productId1, 1, new Money(new BigDecimal("9.99"), "USD"))
        ));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThatThrownBy(() -> orderService.payOrder(orderId))
            .isInstanceOf(InvalidOrderStateException.class)
            .hasMessageContaining("minimum order value");

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenPayingOrderInInvalidState() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Order order = new Order(customerId, List.of(
            new OrderItem(productId1, 2, new Money(new BigDecimal("25.00"), "USD"))
        ));
        order.pay();
        order.ship();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThatThrownBy(() -> orderService.payOrder(orderId))
            .isInstanceOf(InvalidOrderStateException.class)
            .hasMessageContaining("cannot be paid");

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any(Order.class));
    }
}
