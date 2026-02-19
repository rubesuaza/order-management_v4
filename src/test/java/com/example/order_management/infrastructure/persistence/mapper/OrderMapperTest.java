package com.example.order_management.infrastructure.persistence.mapper;

import com.example.order_management.domain.model.Money;
import com.example.order_management.domain.model.Order;
import com.example.order_management.domain.model.OrderItem;
import com.example.order_management.domain.model.OrderStatus;
import com.example.order_management.infrastructure.persistence.entity.OrderEntity;
import com.example.order_management.infrastructure.persistence.entity.OrderItemEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for OrderMapper.
 */
class OrderMapperTest {

    private OrderMapper orderMapper;
    private UUID orderId;
    private UUID customerId;
    private UUID productId1;
    private UUID productId2;

    @BeforeEach
    void setUp() {
        orderMapper = new OrderMapper();
        orderId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        productId1 = UUID.randomUUID();
        productId2 = UUID.randomUUID();
    }

    @Test
    void shouldConvertDomainOrderToEntity() {
        // Arrange
        Order order = new Order(customerId, List.of(
            new OrderItem(productId1, 2, new Money(new BigDecimal("25.00"), "USD"))
        ));

        // Act
        OrderEntity entity = orderMapper.toEntity(order);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(order.getId());
        assertThat(entity.getCustomerId()).isEqualTo(customerId);
        assertThat(entity.getStatus()).isEqualTo("PENDING");
        assertThat(entity.getTotalAmount()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(entity.getCurrency()).isEqualTo("USD");
        assertThat(entity.getItems()).hasSize(1);
        assertThat(entity.getItems().get(0).getProductId()).isEqualTo(productId1);
        assertThat(entity.getItems().get(0).getQuantity()).isEqualTo(2);
        assertThat(entity.getItems().get(0).getUnitPrice()).isEqualByComparingTo(new BigDecimal("25.00"));
    }

    @Test
    void shouldConvertDomainOrderToEntityWithMultipleItems() {
        // Arrange
        Order order = new Order(customerId, List.of(
            new OrderItem(productId1, 2, new Money(new BigDecimal("25.00"), "USD")),
            new OrderItem(productId2, 3, new Money(new BigDecimal("10.00"), "USD"))
        ));

        // Act
        OrderEntity entity = orderMapper.toEntity(order);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getItems()).hasSize(2);
        assertThat(entity.getTotalAmount()).isEqualByComparingTo(new BigDecimal("80.00"));
    }

