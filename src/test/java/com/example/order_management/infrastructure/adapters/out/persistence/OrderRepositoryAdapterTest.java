package com.example.order_management.infrastructure.adapters.out.persistence;

import com.example.order_management.domain.model.Money;
import com.example.order_management.domain.model.Order;
import com.example.order_management.domain.model.OrderItem;
import com.example.order_management.domain.model.OrderStatus;
import com.example.order_management.infrastructure.persistence.entity.OrderEntity;
import com.example.order_management.infrastructure.persistence.mapper.OrderMapper;
import com.example.order_management.infrastructure.persistence.repository.OrderJpaRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderRepositoryAdapter.
 */
@ExtendWith(MockitoExtension.class)
class OrderRepositoryAdapterTest {
    
    @Mock
    private OrderJpaRepository jpaRepository;
    
    @Mock
    private OrderMapper mapper;
    
    @InjectMocks
    private OrderRepositoryAdapter adapter;
    
    private UUID orderId;
    private UUID customerId;
    private UUID productId;
    
    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        productId = UUID.randomUUID();
    }
    
    @Test
    void shouldSaveNewOrder() {
        // Given
        OrderItem item = new OrderItem(productId, 2, new Money(new BigDecimal("15.50")));
        Order order = new Order(customerId, List.of(item));
        OrderEntity entity = new OrderEntity(
            orderId, customerId, "PENDING", 
            new BigDecimal("31.00"), "USD", order.getCreatedAt()
        );
        OrderEntity savedEntity = new OrderEntity(
            orderId, customerId, "PENDING",
            new BigDecimal("31.00"), "USD", order.getCreatedAt()
        );
        
        when(jpaRepository.findById(any())).thenReturn(Optional.empty());
        when(mapper.toEntity(order, null)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(order);
        
        // When
        Order result = adapter.save(order);
        
        // Then
        assertThat(result).isNotNull();
        verify(jpaRepository).save(entity);
    }
    
    @Test
    void shouldFindOrderById() {
        // Given
        OrderEntity entity = new OrderEntity(
            orderId, customerId, "PENDING",
            new BigDecimal("31.00"), "USD", java.time.LocalDateTime.now()
        );
        Order order = new Order(customerId, List.of(
            new OrderItem(productId, 2, new Money(new BigDecimal("15.50")))
        ));
        
        when(jpaRepository.findById(orderId)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(order);
        
        // When
        Optional<Order> result = adapter.findById(orderId);
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(order);
    }
    
    @Test
    void shouldReturnEmptyWhenOrderNotFound() {
        // Given
        when(jpaRepository.findById(orderId)).thenReturn(Optional.empty());
        
        // When
        Optional<Order> result = adapter.findById(orderId);
        
        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    void shouldCheckOrderExists() {
        // Given
        when(jpaRepository.existsById(orderId)).thenReturn(true);
        
        // When
        boolean exists = adapter.existsById(orderId);
        
        // Then
        assertThat(exists).isTrue();
        verify(jpaRepository).existsById(orderId);
    }
}
