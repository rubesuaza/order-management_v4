package com.example.order_management.infrastructure.adapters.in.rest.mapper;

import com.example.order_management.application.ports.in.CreateOrderUseCase;
import com.example.order_management.domain.model.Money;
import com.example.order_management.domain.model.Order;
import com.example.order_management.domain.model.OrderItem;
import com.example.order_management.domain.model.OrderStatus;
import com.example.order_management.infrastructure.adapters.in.rest.dto.CreateOrderRequest;
import com.example.order_management.infrastructure.adapters.in.rest.dto.CreateOrderResponse;
import com.example.order_management.infrastructure.adapters.in.rest.dto.OrderResponse;
import com.example.order_management.infrastructure.adapters.in.rest.dto.PayOrderResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for OrderDtoMapper.
 */
class OrderDtoMapperTest {

    private OrderDtoMapper orderDtoMapper;
    private UUID orderId;
    private UUID customerId;
    private UUID productId1;
    private UUID productId2;

    @BeforeEach
    void setUp() {
        orderDtoMapper = new OrderDtoMapper();
        orderId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        productId1 = UUID.randomUUID();
        productId2 = UUID.randomUUID();
    }

    @Test
    void shouldConvertCreateOrderRequestToOrderItemRequests() {
        // Arrange
        List<CreateOrderRequest.OrderItemRequest> itemRequests = List.of(
            new CreateOrderRequest.OrderItemRequest(productId1, 2, new BigDecimal("25.00")),
            new CreateOrderRequest.OrderItemRequest(productId2, 3, new BigDecimal("10.00"))
        );

        // Act
        List<CreateOrderUseCase.OrderItemRequest> result = 
            orderDtoMapper.toOrderItemRequests(itemRequests);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).productId()).isEqualTo(productId1);
        assertThat(result.get(0).quantity()).isEqualTo(2);
        assertThat(result.get(0).unitPrice()).isEqualByComparingTo(new BigDecimal("25.00"));
        assertThat(result.get(0).currency()).isEqualTo("USD");
        
        assertThat(result.get(1).productId()).isEqualTo(productId2);
        assertThat(result.get(1).quantity()).isEqualTo(3);
        assertThat(result.get(1).unitPrice()).isEqualByComparingTo(new BigDecimal("10.00"));
        assertThat(result.get(1).currency()).isEqualTo("USD");
    }

    @Test
    void shouldConvertOrderToOrderResponse() {
        // Arrange
        Order order = new Order(customerId, List.of(
            new OrderItem(productId1, 2, new Money(new BigDecimal("25.00"), "USD")),
            new OrderItem(productId2, 3, new Money(new BigDecimal("10.00"), "USD"))
        ));

        // Act
        OrderResponse response = orderDtoMapper.toOrderResponse(order);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.orderId()).isEqualTo(order.getId());
        assertThat(response.customerId()).isEqualTo(customerId);
        assertThat(response.status()).isEqualTo("PENDING");
        assertThat(response.totalAmount()).isEqualByComparingTo(new BigDecimal("80.00"));
        assertThat(response.currency()).isEqualTo("USD");
        assertThat(response.items()).hasSize(2);
        assertThat(response.items().get(0).productId()).isEqualTo(productId1);
        assertThat(response.items().get(0).quantity()).isEqualTo(2);
        assertThat(response.items().get(0).unitPrice()).isEqualByComparingTo(new BigDecimal("25.00"));
    }

    @Test
    void shouldConvertOrderToOrderResponseWithPaidStatus() {
        // Arrange
        Order order = new Order(customerId, List.of(
            new OrderItem(productId1, 2, new Money(new BigDecimal("25.00"), "USD"))
        ));
        order.pay();

        // Act
        OrderResponse response = orderDtoMapper.toOrderResponse(order);

        // Assert
        assertThat(response.status()).isEqualTo("PAID");
    }

    @Test
    void shouldConvertOrderToCreateOrderResponse() {
        // Arrange
        Order order = new Order(customerId, List.of(
            new OrderItem(productId1, 2, new Money(new BigDecimal("25.00"), "USD"))
        ));

        // Act
        CreateOrderResponse response = orderDtoMapper.toCreateOrderResponse(order);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.orderId()).isEqualTo(order.getId());
        assertThat(response.status()).isEqualTo("PENDING");
        assertThat(response.totalAmount()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(response.createdAt()).isEqualTo(order.getCreatedAt());
    }

    @Test
    void shouldConvertOrderToCreateOrderResponseWithPaidStatus() {
        // Arrange
        Order order = new Order(customerId, List.of(
            new OrderItem(productId1, 2, new Money(new BigDecimal("25.00"), "USD"))
        ));
        order.pay();

        // Act
        CreateOrderResponse response = orderDtoMapper.toCreateOrderResponse(order);

        // Assert
        assertThat(response.status()).isEqualTo("PAID");
    }

    @Test
    void shouldConvertOrderToPayOrderResponse() {
        // Arrange
        Order order = new Order(customerId, List.of(
            new OrderItem(productId1, 2, new Money(new BigDecimal("25.00"), "USD"))
        ));
        order.pay();

        // Act
        PayOrderResponse response = orderDtoMapper.toPayOrderResponse(order);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.orderId()).isEqualTo(order.getId());
        assertThat(response.status()).isEqualTo("PAID");
    }

    @Test
    void shouldConvertOrderToPayOrderResponseWithPendingStatus() {
        // Arrange
        Order order = new Order(customerId, List.of(
            new OrderItem(productId1, 2, new Money(new BigDecimal("25.00"), "USD"))
        ));

        // Act
        PayOrderResponse response = orderDtoMapper.toPayOrderResponse(order);

        // Assert
        assertThat(response.status()).isEqualTo("PENDING");
    }

    @Test
    void shouldConvertOrderToPayOrderResponseWithShippedStatus() {
        // Arrange
        Order order = new Order(customerId, List.of(
            new OrderItem(productId1, 2, new Money(new BigDecimal("25.00"), "USD"))
        ));
        order.pay();
        order.ship();

        // Act
        PayOrderResponse response = orderDtoMapper.toPayOrderResponse(order);

        // Assert
        assertThat(response.status()).isEqualTo("SHIPPED");
    }

    @Test
    void shouldConvertOrderToPayOrderResponseWithDeliveredStatus() {
        // Arrange
        Order order = new Order(customerId, List.of(
            new OrderItem(productId1, 2, new Money(new BigDecimal("25.00"), "USD"))
        ));
        order.pay();
        order.ship();
        order.deliver();

        // Act
        PayOrderResponse response = orderDtoMapper.toPayOrderResponse(order);

        // Assert
        assertThat(response.status()).isEqualTo("DELIVERED");
    }

    @Test
    void shouldConvertOrderToPayOrderResponseWithCancelledStatus() {
        // Arrange
        Order order = new Order(customerId, List.of(
            new OrderItem(productId1, 2, new Money(new BigDecimal("25.00"), "USD"))
        ));
        order.cancel();

        // Act
        PayOrderResponse response = orderDtoMapper.toPayOrderResponse(order);

        // Assert
        assertThat(response.status()).isEqualTo("CANCELLED");
    }

    @Test
    void shouldConvertEmptyItemList() {
        // Arrange
        List<CreateOrderRequest.OrderItemRequest> emptyList = List.of();

        // Act
        List<CreateOrderUseCase.OrderItemRequest> result = 
            orderDtoMapper.toOrderItemRequests(emptyList);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void shouldConvertOrderWithSingleItem() {
        // Arrange
        Order order = new Order(customerId, List.of(
            new OrderItem(productId1, 1, new Money(new BigDecimal("15.50"), "USD"))
        ));

        // Act
        OrderResponse response = orderDtoMapper.toOrderResponse(order);

        // Assert
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).productId()).isEqualTo(productId1);
        assertThat(response.items().get(0).quantity()).isEqualTo(1);
        assertThat(response.items().get(0).unitPrice()).isEqualByComparingTo(new BigDecimal("15.50"));
    }
}