    @Test
    void shouldConvertEntityToDomainOrder() {
        // Arrange
        LocalDateTime createdAt = LocalDateTime.now();
        OrderEntity entity = new OrderEntity(orderId, customerId, "PENDING", 
            new BigDecimal("50.00"), "USD", createdAt);
        
        OrderItemEntity itemEntity = new OrderItemEntity(
            UUID.randomUUID(), entity, productId1, 2, new BigDecimal("25.00")
        );
        entity.setItems(List.of(itemEntity));

        // Act
        Order order = orderMapper.toDomain(entity);

        // Assert
        assertThat(order).isNotNull();
        assertThat(order.getId()).isEqualTo(orderId);
        assertThat(order.getCustomerId()).isEqualTo(customerId);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getTotalAmount().getAmount()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(order.getTotalAmount().getCurrency()).isEqualTo("USD");
        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getItems().get(0).getProductId()).isEqualTo(productId1);
        assertThat(order.getItems().get(0).getQuantity()).isEqualTo(2);
        assertThat(order.getItems().get(0).getUnitPrice().getAmount()).isEqualByComparingTo(new BigDecimal("25.00"));
    }

    @Test
    void shouldConvertEntityToDomainOrderWithPaidStatus() {
        // Arrange
        LocalDateTime createdAt = LocalDateTime.now();
        OrderEntity entity = new OrderEntity(orderId, customerId, "PAID", 
            new BigDecimal("50.00"), "USD", createdAt);
        
        OrderItemEntity itemEntity = new OrderItemEntity(
            UUID.randomUUID(), entity, productId1, 2, new BigDecimal("25.00")
        );
        entity.setItems(List.of(itemEntity));

        // Act
        Order order = orderMapper.toDomain(entity);

        // Assert
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void shouldUpdateExistingEntityWhenProvided() {
        // Arrange
        Order order = new Order(customerId, List.of(
            new OrderItem(productId1, 2, new Money(new BigDecimal("25.00"), "USD"))
        ));
        order.pay();

        LocalDateTime createdAt = LocalDateTime.now();
        OrderEntity existingEntity = new OrderEntity(orderId, customerId, "PENDING", 
            new BigDecimal("50.00"), "USD", createdAt);
        
        OrderItemEntity existingItem = new OrderItemEntity(
            UUID.randomUUID(), existingEntity, productId1, 2, new BigDecimal("25.00")
        );
        existingEntity.setItems(List.of(existingItem));

        // Act
        OrderEntity updatedEntity = orderMapper.toEntity(order, existingEntity);

        // Assert
        assertThat(updatedEntity).isSameAs(existingEntity);
        assertThat(updatedEntity.getStatus()).isEqualTo("PAID");
        assertThat(updatedEntity.getId()).isEqualTo(orderId);
        assertThat(updatedEntity.getCustomerId()).isEqualTo(customerId);
        assertThat(updatedEntity.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void shouldUpdateExistingItemWhenProductIdAndQuantityMatch() {
        // Arrange
        Order order = new Order(customerId, List.of(
            new OrderItem(productId1, 2, new Money(new BigDecimal("30.00"), "USD"))
        ));

        LocalDateTime createdAt = LocalDateTime.now();
        OrderEntity existingEntity = new OrderEntity(orderId, customerId, "PENDING", 
            new BigDecimal("50.00"), "USD", createdAt);
        
        UUID existingItemId = UUID.randomUUID();
        OrderItemEntity existingItem = new OrderItemEntity(
            existingItemId, existingEntity, productId1, 2, new BigDecimal("25.00")
        );
        existingEntity.setItems(List.of(existingItem));

        // Act
        OrderEntity updatedEntity = orderMapper.toEntity(order, existingEntity);

        // Assert
        assertThat(updatedEntity.getItems()).hasSize(1);
        assertThat(updatedEntity.getItems().get(0).getId()).isEqualTo(existingItemId);
        assertThat(updatedEntity.getItems().get(0).getUnitPrice()).isEqualByComparingTo(new BigDecimal("30.00"));
    }

    @Test
    void shouldCreateNewItemWhenProductIdOrQuantityDoesNotMatch() {
        // Arrange
        Order order = new Order(customerId, List.of(
            new OrderItem(productId2, 3, new Money(new BigDecimal("10.00"), "USD"))
        ));

        LocalDateTime createdAt = LocalDateTime.now();
        OrderEntity existingEntity = new OrderEntity(orderId, customerId, "PENDING", 
            new BigDecimal("50.00"), "USD", createdAt);
        
        OrderItemEntity existingItem = new OrderItemEntity(
            UUID.randomUUID(), existingEntity, productId1, 2, new BigDecimal("25.00")
        );
        existingEntity.setItems(List.of(existingItem));

        // Act
        OrderEntity updatedEntity = orderMapper.toEntity(order, existingEntity);

        // Assert
        assertThat(updatedEntity.getItems()).hasSize(1);
        assertThat(updatedEntity.getItems().get(0).getProductId()).isEqualTo(productId2);
        assertThat(updatedEntity.getItems().get(0).getQuantity()).isEqualTo(3);
    }

    @Test
    void shouldConvertEntityWithShippedStatus() {
        // Arrange
        LocalDateTime createdAt = LocalDateTime.now();
        OrderEntity entity = new OrderEntity(orderId, customerId, "SHIPPED", 
            new BigDecimal("50.00"), "USD", createdAt);
        
        OrderItemEntity itemEntity = new OrderItemEntity(
            UUID.randomUUID(), entity, productId1, 2, new BigDecimal("25.00")
        );
        entity.setItems(List.of(itemEntity));

        // Act
        Order order = orderMapper.toDomain(entity);

        // Assert
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    void shouldConvertEntityWithDeliveredStatus() {
        // Arrange
        LocalDateTime createdAt = LocalDateTime.now();
        OrderEntity entity = new OrderEntity(orderId, customerId, "DELIVERED", 
            new BigDecimal("50.00"), "USD", createdAt);
        
        OrderItemEntity itemEntity = new OrderItemEntity(
            UUID.randomUUID(), entity, productId1, 2, new BigDecimal("25.00")
        );
        entity.setItems(List.of(itemEntity));

        // Act
        Order order = orderMapper.toDomain(entity);

        // Assert
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    void shouldConvertEntityWithCancelledStatus() {
        // Arrange
        LocalDateTime createdAt = LocalDateTime.now();
        OrderEntity entity = new OrderEntity(orderId, customerId, "CANCELLED", 
            new BigDecimal("50.00"), "USD", createdAt);
        
        OrderItemEntity itemEntity = new OrderItemEntity(
            UUID.randomUUID(), entity, productId1, 2, new BigDecimal("25.00")
        );
        entity.setItems(List.of(itemEntity));

        // Act
        Order order = orderMapper.toDomain(entity);

        // Assert
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }
}
