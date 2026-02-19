package com.example.order_management.domain.model;

import com.example.order_management.domain.exception.CurrencyMismatchException;
import com.example.order_management.domain.exception.InvalidOrderStateException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Test
    void shouldCreateOrderWithValidItems() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        List<OrderItem> items = List.of(
            new OrderItem(productId, 2, new Money(new BigDecimal("25.00")))
        );
        
        Order order = new Order(customerId, items);
        
        assertThat(order.getCustomerId()).isEqualTo(customerId);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getTotalAmount().getAmount()).isEqualByComparingTo(new BigDecimal("50.00"));
    }

    @Test
    void shouldCalculateTotalAmountCorrectly() {
        UUID customerId = UUID.randomUUID();
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();
        List<OrderItem> items = List.of(
            new OrderItem(productId1, 2, new Money(new BigDecimal("25.00"))),
            new OrderItem(productId2, 3, new Money(new BigDecimal("10.00")))
        );
        
        Order order = new Order(customerId, items);
        
        // Total: (2 * 25.00) + (3 * 10.00) = 50.00 + 30.00 = 80.00
        assertThat(order.getTotalAmount().getAmount()).isEqualByComparingTo(new BigDecimal("80.00"));
    }

    @Test
    void shouldThrowExceptionWhenOrderIsEmpty() {
        UUID customerId = UUID.randomUUID();
        List<OrderItem> emptyItems = new ArrayList<>();
        
        assertThatThrownBy(() -> new Order(customerId, emptyItems))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Order must have at least one item");
    }

    @Test
    void shouldThrowExceptionWhenItemsListIsNull() {
        UUID customerId = UUID.randomUUID();
        
        assertThatThrownBy(() -> new Order(customerId, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowExceptionWhenItemsHaveDifferentCurrencies() {
        UUID customerId = UUID.randomUUID();
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();
        List<OrderItem> items = List.of(
            new OrderItem(productId1, 1, new Money(new BigDecimal("25.00"), "USD")),
            new OrderItem(productId2, 1, new Money(new BigDecimal("20.00"), "EUR"))
        );
        
        assertThatThrownBy(() -> new Order(customerId, items))
                .isInstanceOf(CurrencyMismatchException.class)
                .hasMessageContaining("All items must have the same currency");
    }

    @Test
    void shouldPayOrderWhenTotalIsGreaterThanMinimum() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        List<OrderItem> items = List.of(
            new OrderItem(productId, 1, new Money(new BigDecimal("25.00")))
        );
        
        Order order = new Order(customerId, items);
        order.pay();
        
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void shouldPayOrderWhenTotalEqualsMinimum() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        List<OrderItem> items = List.of(
            new OrderItem(productId, 1, new Money(new BigDecimal("10.00")))
        );
        
        Order order = new Order(customerId, items);
        order.pay();
        
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void shouldThrowExceptionWhenPayingOrderBelowMinimum() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        List<OrderItem> items = List.of(
            new OrderItem(productId, 1, new Money(new BigDecimal("9.99")))
        );
        
        Order order = new Order(customerId, items);
        
        assertThatThrownBy(() -> order.pay())
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("minimum order value");
    }

    @Test
    void shouldCancelPendingOrder() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        List<OrderItem> items = List.of(
            new OrderItem(productId, 1, new Money(new BigDecimal("25.00")))
        );
        
        Order order = new Order(customerId, items);
        order.cancel();
        
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void shouldCancelPaidOrder() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        List<OrderItem> items = List.of(
            new OrderItem(productId, 1, new Money(new BigDecimal("25.00")))
        );
        
        Order order = new Order(customerId, items);
        order.pay();
        order.cancel();
        
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void shouldThrowExceptionWhenCancellingShippedOrder() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        List<OrderItem> items = List.of(
            new OrderItem(productId, 1, new Money(new BigDecimal("25.00")))
        );
        
        Order order = new Order(customerId, items);
        order.pay();
        order.ship();
        
        assertThatThrownBy(() -> order.cancel())
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("cannot be cancelled");
    }

    @Test
    void shouldThrowExceptionWhenCancellingDeliveredOrder() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        List<OrderItem> items = List.of(
            new OrderItem(productId, 1, new Money(new BigDecimal("25.00")))
        );
        
        Order order = new Order(customerId, items);
        order.pay();
        order.ship();
        order.deliver();
        
        assertThatThrownBy(() -> order.cancel())
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("cannot be cancelled");
    }

    @Test
    void shouldShipPaidOrder() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        List<OrderItem> items = List.of(
            new OrderItem(productId, 1, new Money(new BigDecimal("25.00")))
        );
        
        Order order = new Order(customerId, items);
        order.pay();
        order.ship();
        
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    void shouldThrowExceptionWhenShippingPendingOrder() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        List<OrderItem> items = List.of(
            new OrderItem(productId, 1, new Money(new BigDecimal("25.00")))
        );
        
        Order order = new Order(customerId, items);
        
        assertThatThrownBy(() -> order.ship())
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("can only be shipped");
    }

    @Test
    void shouldDeliverShippedOrder() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        List<OrderItem> items = List.of(
            new OrderItem(productId, 1, new Money(new BigDecimal("25.00")))
        );
        
        Order order = new Order(customerId, items);
        order.pay();
        order.ship();
        order.deliver();
        
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    void shouldHaveCreatedAtTimestamp() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        List<OrderItem> items = List.of(
            new OrderItem(productId, 1, new Money(new BigDecimal("25.00")))
        );
        
        Order order = new Order(customerId, items);
        
        assertThat(order.getCreatedAt()).isNotNull();
        assertThat(order.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void shouldHaveUniqueOrderId() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        List<OrderItem> items = List.of(
            new OrderItem(productId, 1, new Money(new BigDecimal("25.00")))
        );
        
        Order order1 = new Order(customerId, items);
        Order order2 = new Order(customerId, items);
        
        assertThat(order1.getId()).isNotNull();
        assertThat(order2.getId()).isNotNull();
        assertThat(order1.getId()).isNotEqualTo(order2.getId());
    }
}
