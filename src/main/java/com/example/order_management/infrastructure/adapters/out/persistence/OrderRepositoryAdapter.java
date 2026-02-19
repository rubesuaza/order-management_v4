package com.example.order_management.infrastructure.adapters.out.persistence;

import com.example.order_management.application.ports.out.OrderRepositoryPort;
import com.example.order_management.domain.model.Order;
import com.example.order_management.infrastructure.persistence.entity.OrderEntity;
import com.example.order_management.infrastructure.persistence.mapper.OrderMapper;
import com.example.order_management.infrastructure.persistence.repository.OrderJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Adapter implementing OrderRepositoryPort using JPA.
 */
@Component
public class OrderRepositoryAdapter implements OrderRepositoryPort {
    
    private final OrderJpaRepository jpaRepository;
    private final OrderMapper mapper;
    
    public OrderRepositoryAdapter(OrderJpaRepository jpaRepository, OrderMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }
    
    @Override
    public Order save(Order order) {
        // Check if order exists to preserve item IDs
        OrderEntity existingEntity = null;
        if (order.getId() != null) {
            existingEntity = jpaRepository.findById(order.getId()).orElse(null);
        }
        
        OrderEntity entity = mapper.toEntity(order, existingEntity);
        OrderEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<Order> findById(UUID orderId) {
        return jpaRepository.findById(orderId)
            .map(mapper::toDomain);
    }
    
    @Override
    public boolean existsById(UUID orderId) {
        return jpaRepository.existsById(orderId);
    }
}
