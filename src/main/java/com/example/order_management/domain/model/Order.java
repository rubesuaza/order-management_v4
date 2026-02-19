package com.example.order_management.domain.model;

import com.example.order_management.domain.exception.CurrencyMismatchException;
import com.example.order_management.domain.exception.InvalidOrderStateException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate Root representing an Order.
 * Enforces business rules and invariants.
 */
public class Order {
    
    private final UUID id;
    private OrderStatus status;
    private final LocalDateTime createdAt;
    private final List<OrderItem> items;
    private final UUID customerId;
    private Money totalAmount;
    
    private static final BigDecimal MINIMUM_ORDER_VALUE = new BigDecimal("10.00");
    
    public Order(UUID customerId, List<OrderItem> items) {
        if (customerId == null) {
            throw new IllegalArgumentException("CustomerId cannot be null");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
        
        this.id = UUID.randomUUID();
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.customerId = customerId;
        this.items = new ArrayList<>(items);
        
        validateCurrencyConsistency();
        this.totalAmount = calculateTotal();
    }
    
    public UUID getId() {
        return id;
    }
    
    public OrderStatus getStatus() {
        return status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public List<OrderItem> getItems() {
        return new ArrayList<>(items);
    }
    
    public UUID getCustomerId() {
        return customerId;
    }
    
    public Money getTotalAmount() {
        return totalAmount;
    }
    
    public void pay() {
        if (status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException(
                String.format("Order in status %s cannot be paid", status)
            );
        }
        
        if (totalAmount.getAmount().compareTo(MINIMUM_ORDER_VALUE) < 0) {
            throw new InvalidOrderStateException(
                String.format("Order total %.2f is below minimum order value of %.2f USD", 
                    totalAmount.getAmount().doubleValue(), MINIMUM_ORDER_VALUE.doubleValue())
            );
        }
        
        this.status = OrderStatus.PAID;
    }
    
    public void cancel() {
        if (status == OrderStatus.SHIPPED || status == OrderStatus.DELIVERED) {
            throw new InvalidOrderStateException(
                String.format("Order in status %s cannot be cancelled", status)
            );
        }
        
        this.status = OrderStatus.CANCELLED;
    }
    
    public void ship() {
        if (status != OrderStatus.PAID) {
            throw new InvalidOrderStateException(
                String.format("Order can only be shipped if it is in PAID status, current status: %s", status)
            );
        }
        
        this.status = OrderStatus.SHIPPED;
    }
    
    public void deliver() {
        if (status != OrderStatus.SHIPPED) {
            throw new InvalidOrderStateException(
                String.format("Order can only be delivered if it is in SHIPPED status, current status: %s", status)
            );
        }
        
        this.status = OrderStatus.DELIVERED;
    }
    
    private Money calculateTotal() {
        Money total = new Money(BigDecimal.ZERO, items.get(0).getUnitPrice().getCurrency());
        
        for (OrderItem item : items) {
            total = total.add(item.calculateSubtotal());
        }
        
        return total;
    }
    
    private void validateCurrencyConsistency() {
        if (items.isEmpty()) {
            return;
        }
        
        String firstCurrency = items.get(0).getUnitPrice().getCurrency();
        
        for (OrderItem item : items) {
            if (!item.getUnitPrice().getCurrency().equals(firstCurrency)) {
                throw new CurrencyMismatchException(
                    String.format("All items must have the same currency. Found %s and %s",
                        firstCurrency, item.getUnitPrice().getCurrency())
                );
            }
        }
    }
    
    /**
     * Factory method to reconstruct an Order from persistence.
     * Public to be used by infrastructure adapters.
     */
    public static Order reconstruct(UUID id, UUID customerId, List<OrderItem> items, 
                            OrderStatus status, LocalDateTime createdAt) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        if (customerId == null) {
            throw new IllegalArgumentException("CustomerId cannot be null");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("CreatedAt cannot be null");
        }
        
        Order order = new Order(customerId, items);
        try {
            java.lang.reflect.Field idField = Order.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(order, id);
            
            java.lang.reflect.Field createdAtField = Order.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(order, createdAt);
            
            java.lang.reflect.Field statusField = Order.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(order, status);
        } catch (Exception e) {
            throw new RuntimeException("Failed to reconstruct Order from persistence", e);
        }
        
        return order;
    }
}
