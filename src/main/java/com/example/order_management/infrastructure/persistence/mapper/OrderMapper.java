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
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper between Domain Order aggregate and Infrastructure OrderEntity.
 * Provides distinct methods for creating new entities and updating existing ones.
 */
@Component
public class OrderMapper {

    /**
     * Converts a Domain Order to a new OrderEntity (for creation).
     * Uses domain OrderItem ids for OrderItemEntity ids.
     */
    public OrderEntity toNewEntity(Order order) {
        OrderEntity entity = new OrderEntity(
            order.getId(),
            order.getCustomerId(),
            order.getStatus().name(),
            order.getTotalAmount().getAmount(),
            order.getTotalAmount().getCurrency(),
            order.getCreatedAt()
        );
        List<OrderItemEntity> itemEntities = order.getItems().stream()
            .map(item -> new OrderItemEntity(
                item.getId(),
                entity,
                item.getProductId(),
                item.getQuantity(),
                item.getUnitPrice().getAmount()
            ))
            .toList();
        entity.setItems(itemEntities);
        return entity;
    }

    /**
     * Updates an existing OrderEntity with state from the Domain Order.
     * Matches OrderItems to OrderItemEntities by id for correct updates.
     */
    public void updateEntity(Order order, OrderEntity existingEntity) {
        existingEntity.setStatus(order.getStatus().name());
        existingEntity.setTotalAmount(order.getTotalAmount().getAmount());
        existingEntity.setCurrency(order.getTotalAmount().getCurrency());

        Map<UUID, OrderItemEntity> existingItemsById = existingEntity.getItems() != null
            ? existingEntity.getItems().stream().collect(Collectors.toMap(OrderItemEntity::getId, ei -> ei))
            : Map.of();

        List<OrderItemEntity> itemEntities = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            OrderItemEntity existingItem = existingItemsById.get(item.getId());
            if (existingItem != null) {
                existingItem.setUnitPrice(item.getUnitPrice().getAmount());
                existingItem.setQuantity(item.getQuantity());
                itemEntities.add(existingItem);
            } else {
                itemEntities.add(new OrderItemEntity(
                    item.getId(),
                    existingEntity,
                    item.getProductId(),
                    item.getQuantity(),
                    item.getUnitPrice().getAmount()
                ));
            }
        }
        existingEntity.setItems(itemEntities);
    }

    /**
     * Converts an OrderEntity to a Domain Order aggregate.
     */
    public Order toDomain(OrderEntity entity) {
        List<OrderItem> domainItems = entity.getItems().stream()
            .map(item -> new OrderItem(
                item.getId(),
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
