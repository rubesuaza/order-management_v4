package com.example.order_management.domain.model;

import com.example.order_management.domain.exception.InvalidItemException;

import java.util.Objects;
import java.util.UUID;

/**
 * Entity representing an item within an Order. Has a unique id for reliable
 * mapping and updates in persistence.
 */
public class OrderItem {

    private final UUID id;
    private final UUID productId;
    private final int quantity;
    private final Money unitPrice;

    /**
     * Constructor for new items (id is generated). Use when creating items from commands.
     */
    public OrderItem(UUID productId, int quantity, Money unitPrice) {
        this(UUID.randomUUID(), productId, quantity, unitPrice);
    }

    /**
     * Constructor with explicit id (for reconstruction from persistence or when id is known).
     */
    public OrderItem(UUID id, UUID productId, int quantity, Money unitPrice) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        if (productId == null) {
            throw new IllegalArgumentException("ProductId cannot be null");
        }
        if (quantity <= 0) {
            throw new InvalidItemException("Quantity must be greater than zero");
        }
        if (unitPrice == null) {
            throw new IllegalArgumentException("UnitPrice cannot be null");
        }
        if (unitPrice.getAmount().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new InvalidItemException("Unit price cannot be negative");
        }
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public UUID getId() {
        return id;
    }

    public UUID getProductId() {
        return productId;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public Money getUnitPrice() {
        return unitPrice;
    }
    
    public Money calculateSubtotal() {
        return unitPrice.multiply(quantity);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(id, orderItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
