package com.example.order_management.domain.model;

import com.example.order_management.domain.exception.InvalidItemException;

import java.util.Objects;
import java.util.UUID;

/**
 * Entity representing an item within an Order.
 */
public class OrderItem {
    
    private final UUID productId;
    private final int quantity;
    private final Money unitPrice;
    
    public OrderItem(UUID productId, int quantity, Money unitPrice) {
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
        
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
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
        return quantity == orderItem.quantity &&
               Objects.equals(productId, orderItem.productId) &&
               Objects.equals(unitPrice, orderItem.unitPrice);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(productId, quantity, unitPrice);
    }
}
