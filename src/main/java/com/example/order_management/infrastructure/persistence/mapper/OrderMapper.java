package com.example.order_management.infrastructure.persistence.mapper;

import com.example.order_management.domain.model.Money;
import com.example.order_management.domain.model.Order;
import com.example.order_management.domain.model.OrderItem;
import com.example.order_management.domain.model.OrderStatus;
import com.example.order_management.infrastructure.persistence.entity.OrderEntity;
import com.example.order_management.infrastructure.persistence.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Mapper between Domain Order aggregate and Infrastructure OrderEntity.
 */
@Component
public class OrderMapper {
    
    /**
     * Converts a Domain Order to an OrderEntity.
     * If the order already exists in the database, preserves existing item IDs.
     */
    public OrderEntity toEntity(Order order, OrderEntity existingEntity) {
        OrderEntity entity;
        if (existingEntity != null) {
            // Update existing entity
            entity = existingEntity;
            entity.setStatus(order.getStatus().name());
            entity.setTotalAmount(order.getTotalAmount().getAmount());
            entity.setCurrency(order.getTotalAmount().getCurrency());
            // Note: customerId and createdAt should not change
        } else {
            // Create new entity
            entity = new OrderEntity(
                order.getId(),
                order.getCustomerId(),
                order.getStatus().name(),
                order.getTotalAmount().getAmount(),
                order.getTotalAmount().getCurrency(),
                order.getCreatedAt()
            );
        }
        
        // Map items - for new orders, generate IDs; for existing, preserve if possible
        List<OrderItemEntity> itemEntities = order.getItems().stream()
            .map(item -> {
                // Try to find existing item by productId and quantity
                OrderItemEntity existingItem = null;
                if (existingEntity != null && existingEntity.getItems() != null) {
                    existingItem = existingEntity.getItems().stream()
                        .filter(ei -> ei.getProductId().equals(item.getProductId()) 
                                   && ei.getQuantity().equals(item.getQuantity()))
                        .findFirst()
                        .orElse(null);
                }
                
                if (existingItem != null) {
                    // Update existing item
                    existingItem.setUnitPrice(item.getUnitPrice().getAmount());
                    return existingItem;
                } else {
                    // Create new item
                    return new OrderItemEntity(
                        UUID.randomUUID(),
                        entity,
                        item.getProductId(),
                        item.getQuantity(),
                        item.getUnitPrice().getAmount()
                    );
                }
            })
            .toList();

        entity.setItems(itemEntities);
        return entity;
    }
    
    /**
     * Converts a Domain Order to an OrderEntity (creates new entity).
     */
    public OrderEntity toEntity(Order order) {
        return toEntity(order, null);
    }
    
    /**
     * Converts an OrderEntity to a Domain Order aggregate.
     */
    public Order toDomain(OrderEntity entity) {
        List<OrderItem> domainItems = entity.getItems().stream()
            .map(item -> new OrderItem(
                item.getProductId(),
                item.getQuantity(),
                new Money(item.getUnitPrice(), entity.getCurrency())
            ))
            .toList();
        
        Money totalAmount = new Money(entity.getTotalAmount(), entity.getCurrency());
        return Order.reconstruct(
            entity.getId(),
            entity.getCustomerId(),
            domainItems,
            OrderStatus.valueOf(entity.getStatus()),
            entity.getCreatedAt(),
            totalAmount
        );
    }
}
